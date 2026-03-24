#!/bin/bash

# 蓝牙音乐自动化应用 - 快速构建脚本
# 适用于 Linux/macOS

set -e

echo "🎵 蓝牙音乐自动化应用 - 构建脚本"
echo "================================="

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未找到 Java，请先安装 JDK 11 或更高版本"
    exit 1
fi

echo "✅ Java 版本：$(java -version 2>&1 | head -n1)"

# 检查 ANDROID_HOME
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️  警告：未设置 ANDROID_HOME 或 ANDROID_SDK_ROOT"
    echo "   请确保已安装 Android SDK 并设置环境变量"
    echo ""
    echo "   macOS: export ANDROID_HOME=~/Library/Android/sdk"
    echo "   Linux: export ANDROID_HOME=~/Android/Sdk"
    echo "   Windows: set ANDROID_HOME=C:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk"
    echo ""
    read -p "是否继续？(y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 进入项目目录
cd "$(dirname "$0")"

# 检查 Gradle Wrapper
if [ ! -f "gradlew" ]; then
    echo "📥 下载 Gradle Wrapper..."
    # 如果没有 gradlew，尝试使用系统 gradle
    if command -v gradle &> /dev/null; then
        GRADLE_CMD="gradle"
    else
        echo "❌ 错误：未找到 Gradle，请安装或使用 Android Studio"
        exit 1
    fi
else
    GRADLE_CMD="./gradlew"
    chmod +x gradlew
fi

# 构建
echo ""
echo "🔨 开始构建..."
echo ""

if [ "$1" == "release" ]; then
    echo "构建 Release 版本（需要先配置签名）..."
    $GRADLE_CMD assembleRelease
    echo ""
    echo "✅ Release APK 生成位置："
    echo "   app/build/outputs/apk/release/app-release-unsigned.apk"
else
    echo "构建 Debug 版本..."
    $GRADLE_CMD assembleDebug
    echo ""
    echo "✅ Debug APK 生成位置："
    echo "   app/build/outputs/apk/debug/app-debug.apk"
fi

echo ""
echo "📱 安装到设备（需要 USB 连接和 ADB）："
echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
echo ""
