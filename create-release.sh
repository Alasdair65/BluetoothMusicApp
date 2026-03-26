#!/bin/bash

# 在本地电脑运行此脚本创建 GitHub Release
# 需要安装 GitHub CLI (gh)

set -e

echo "🎉 创建 GitHub Release"
echo "======================"
echo ""

# 检查 gh 是否安装
if ! command -v gh &> /dev/null; then
    echo "❌ 未找到 GitHub CLI (gh)"
    echo ""
    echo "请先安装："
    echo "macOS: brew install gh"
    echo "Windows: winget install GitHub.cli"
    echo "Linux: sudo apt install gh"
    exit 1
fi

# 检查是否已登录
if ! gh auth status &> /dev/null; then
    echo "🔐 请先登录 GitHub"
    gh auth login
fi

cd "$(dirname "$0")"

echo "📱 构建 APK..."
if command -v ./gradlew &> /dev/null; then
    ./gradlew assembleDebug
else
    echo "❌ 未找到 gradlew，请先用 Android Studio 构建 APK"
    exit 1
fi

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK 文件不存在：$APK_PATH"
    exit 1
fi

echo "✅ APK 已构建：$APK_PATH"
echo ""

echo "📦 创建 Release v1.0.0..."

# 创建 release 描述
DESCRIPTION=$(cat << 'EOF'
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
1. 安装 APK
2. 授予蓝牙和通知权限
3. 开启无障碍权限（设置 → 无障碍）
4. 配置 BGM 歌曲和播放列表
5. 连接蓝牙测试
EOF
)

# 创建 release
gh release create v1.0.0 \
  --title "v1.0.0 - 初始版本" \
  --notes "$DESCRIPTION" \
  "$APK_PATH"

echo ""
echo "✅ Release 创建成功！"
echo ""
echo "📥 下载链接："
echo "https://github.com/Alasdair65/BluetoothMusicApp/releases/tag/v1.0.0"
