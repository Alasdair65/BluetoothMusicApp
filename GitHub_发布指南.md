# 📦 GitHub 发布指南

## 由于服务器网络限制，请按以下步骤手动发布到 GitHub

---

## 方法一：使用 GitHub Desktop（最简单）

### 步骤 1：下载并安装 GitHub Desktop
- 官网：https://desktop.github.com/
- 支持 Windows 和 macOS

### 步骤 2：添加本地仓库
1. 打开 GitHub Desktop
2. 选择 `File → Add Local Repository`
3. 点击 `Choose...` 选择 `BluetoothMusicApp` 文件夹
4. 如果是新仓库，选择 `Create a repository`

### 步骤 3：发布到 GitHub
1. 点击右上角 `Publish repository`
2. 输入仓库名称：`BluetoothMusicApp`
3. 描述：`安卓蓝牙音乐自动化应用 - 支持 Apple Music BGM→播放列表自动切换`
4. 勾选 `Keep this code private`（如果需要私有）
5. 点击 `Publish Repository`

---

## 方法二：使用命令行（需要 GitHub 账号）

### 步骤 1：在 GitHub 创建仓库

1. 访问 https://github.com/new
2. 仓库名称：`BluetoothMusicApp`
3. 描述：`🎵 安卓蓝牙音乐自动化应用 - 支持 Apple Music BGM→播放列表自动切换`
4. **不要**勾选 "Add a README file"
5. **不要**选择 .gitignore
6. **不要**选择 License
7. 点击 `Create repository`

### 步骤 2：关联远程仓库并推送

```bash
# 进入项目目录
cd /path/to/BluetoothMusicApp

# 添加远程仓库（替换 YOUR_USERNAME 为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/BluetoothMusicApp.git

# 验证远程仓库
git remote -v

# 推送代码
git push -u origin main
```

### 步骤 3：创建 Release

1. 访问你的仓库页面
2. 点击右侧 `Releases` → `Create a new release`
3. Tag version: `v1.0.0`
4. Release title: `v1.0.0 - 初始版本`
5. 描述：
   ```markdown
   ## 🎉 首个版本发布！

   ### ✨ 功能特性
   - 🔗 蓝牙连接自动检测
   - 🎼 Apple Music 完整支持
   - 🎹 BGM 歌曲先播放，完成后自动切换播放列表
   - 🔄 支持随机/顺序/单曲循环播放模式
   - 📡 后台持续监控服务
   - ♿ 无障碍服务精确控制

   ### 📱 系统要求
   - Android 8.0 (API 26) 及以上
   - 已测试兼容 Android 16

   ### 📋 使用说明
   详见 README.md 和 QUICKSTART.md
   ```
6. 点击 `Publish release`

---

## 方法三：使用 Git 凭据（推荐）

### 创建 Personal Access Token

1. 访问 https://github.com/settings/tokens
2. 点击 `Generate new token (classic)`
3. 名称：`BluetoothMusicApp`
4. 过期时间：选择 `90 days` 或更长
5. 勾选权限：
   - ✅ `repo` (Full control of private repositories)
6. 点击 `Generate token`
7. **复制并保存 Token**（只会显示一次）

### 使用 Token 推送

```bash
# 配置凭据（替换 YOUR_USERNAME 和 YOUR_TOKEN）
git config --global credential.helper store

# 推送代码（使用 Token 作为密码）
git push -u origin main
# 用户名：你的 GitHub 用户名
# 密码：粘贴刚才复制的 Token
```

---

## 📥 用户下载安装方式

### 方式 1：从 Release 下载 APK（推荐）

发布后，用户可以：

1. 访问仓库的 Releases 页面
   ```
   https://github.com/YOUR_USERNAME/BluetoothMusicApp/releases
   ```
2. 下载 `app-debug.apk` 或 `app-release.apk`
3. 传输到手机安装

### 方式 2：克隆源码自行构建

```bash
# 克隆仓库
git clone https://github.com/YOUR_USERNAME/BluetoothMusicApp.git

# 用 Android Studio 打开
# File → Open → 选择 BluetoothMusicApp 文件夹

# 构建 APK
# Build → Build APK(s)
```

---

## 📝 推荐的 README.md 更新

在仓库 README 顶部添加下载链接：

```markdown
# 🎵 蓝牙音乐自动化 (BluetoothMusicApp)

[![Release](https://img.shields.io/github/v/release/YOUR_USERNAME/BluetoothMusicApp)](https://github.com/YOUR_USERNAME/BluetoothMusicApp/releases)
[![Platform](https://img.shields.io/badge/platform-Android-blue)](https://www.android.com/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

一款安卓应用，当连接到指定蓝牙设备时自动播放音乐（类似 iOS 快捷指令）。

## 📥 下载

**最新版本**: [v1.0.0](https://github.com/YOUR_USERNAME/BluetoothMusicApp/releases/tag/v1.0.0)

- [app-debug.apk](https://github.com/YOUR_USERNAME/BluetoothMusicApp/releases/download/v1.0.0/app-debug.apk) - 调试版（推荐测试）
- [app-release.apk](https://github.com/YOUR_USERNAME/BluetoothMusicApp/releases/download/v1.0.0/app-release.apk) - 发布版

## ✨ 功能特性

...（保留原有内容）
```

---

## 🚀 快速发布脚本

创建 `publish-to-github.sh`：

```bash
#!/bin/bash

echo "📦 GitHub 发布脚本"
echo "================"

# 检查远程仓库
if ! git remote get-url origin &> /dev/null; then
    echo "❌ 未配置远程仓库"
    echo "请输入你的 GitHub 用户名："
    read USERNAME
    git remote add origin https://github.com/$USERNAME/BluetoothMusicApp.git
    echo "✅ 已添加远程仓库"
fi

# 推送代码
echo "🚀 推送到 GitHub..."
git push -u origin main

if [ $? -eq 0 ]; then
    echo "✅ 推送成功！"
    echo ""
    echo "📱 下一步："
    echo "1. 访问 https://github.com/$USERNAME/BluetoothMusicApp"
    echo "2. 创建 Release: https://github.com/$USERNAME/BluetoothMusicApp/releases/new"
    echo "3. 上传 APK 文件"
else
    echo "❌ 推送失败，请检查网络和凭据"
fi
```

使用：
```bash
chmod +x publish-to-github.sh
./publish-to-github.sh
```

---

## ✅ 发布检查清单

- [ ] 已在 GitHub 创建仓库
- [ ] 已关联远程仓库
- [ ] 已推送代码
- [ ] 已创建 Release (v1.0.0)
- [ ] 已上传 APK 文件
- [ ] README.md 已更新下载链接
- [ ] 测试下载链接有效

---

## 📞 需要帮助？

如果遇到问题：

1. **Git 推送失败**
   - 检查网络连接
   - 验证 GitHub 凭据
   - 使用 Personal Access Token

2. **APK 构建问题**
   - 查看 `QUICKSTART.md`
   - 检查 Android SDK 配置
   - 查看 `local.properties`

3. **GitHub 仓库问题**
   - 确认仓库名称正确
   - 检查账号权限

---

**祝发布顺利！** 🎉
