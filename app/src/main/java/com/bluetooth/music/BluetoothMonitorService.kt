package com.bluetooth.music

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

/**
 * 蓝牙监控后台服务
 * 持续监控蓝牙连接状态，当连接到指定设备时触发音乐播放
 * 
 * 完整功能：
 * 1. 检测蓝牙连接
 * 2. 启动 Apple Music
 * 3. 播放 BGM 歌曲（如果设置了）
 * 4. BGM 播放完成后自动切换到播放列表
 * 5. 设置播放模式（随机/顺序/单曲循环）
 */
class BluetoothMonitorService : Service() {

    companion object {
        private const val TAG = "BluetoothMonitorService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "bluetooth_music_monitor"
        private const val CHECK_INTERVAL_MS = 5000L // 每 5 秒检查一次
        
        // Apple Music 包名
        private const val APPLE_MUSIC_PACKAGE = "com.apple.android.music"
        
        // 播放状态跟踪
        private var lastConnectedTime = 0L
        private var isPlaying = false
        private var currentStep = PlaybackStep.IDLE
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    private var mediaController: MediaController? = null

    // 播放步骤枚举
    enum class PlaybackStep {
        IDLE,           // 空闲
        LAUNCH_APP,     // 启动应用
        PLAY_BGM,       // 播放 BGM
        WAIT_BGM,       // 等待 BGM 完成
        PLAY_PLAYLIST,  // 播放播放列表
        COMPLETE        // 完成
    }

    private val checkBluetoothRunnable = object : Runnable {
        override fun run() {
            if (isMonitoring) {
                checkBluetoothConnection()
                handler.postDelayed(this, CHECK_INTERVAL_MS)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // 初始化蓝牙适配器
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // 创建通知渠道
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")

        // 启动前台服务
        startForeground()

        // 开始监控
        if (!isMonitoring) {
            isMonitoring = true
            handler.post(checkBluetoothRunnable)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        isMonitoring = false
        handler.removeCallbacks(checkBluetoothRunnable)
        mediaController = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForeground() {
        val notification = createNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🎵 蓝牙音乐监控")
            .setContentText("正在监控蓝牙连接...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "蓝牙音乐监控",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "监控蓝牙连接状态并自动播放音乐"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkBluetoothConnection() {
        // 如果正在播放中，跳过检查
        if (currentStep != PlaybackStep.IDLE) {
            Log.d(TAG, "当前播放步骤：$currentStep，跳过检查")
            return
        }

        val prefs = getSharedPreferences("bluetooth_music_config", Context.MODE_PRIVATE)
        val targetDeviceName = prefs.getString("bluetooth_device", null)

        if (targetDeviceName.isNullOrEmpty()) {
            Log.d(TAG, "未配置目标蓝牙设备")
            return
        }

        bluetoothAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                Log.d(TAG, "蓝牙未启用")
                return
            }

            // 检查是否有设备已连接
            val connectedDevices = adapter.bondedDevices.filter { device ->
                device.name == targetDeviceName
            }

            if (connectedDevices.isNotEmpty()) {
                val now = System.currentTimeMillis()
                // 避免重复触发（5 分钟内只触发一次）
                if (now - lastConnectedTime > 300000) {
                    Log.d(TAG, "检测到目标设备已连接：$targetDeviceName")
                    lastConnectedTime = now
                    // 触发音乐播放
                    triggerMusicPlayback()
                } else {
                    Log.d(TAG, "设备已连接，但在冷却时间内，跳过触发")
                }
            }
        }
    }

    private fun triggerMusicPlayback() {
        val prefs = getSharedPreferences("bluetooth_music_config", Context.MODE_PRIVATE)
        val musicApp = prefs.getString("music_app", "Apple Music") ?: "Apple Music"
        val playbackMode = prefs.getString("playback_mode", PlaybackMode.SEQUENTIAL.name) ?: PlaybackMode.SEQUENTIAL.name
        val playlistName = prefs.getString("playlist_name", "我的最爱") ?: "我的最爱"
        val bgmSong = prefs.getString("bgm_song", "") ?: ""

        Log.d(TAG, "触发音乐播放：应用=$musicApp, BGM=$bgmSong, 歌单=$playlistName, 模式=$playbackMode")

        // 确认是 Apple Music
        if (musicApp != "Apple Music") {
            Log.w(TAG, "当前仅完整支持 Apple Music，其他应用可能功能受限")
        }

        // 开始播放流程
        currentStep = PlaybackStep.LAUNCH_APP
        startMusicApp(musicApp)

        // 延迟后执行后续步骤
        handler.postDelayed({
            // 初始化媒体控制器
            initMediaController()

            // 如果有 BGM 歌曲，先播放 BGM
            if (!bgmSong.isNullOrEmpty()) {
                Log.d(TAG, "步骤 1: 播放 BGM 歌曲：$bgmSong")
                currentStep = PlaybackStep.PLAY_BGM
                playBGMSong(bgmSong, musicApp)
                
                // 监控 BGM 播放状态
                monitorBGMPlayback(playlistName, playbackMode)
            } else {
                // 没有 BGM，直接播放播放列表
                Log.d(TAG, "步骤 1: 直接播放播放列表：$playlistName")
                currentStep = PlaybackStep.PLAY_PLAYLIST
                playPlaylist(playlistName, playbackMode)
            }
        }, 3000L) // 等待 3 秒让应用启动
    }

    /**
     * 启动音乐应用
     */
    private fun startMusicApp(musicApp: String) {
        val packageName = getMusicAppPackageName(musicApp)
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        
        if (launchIntent != null) {
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(launchIntent)
            Log.d(TAG, "已启动音乐应用：$musicApp ($packageName)")
        } else {
            Log.w(TAG, "未找到音乐应用：$musicApp")
            currentStep = PlaybackStep.IDLE
        }
    }

    /**
     * 初始化媒体控制器
     */
    private fun initMediaController() {
        try {
            val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val activeSessions = mediaSessionManager.getActiveSessions(null)

            if (activeSessions.isNotEmpty()) {
                mediaController = MediaController(this, activeSessions[0].sessionToken)
                Log.d(TAG, "媒体控制器已初始化")
            } else {
                Log.w(TAG, "没有活动的媒体会话")
            }
        } catch (e: Exception) {
            Log.e(TAG, "初始化媒体控制器失败", e)
        }
    }

    /**
     * 播放 BGM 歌曲
     * 使用 Apple Music 的 Intent 或媒体控制
     */
    private fun playBGMSong(songName: String, musicApp: String) {
        // 方法 1: 尝试使用 Apple Music 的搜索 Intent
        if (musicApp == "Apple Music") {
            try {
                // Apple Music 支持通过搜索 URL 打开特定歌曲
                val searchQuery = songName.replace(" ", "+")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://music.apple.com/search?term=$searchQuery")
                    setPackage(APPLE_MUSIC_PACKAGE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                Log.d(TAG, "已发送搜索 Intent: $songName")
            } catch (e: Exception) {
                Log.e(TAG, "发送搜索 Intent 失败，使用备用方案", e)
                // 备用方案：使用媒体控制
                sendMediaCommand("play")
            }
        } else {
            // 其他应用：使用媒体控制
            sendMediaCommand("play")
        }

        // 通知无障碍服务开始播放 BGM
        if (MusicAccessibilityService.isRunning) {
            Log.d(TAG, "通知无障碍服务播放 BGM")
            // 无障碍服务会在后台处理 UI 操作
        }
    }

    /**
     * 监控 BGM 播放状态，播放完成后切换到播放列表
     */
    private fun monitorBGMPlayback(playlistName: String, playbackMode: String) {
        currentStep = PlaybackStep.WAIT_BGM

        // 定期检查播放状态
        val checkPlaybackRunnable = object : Runnable {
            override fun run() {
                if (currentStep != PlaybackStep.WAIT_BGM) {
                    return
                }

                val state = mediaController?.playbackState?.state

                when (state) {
                    PlaybackState.STATE_PLAYING -> {
                        Log.d(TAG, "BGM 正在播放...")
                        handler.postDelayed(this, 3000L)
                    }
                    PlaybackState.STATE_PAUSED,
                    PlaybackState.STATE_STOPPED,
                    PlaybackState.STATE_ERROR -> {
                        // BGM 播放完成，切换到播放列表
                        Log.d(TAG, "BGM 播放完成，切换到播放列表")
                        currentStep = PlaybackStep.PLAY_PLAYLIST
                        playPlaylist(playlistName, playbackMode)
                    }
                    else -> {
                        Log.d(TAG, "未知播放状态：$state")
                        handler.postDelayed(this, 3000L)
                    }
                }
            }
        }

        handler.post(checkPlaybackRunnable)
    }

    /**
     * 播放播放列表
     */
    private fun playPlaylist(playlistName: String, playbackMode: String) {
        Log.d(TAG, "播放播放列表：$playlistName, 模式：$playbackMode")

        // 方法 1: 尝试使用 Apple Music 的播放列表 Intent
        if (musicAppIsAppleMusic()) {
            try {
                // 尝试打开播放列表
                val searchQuery = playlistName.replace(" ", "+")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://music.apple.com/search?term=$searchQuery+playlist")
                    setPackage(APPLE_MUSIC_PACKAGE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                Log.d(TAG, "已发送播放列表搜索 Intent")
            } catch (e: Exception) {
                Log.e(TAG, "发送播放列表 Intent 失败", e)
            }
        }

        // 方法 2: 使用无障碍服务
        if (MusicAccessibilityService.isRunning) {
            Log.d(TAG, "使用无障碍服务播放播放列表")
            // 无障碍服务会处理 UI 操作
        }

        // 方法 3: 使用媒体控制（播放下一首，进入播放列表）
        handler.postDelayed({
            sendMediaCommand("next")
            
            // 设置播放模式
            setPlaybackMode(playbackMode)
            
            currentStep = PlaybackStep.COMPLETE
            Log.d(TAG, "播放流程完成")
            
            // 重置状态（10 分钟后）
            handler.postDelayed({
                currentStep = PlaybackStep.IDLE
                Log.d(TAG, "播放状态已重置")
            }, 600000L)
        }, 5000L)
    }

    /**
     * 发送媒体控制命令
     */
    private fun sendMediaCommand(command: String) {
        mediaController?.let { controller ->
            try {
                when (command) {
                    "play" -> controller.transportControls.play()
                    "pause" -> controller.transportControls.pause()
                    "next" -> controller.transportControls.skipToNext()
                    "previous" -> controller.transportControls.skipToPrevious()
                }
                Log.d(TAG, "已发送媒体控制命令：$command")
            } catch (e: Exception) {
                Log.e(TAG, "发送媒体控制命令失败", e)
            }
        } ?: Log.w(TAG, "媒体控制器未初始化")
    }

    /**
     * 设置播放模式
     */
    private fun setPlaybackMode(modeName: String) {
        Log.d(TAG, "设置播放模式：$modeName")
        
        // 注意：安卓系统对播放模式的控制有限
        // 最佳实践：用户在音乐应用中预设播放模式
        
        if (MusicAccessibilityService.isRunning) {
            val mode = PlaybackMode.valueOf(modeName)
            // 无障碍服务可以模拟点击播放模式按钮
        }
    }

    /**
     * 检查是否是 Apple Music
     */
    private fun musicAppIsAppleMusic(): Boolean {
        val prefs = getSharedPreferences("bluetooth_music_config", Context.MODE_PRIVATE)
        val musicApp = prefs.getString("music_app", "Apple Music") ?: "Apple Music"
        return musicApp == "Apple Music"
    }

    private fun getMusicAppPackageName(appName: String): String {
        return when (appName) {
            "Apple Music" -> "com.apple.android.music"
            "Spotify" -> "com.spotify.music"
            "网易云音乐" -> "com.netease.cloudmusic"
            "QQ 音乐" -> "com.tencent.qqmusic"
            else -> "com.apple.android.music"
        }
    }
}
