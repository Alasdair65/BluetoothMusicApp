package com.bluetooth.music

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Path
import android.media.MediaController
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 无障碍辅助服务
 * 用于模拟 UI 点击操作，实现 Apple Music 的精确控制
 * 包括：搜索歌曲、播放特定歌曲、选择播放列表等
 */
class MusicAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MusicAccessibilityService"
        var isRunning = false
            private set
        
        // Apple Music 包名
        private const val APPLE_MUSIC_PACKAGE = "com.apple.android.music"
        
        // 播放状态
        private var isPlayingBGM = false
        private var bgmPlayed = false
        private val handler = Handler(Looper.getMainLooper())
    }

    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
        prefs = getSharedPreferences("bluetooth_music_config", MODE_PRIVATE)
        Log.d(TAG, "无障碍服务已连接")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isRunning = false
        Log.d(TAG, "无障碍服务已断开")
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 监听 Apple Music 的界面变化
        if (event?.packageName == APPLE_MUSIC_PACKAGE) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    handleWindowStateChanged(event)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "服务被中断")
    }

    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        // 检测是否正在播放 BGM
        if (isPlayingBGM && !bgmPlayed) {
            // 检查 BGM 是否播放完成
            checkBGMCompletion()
        }
    }

    /**
     * 播放 BGM 歌曲
     * 使用无障碍服务在 Apple Music 中搜索并播放指定歌曲
     */
    fun playBGMSong(songName: String) {
        Log.d(TAG, "开始播放 BGM: $songName")
        isPlayingBGM = true
        bgmPlayed = false

        // 1. 启动 Apple Music
        val launchIntent = packageManager.getLaunchIntentForPackage(APPLE_MUSIC_PACKAGE)
        launchIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        launchIntent?.let { startActivity(it) }

        // 2. 延迟后执行搜索
        handler.postDelayed({
            // 搜索歌曲
            searchAndPlaySong(songName)
        }, 3000)
    }

    /**
     * 搜索并播放歌曲
     */
    private fun searchAndPlaySong(songName: String) {
        val rootNode = rootInActiveWindow ?: return

        try {
            // 查找搜索按钮并点击
            val searchButton = findNodeByText(rootNode, "搜索") ?: findNodeByText(rootNode, "Search")
            searchButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            
            // 输入歌曲名称
            handler.postDelayed({
                val searchField = rootInActiveWindow?.findAccessibilityNodeInfosByText(songName)
                // 使用输入法输入歌曲名
                // 注意：实际实现需要更复杂的输入处理
            }, 500)

        } catch (e: Exception) {
            Log.e(TAG, "搜索歌曲失败", e)
        }
    }

    /**
     * 检查 BGM 是否播放完成
     */
    private fun checkBGMCompletion() {
        val mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        val activeSessions = mediaSessionManager.getActiveSessions(null)

        if (activeSessions.isNotEmpty()) {
            val controller = MediaController(this, activeSessions[0].sessionToken)
            val playbackState = controller.playbackState

            // 检查是否播放完成（状态变为暂停或停止）
            if (playbackState?.state == android.media.session.PlaybackState.STATE_PAUSED ||
                playbackState?.state == android.media.session.PlaybackState.STATE_STOPPED) {
                
                Log.d(TAG, "BGM 播放完成，切换到播放列表")
                bgmPlayed = true
                isPlayingBGM = false
                
                // 播放预设的播放列表
                playPlaylist()
            }
        }
    }

    /**
     * 播放预设的播放列表
     */
    private fun playPlaylist() {
        val playlistName = prefs.getString("playlist_name", "我的最爱") ?: "我的最爱"
        Log.d(TAG, "开始播放播放列表：$playlistName")

        val rootNode = rootInActiveWindow ?: return

        try {
            // 查找播放列表并点击
            val playlistNode = findNodeByText(rootNode, playlistName)
            playlistNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)

            // 点击播放按钮
            handler.postDelayed({
                val playButton = rootInActiveWindow?.findAccessibilityNodeInfosByText("播放")
                    ?: rootInActiveWindow?.findAccessibilityNodeInfosByText("Play")
                playButton?.firstOrNull()?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }, 1000)

        } catch (e: Exception) {
            Log.e(TAG, "播放播放列表失败", e)
        }
    }

    /**
     * 根据文本查找节点
     */
    private fun findNodeByText(root: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        for (i in 0 until root.childCount) {
            val child = root.getChild(i)
            child?.let {
                val nodeText = it.text?.toString()
                if (nodeText?.contains(text, ignoreCase = true) == true) {
                    return it
                }
                // 递归查找
                val found = findNodeByText(it, text)
                if (found != null) return found
            }
        }
        return null
    }

    /**
     * 执行点击手势
     */
    private fun performClick(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        
        val gesture = GestureDescription.Builder()
            .addStroke(StrokeDescription(path, 0, 100))
            .build()
        
        dispatchGesture(gesture, null, null)
    }

    /**
     * 设置播放模式
     */
    fun setPlaybackMode(mode: PlaybackMode) {
        Log.d(TAG, "设置播放模式：$mode")
        
        val rootNode = rootInActiveWindow ?: return
        
        try {
            when (mode) {
                PlaybackMode.SEQUENTIAL -> {
                    // 关闭随机播放
                    val shuffleButton = findNodeByText(rootNode, "随机")
                    shuffleButton?.let {
                        if (it.isSelected) {
                            it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                    }
                }
                PlaybackMode.SHUFFLE -> {
                    // 开启随机播放
                    val shuffleButton = findNodeByText(rootNode, "随机")
                    shuffleButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                PlaybackMode.REPEAT_ONE -> {
                    // 设置单曲循环
                    val repeatButton = findNodeByText(rootNode, "重复")
                    repeatButton?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "设置播放模式失败", e)
        }
    }
}
