package com.bluetooth.music

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * 蓝牙音乐自动化应用 - 主界面
 * 
 * 完整功能：
 * 1. 选择目标蓝牙设备
 * 2. 选择音乐应用（完整支持 Apple Music）
 * 3. 设置 BGM 歌曲（连接蓝牙后先播放这首歌）
 * 4. 设置播放列表（BGM 播放完成后自动切换）
 * 5. 配置播放模式（随机/单曲循环/顺序播放）
 */
class MainActivity : ComponentActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            initializeBluetooth()
        } else {
            Toast.makeText(this, "需要蓝牙权限才能使用此功能", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化蓝牙适配器
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // 检查并请求权限
        checkAndRequestPermissions()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BluetoothMusicApp(
                        bluetoothAdapter = bluetoothAdapter,
                        context = this
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        val needRequest = permissions.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needRequest) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        } else {
            initializeBluetooth()
        }
    }

    private fun initializeBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "此设备不支持蓝牙", Toast.LENGTH_LONG).show()
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要启用蓝牙才能使用此功能", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        // 启动后台服务监控蓝牙连接
        val serviceIntent = Intent(this, BluetoothMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}

@Composable
fun BluetoothMusicApp(bluetoothAdapter: BluetoothAdapter?, context: Context) {
    var selectedBluetoothDevice by remember { mutableStateOf<String?>(null) }
    var selectedMusicApp by remember { mutableStateOf("Apple Music") }
    var playbackMode by remember { mutableStateOf(PlaybackMode.SEQUENTIAL) }
    var playlistName by remember { mutableStateOf("我的最爱") }
    var bgmSongName by remember { mutableStateOf("") }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    // 获取已配对的蓝牙设备
    val pairedDevices = remember {
        bluetoothAdapter?.bondedDevices?.map { it.name ?: it.address } ?: emptyList()
    }

    // 加载已保存的配置
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("bluetooth_music_config", Context.MODE_PRIVATE)
        selectedBluetoothDevice = prefs.getString("bluetooth_device", null)
        selectedMusicApp = prefs.getString("music_app", "Apple Music") ?: "Apple Music"
        playlistName = prefs.getString("playlist_name", "我的最爱") ?: "我的最爱"
        bgmSongName = prefs.getString("bgm_song", "") ?: ""
        
        val modeName = prefs.getString("playback_mode", PlaybackMode.SEQUENTIAL.name)
        playbackMode = try {
            PlaybackMode.valueOf(modeName ?: PlaybackMode.SEQUENTIAL.name)
        } catch (e: Exception) {
            PlaybackMode.SEQUENTIAL
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎵 蓝牙音乐自动化",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "完整支持 Apple Music · BGM→播放列表自动切换",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 蓝牙设备选择
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1️⃣ 选择蓝牙设备",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                pairedDevices.forEach { deviceName ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedBluetoothDevice == deviceName,
                                onClick = { selectedBluetoothDevice = deviceName },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedBluetoothDevice == deviceName,
                            onClick = null
                        )
                        Text(
                            text = deviceName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                if (pairedDevices.isEmpty()) {
                    Text(
                        text = "⚠️ 未找到已配对的蓝牙设备，请先在系统设置中配对",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // 音乐应用选择
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2️⃣ 选择音乐应用",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "✅ Apple Music 完整支持：BGM 歌曲 + 播放列表自动切换",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                listOf("Apple Music", "Spotify", "网易云音乐", "QQ 音乐").forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedMusicApp == app,
                                onClick = { selectedMusicApp = app },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMusicApp == app,
                            onClick = null
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                text = app,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (app == "Apple Music") {
                                Text(
                                    text = "✓ 完整功能支持",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // BGM 歌曲设置
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3️⃣ BGM 歌曲（可选）",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "连接蓝牙后先播放这首歌，播放完成后自动切换到播放列表",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = bgmSongName,
                    onValueChange = { bgmSongName = it },
                    label = { Text("BGM 歌曲名称") },
                    placeholder = { Text("例如：Welcome Home - Mr. Mister") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        Text("💡 留空则直接播放歌单，不播放 BGM")
                    }
                )
            }
        }

        // 歌单名称
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "4️⃣ 播放列表",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "BGM 播放完成后自动播放这个歌单",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("播放列表名称") },
                    placeholder = { Text("例如：我的最爱、驾驶歌单、晨跑音乐") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        Text("📋 请确保在 Apple Music 中已创建此歌单")
                    }
                )
            }
        }

        // 播放模式选择
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "5️⃣ 播放模式",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(modifier = Modifier.selectableGroup()) {
                    PlaybackMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = playbackMode == mode,
                                    onClick = { playbackMode = mode },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = playbackMode == mode,
                                onClick = null
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 保存按钮
        Button(
            onClick = {
                // 保存配置到 SharedPreferences
                val prefs = context.getSharedPreferences("bluetooth_music_config", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("bluetooth_device", selectedBluetoothDevice)
                    putString("music_app", selectedMusicApp)
                    putString("playback_mode", playbackMode.name)
                    putString("playlist_name", playlistName)
                    putString("bgm_song", bgmSongName)
                    apply()
                }
                
                // 显示提示消息
                val message = if (bgmSongName.isNotEmpty()) {
                    "✅ 配置已保存！\nBGM: $bgmSongName → 歌单：$playlistName"
                } else {
                    "✅ 配置已保存！\n直接播放歌单：$playlistName"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                
                // 提示开启无障碍服务
                showAccessibilityDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !selectedBluetoothDevice.isNullOrEmpty()
        ) {
            Text("💾 保存配置并启动监控", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 状态提示
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📡 应用已在后台运行",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "连接到指定蓝牙设备时自动播放音乐",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    // 无障碍服务设置对话框
    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("需要开启无障碍权限") },
            text = { 
                Column {
                    Text("为了完整控制 Apple Music，需要开启无障碍权限：")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• 搜索并播放 BGM 歌曲")
                    Text("• BGM 播放完成后自动切换到播放列表")
                    Text("• 设置播放模式")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 打开无障碍设置
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                        showAccessibilityDialog = false
                    }
                ) {
                    Text("去设置")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAccessibilityDialog = false }
                ) {
                    Text("稍后")
                }
            }
        )
    }
}

enum class PlaybackMode(val displayName: String, val description: String) {
    SEQUENTIAL("顺序播放", "按歌单顺序依次播放"),
    SHUFFLE("随机播放", "随机播放歌单中的歌曲"),
    REPEAT_ONE("单曲循环", "重复播放当前歌曲")
}
