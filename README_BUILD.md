# BluetoothMusicApp 构建指南

## ✅ 项目已准备就绪

项目位置：`/home/admin/openclaw/workspace/BluetoothMusicApp`

## 🚀 构建方式

### 方式一：Android Studio（推荐）

1. **打开 Android Studio**
2. **File → Open**，选择 `/home/admin/openclaw/workspace/BluetoothMusicApp`
3. **等待 Gradle 同步完成**（首次打开会自动下载依赖）
4. **Build → Build APK(s)** 或直接运行到模拟器/真机

### 方式二：命令行

```bash
cd /home/admin/openclaw/workspace/BluetoothMusicApp
./gradlew clean assembleDebug
```

APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`

## 📋 项目信息

- **仓库**: https://github.com/Alasdair65/BluetoothMusicApp
- **编译 SDK**: 33
- **目标 SDK**: 33
- **最低 SDK**: 26 (Android 8.0)
- **Kotlin 版本**: 1.9.20

## ⚠️ 注意事项

- `local.properties` 已配置为 Linux 路径，如果你在 macOS/Windows 上使用，需要修改 SDK 路径
- 首次构建需要下载 Gradle 和 Android 依赖，可能需要几分钟
- 确保已安装 Android SDK Build-Tools 33.0.2
