#!/bin/bash
# Android SDK 环境检查脚本

SDK_DIR="/home/admin/Android/Sdk"
BUILD_TOOLS_VERSION="33.0.2"

echo "=== Android SDK 环境检查 ==="
echo ""

# 检查 SDK 目录
if [ -d "$SDK_DIR" ]; then
    echo "✓ SDK 目录存在：$SDK_DIR"
else
    echo "✗ SDK 目录不存在：$SDK_DIR"
    echo "  需要安装 Android SDK"
fi

# 检查 build-tools
if [ -d "$SDK_DIR/build-tools/$BUILD_TOOLS_VERSION" ]; then
    echo "✓ Build Tools $BUILD_TOOLS_VERSION 已安装"
else
    echo "✗ Build Tools $BUILD_TOOLS_VERSION 未安装"
    echo "  需要安装：$SDK_DIR/build-tools/$BUILD_TOOLS_VERSION"
fi

# 检查 platforms
if [ -d "$SDK_DIR/platforms/android-33" ]; then
    echo "✓ Android Platform 33 已安装"
else
    echo "✗ Android Platform 33 未安装"
fi

# 检查 local.properties
echo ""
echo "=== local.properties 内容 ==="
cat /home/admin/openclaw/workspace/BluetoothMusicApp/local.properties
