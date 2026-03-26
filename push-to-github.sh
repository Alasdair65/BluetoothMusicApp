#!/bin/bash

# 蓝牙音乐自动化应用 - 一键推送到 GitHub
# 用户：Alasdair65
# 仓库：BluetoothMusicApp

set -e

cd /home/admin/openclaw/workspace/BluetoothMusicApp

echo "🚀 推送到 GitHub"
echo "================"
echo ""
echo "仓库：https://github.com/Alasdair65/BluetoothMusicApp"
echo ""

# 检查 Git 状态
echo "📋 检查 Git 状态..."
git status -s

if [ $? -ne 0 ]; then
    echo "❌ Git 仓库状态异常"
    exit 1
fi

echo ""
echo "📝 最近提交:"
git log --oneline -3
echo ""

# 提示输入 Token
echo "🔑 请输入你的 GitHub Personal Access Token:"
echo "   (以 ghp_ 开头，在 https://github.com/settings/tokens 创建)"
echo "   (输入时不会显示，按 Enter 确认)"
read -s TOKEN

if [ -z "$TOKEN" ]; then
    echo ""
    echo "❌ Token 不能为空"
    exit 1
fi

echo ""
echo ""
echo "🚀 开始推送..."
echo ""

# 推送
git push https://Alasdair65:$TOKEN@github.com/Alasdair65/BluetoothMusicApp.git main 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 推送成功！"
    echo ""
    echo "📱 下一步:"
    echo ""
    echo "1️⃣  访问仓库:"
    echo "   https://github.com/Alasdair65/BluetoothMusicApp"
    echo ""
    echo "2️⃣  创建 Release:"
    echo "   https://github.com/Alasdair65/BluetoothMusicApp/releases/new"
    echo "   - Tag: v1.0.0"
    echo "   - 上传 APK 文件"
    echo ""
    echo "3️⃣  构建 APK (在本地电脑):"
    echo "   git clone https://github.com/Alasdair65/BluetoothMusicApp.git"
    echo "   用 Android Studio 打开 → Build → Build APK"
    echo ""
else
    echo ""
    echo "❌ 推送失败"
    echo ""
    echo "可能的原因:"
    echo "1. Token 无效或已过期"
    echo "2. 仓库不存在（请先在 GitHub 创建）"
    echo "3. Token 权限不足（需要 repo 权限）"
    echo ""
    echo "解决方案:"
    echo "1. 创建新 Token: https://github.com/settings/tokens"
    echo "2. 确保勾选 'repo' 权限"
    echo "3. 确认仓库已创建：https://github.com/new"
    exit 1
fi
