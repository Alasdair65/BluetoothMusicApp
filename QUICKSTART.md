# 🚀 快速入门指南

## 5 分钟上手

### 步骤 1：准备环境（2 分钟）

**选项 A：使用 Android Studio（推荐新手）**

1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 打开 Android Studio
3. 选择 `File → Open`，选择 `BluetoothMusicApp` 文件夹
4. 等待 Gradle 同步完成

**选项 B：命令行构建（适合有经验用户）**

```bash
# 确保已安装：
# - JDK 11+
# - Android SDK
# - ANDROID_HOME 环境变量已设置

cd BluetoothMusicApp
chmod +x gradlew
./gradlew assembleDebug
```

### 步骤 2：构建 APK（1 分钟）

**Android Studio:**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**命令行:**
```bash
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 步骤 3：安装到手机（1 分钟）

**方法 A：USB 连接**
```bash
# 手机开启 USB 调试
# 通过 USB 连接电脑
adb install app/build/outputs/apk/debug/app-debug.apk
```

**方法 B：直接传输**
1. 将 APK 文件发送到手机（微信/QQ/邮件/数据线）
2. 在手机上点击 APK 文件安装
3. 允许"未知来源"安装

### 步骤 4：配置自动化（1 分钟）

1. **打开应用**
   - 首次启动会请求权限
   - 授予所有请求的权限

2. **选择蓝牙设备**
   - 从列表中选择你的蓝牙设备
   - 如果列表为空，请先在系统设置中配对

3. **选择音乐应用**
   - 选择 Apple Music（或其他）

4. **设置 BGM（可选）**
   - 输入连接后想先播放的歌曲
   - 或留空直接播放歌单

5. **选择播放模式**
   - 顺序播放 / 随机播放 / 单曲循环

6. **输入歌单名称**
   - 例如："我的最爱"

7. **点击"保存配置并启动监控"**

### 步骤 5：测试

1. 断开当前蓝牙连接
2. 重新连接蓝牙设备
3. 观察手机：
   - ✅ 自动打开音乐应用
   - ✅ 开始播放音乐

---

## ⚡ 故障快速排查

| 问题 | 解决方案 |
|------|----------|
| 应用无法打开 | 检查是否授予所有权限 |
| 找不到蓝牙设备 | 先在系统设置中配对设备 |
| 音乐不播放 | 确保音乐应用已安装并登录 |
| 应用被杀死 | 关闭电池优化，允许后台运行 |
| 通知栏没有监控状态 | 检查通知权限是否授予 |

---

## 📱 安卓 16 特别说明

你使用的是 Android 16，请注意：

1. **权限更严格**：确保授予所有请求的权限
2. **后台限制**：在设置中允许应用后台运行
3. **通知权限**：Android 13+ 需要单独授予通知权限

---

## 🎯 下一步

- 阅读 [README.md](README.md) 了解详细功能
- 阅读 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 了解代码结构
- 如需修改功能，编辑 `MainActivity.kt` 和 `BluetoothMonitorService.kt`

---

## 💡 提示

**首次使用建议：**

1. 在音乐应用中提前创建好歌单
2. 在音乐应用中设置好播放模式（随机/循环）
3. 测试时先使用较短的歌单，方便验证

**最佳实践：**

- 为不同场景创建多个配置（车载、耳机、音箱）
- 使用不同的歌单名称区分场景
- 定期检查应用是否在后台运行
