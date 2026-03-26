# Android 构建环境修复记录

## 问题
构建失败：`Failed to find Build Tools revision 30.0.3`

## 已完成的修复

### 1. 修复 local.properties
- ✅ 已将 SDK 路径从 macOS 路径 `/Users/yourname/Library/Android/sdk` 更新为 Linux 路径 `/home/admin/Android/Sdk`

### 2. 添加 Gradle Wrapper
- ✅ 已下载 `gradle-wrapper.jar` 到 `gradle/wrapper/` 目录
- ✅ 已设置 `gradlew` 执行权限

## 待解决的问题

### Android SDK 未安装
当前状态：
- SDK 目录已创建：`/home/admin/Android/Sdk`
- ❌ Build Tools 未安装
- ❌ Android Platform 未安装
- ❌ sdkmanager 不可用（命令行工具下载失败）

### 需要手动执行的步骤

由于网络原因，无法自动下载 Android SDK 命令行工具。请手动执行以下命令：

```bash
# 1. 下载 Android 命令行工具
cd /home/admin/Android/Sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

# 2. 解压
unzip cmdline-tools.zip
mkdir -p latest
mv cmdline-tools/* latest/

# 3. 安装 SDK 组件
yes | ./latest/bin/sdkmanager --install "build-tools;33.0.2" "platforms;android-33" "platform-tools"

# 4. 接受许可证
yes | ./latest/bin/sdkmanager --licenses
```

## 替代方案

如果上述方法不可行，可以考虑：

1. **使用 Android Studio**: 在本地安装 Android Studio，它会自动管理 SDK
2. **使用 Docker**: 使用预配置的 Android 构建 Docker 镜像
3. **使用 GitHub Actions**: 在 CI 环境中构建

## 验证构建

完成 SDK 安装后，运行：

```bash
cd /home/admin/openclaw/workspace/BluetoothMusicApp
./gradlew clean assembleDebug
```
