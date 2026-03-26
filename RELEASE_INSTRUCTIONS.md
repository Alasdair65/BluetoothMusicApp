# 📦 发布 APK 到 GitHub Release

## 快速步骤（在本地电脑执行）

### 1️⃣ 克隆/拉取最新代码

```bash
cd ~/Projects  # 或你的项目目录
git clone https://github.com/Alasdair65/BluetoothMusicApp.git
# 或如果已有：
cd BluetoothMusicApp
git pull
```

### 2️⃣ 用 Android Studio 打开

- 打开 Android Studio
- File → Open → 选择 `BluetoothMusicApp` 文件夹
- 等待 Gradle 同步完成

### 3️⃣ 构建 APK

**方法 A：使用 Android Studio（推荐）**
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. 等待构建完成
3. 点击 "locate" 打开 APK 所在文件夹
4. APK 位置：`app/build/outputs/apk/release/app-release.apk`

**方法 B：使用命令行**
```bash
cd BluetoothMusicApp
./gradlew assembleRelease
```

### 4️⃣ 安装 GitHub CLI（如果还没有）

**macOS:**
```bash
brew install gh
```

**Windows:**
```powershell
winget install GitHub.cli
```

**Linux:**
```bash
sudo apt update && sudo apt install gh
```

### 5️⃣ 登录 GitHub

```bash
gh auth login
```
- 选择 GitHub.com
- 选择 HTTPS
- 登录浏览器授权

### 6️⃣ 创建 Release 并上传 APK

```bash
cd BluetoothMusicApp

# 创建 release 并上传 APK
gh release create v1.0.0 \
  --title "v1.0.0 - 初始版本" \
  --notes "## 🎉 首个版本发布！

### ✨ 功能特性
- 🔗 蓝牙连接自动检测
- 🎼 Apple Music 完整支持
- 🎹 BGM 歌曲先播放，完成后自动切换播放列表
- 🔄 支持随机/顺序/单曲循环播放模式
- 📡 后台持续监控服务
- ♿ 无障碍服务精确控制

### 📱 系统要求
- Android 8.0 (API 26) 及以上

### 📋 使用说明
1. 安装 APK
2. 授予蓝牙和通知权限
3. 开启无障碍权限（设置 → 无障碍）
4. 配置 BGM 歌曲和播放列表
5. 连接蓝牙测试" \
  app/build/outputs/apk/release/app-release.apk
```

### 7️⃣ 完成！

访问：https://github.com/Alasdair65/BluetoothMusicApp/releases/tag/v1.0.0

---

## 🔧 如果遇到问题

### Gradle 同步失败
- 确保 Android Studio 是最新版本
- File → Invalidate Caches / Restart

### 找不到 Build Tools
- Tools → SDK Manager → SDK Tools
- 勾选 "Android SDK Build-Tools"
- 点击 Apply 安装

### GitHub CLI 认证失败
```bash
gh auth logout
gh auth login
```

---

## 📱 测试安装

在 Android 设备上：
1. 下载 APK：https://github.com/Alasdair65/BluetoothMusicApp/releases/download/v1.0.0/app-release.apk
2. 允许"未知来源"安装
3. 安装后打开应用
4. 按提示授予权限
