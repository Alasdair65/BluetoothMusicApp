# 蓝牙音乐自动化应用 - 项目结构

```
BluetoothMusicApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/bluetooth/music/
│   │       │   ├── MainActivity.kt              # 主界面（配置 UI）
│   │       │   ├── BluetoothMonitorService.kt   # 后台监控服务
│   │       │   └── BluetoothConnectionReceiver.kt # 蓝牙广播接收器
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── strings.xml              # 字符串资源
│   │       │   │   ├── colors.xml               # 颜色定义
│   │       │   │   └── themes.xml               # 主题样式
│   │       │   ├── drawable/                    # 图标资源
│   │       │   └── mipmap/                      # 应用图标
│   │       └── AndroidManifest.xml              # 应用清单（权限声明）
│   ├── build.gradle.kts                         # 应用级构建配置
│   └── proguard-rules.pro                       # 代码混淆规则
├── gradle/
│   ├── wrapper/
│   │   └── gradle-wrapper.properties            # Gradle 版本配置
│   └── libs.versions.toml                       # 依赖版本目录
├── build.gradle.kts                             # 项目级构建配置
├── settings.gradle.kts                          # 项目设置
├── local.properties                             # 本地配置（SDK 路径）
├── build.sh                                     # Linux/macOS 构建脚本
├── build.bat                                    # Windows 构建脚本
├── README.md                                    # 详细文档
└── .gitignore                                   # Git 忽略文件
```

## 核心文件说明

### 1. MainActivity.kt
- **功能**：应用主界面，使用 Jetpack Compose 构建
- **职责**：
  - 显示配置 UI（蓝牙设备、音乐应用、播放模式等）
  - 请求并管理权限
  - 保存配置到 SharedPreferences
  - 启动后台服务

### 2. BluetoothMonitorService.kt
- **功能**：后台监控服务
- **职责**：
  - 每 5 秒检查一次蓝牙连接状态
  - 检测到目标设备连接时触发音乐播放
  - 发送前台服务通知
  - 使用 MediaController 控制媒体播放

### 3. BluetoothConnectionReceiver.kt
- **功能**：广播接收器
- **职责**：
  - 监听系统蓝牙广播（ACTION_ACL_CONNECTED/DISCONNECTED）
  - 快速响应蓝牙连接事件
  - 通知服务进行检查

### 4. AndroidManifest.xml
- **功能**：应用清单文件
- **声明**：
  - 应用组件（Activity、Service、Receiver）
  - 所需权限（蓝牙、媒体控制、前台服务等）
  - 应用图标和主题

## 数据流

```
用户操作
   │
   ▼
MainActivity (配置 UI)
   │
   ├─→ 保存配置到 SharedPreferences
   │
   └─→ 启动 BluetoothMonitorService
          │
          ├─→ 定期检查蓝牙连接
          │      │
          │      ├─→ 检测到连接 → 触发播放
          │      │
          │      └─→ 未检测到 → 继续监控
          │
          └─→ 发送媒体控制指令
                 │
                 └─→ 音乐应用播放音乐
```

## 配置存储

应用使用 SharedPreferences 存储以下配置：

| 键名 | 类型 | 说明 |
|------|------|------|
| bluetooth_device | String | 目标蓝牙设备名称 |
| music_app | String | 音乐应用名称 |
| playback_mode | String | 播放模式（SEQUENTIAL/SHUFFLE/REPEAT_ONE） |
| playlist_name | String | 歌单名称 |
| bgm_song | String | BGM 歌曲名称（可选） |

## 依赖说明

### 核心依赖
- **AndroidX Core KTX**：Kotlin 扩展
- **Material Components**：Material Design 组件
- **Jetpack Compose**：声明式 UI 框架
- **Lifecycle Runtime**：生命周期管理

### 系统 API
- **BluetoothAdapter**：蓝牙设备管理
- **MediaController**：媒体播放控制
- **MediaSessionManager**：媒体会话管理
- **NotificationManager**：通知管理
