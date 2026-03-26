#!/bin/bash

# 蓝牙音乐自动化应用 - 发布到 GitHub
# 使用方法：./publish-to-github.sh

set -e

echo "📦 蓝牙音乐自动化 - GitHub 发布脚本"
echo "===================================="
echo ""

# 检查 Git 仓库
if [ ! -d ".git" ]; then
    echo "❌ 错误：当前目录不是 Git 仓库"
    echo "请先运行：git init"
    exit 1
fi

# 检查远程仓库
if ! git remote get-url origin &> /dev/null; then
    echo "⚠️  未配置远程仓库"
    echo ""
    echo "请在 GitHub 创建仓库后，输入仓库 URL："
    echo "格式：https://github.com/你的用户名/BluetoothMusicApp.git"
    echo ""
    read -p "仓库 URL: " REPO_URL
    git remote add origin "$REPO_URL"
    echo "✅ 已添加远程仓库"
else
    CURRENT_REMOTE=$(git remote get-url origin)
    echo "✅ 当前远程仓库：$CURRENT_REMOTE"
fi

echo ""
echo "📋 当前提交记录:"
git log --oneline -3
echo ""

# 确认发布
read -p "确认推送到 GitHub? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 已取消"
    exit 1
fi

# 推送
echo ""
echo "🚀 推送到 GitHub..."
git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 推送成功！"
    echo ""
    
    # 获取仓库 URL
    REPO_URL=$(git remote get-url origin | sed 's/\.git$//' | sed 's/git@github.com:/https:\/\/github.com\//')
    
    echo "📱 下一步操作："
    echo ""
    echo "1️⃣  访问仓库页面："
    echo "   $REPO_URL"
    echo ""
    echo "2️⃣  创建 Release："
    echo "   $REPO_URL/releases/new"
    echo ""
    echo "3️⃣  填写 Release 信息："
    echo "   - Tag version: v1.0.0"
    echo "   - Release title: v1.0.0 - 初始版本"
    echo "   - 上传 APK 文件（构建后）"
    echo ""
    echo "4️⃣  构建 APK（在 Android Studio 中）："
    echo "   Build → Build APK(s)"
    echo ""
    echo "5️⃣  上传 APK 到 Release："
    echo "   将 app/build/outputs/apk/debug/app-debug.apk 拖到 Release 页面"
    echo ""
    echo "🎉 发布完成！"
else
    echo ""
    echo "❌ 推送失败"
    echo ""
    echo "可能的原因："
    echo "1. 网络连接问题"
    echo "2. GitHub 凭据未配置"
    echo "3. 仓库不存在或无权限"
    echo ""
    echo "解决方案："
    echo "1. 检查网络连接"
    echo "2. 创建 Personal Access Token:"
    echo "   https://github.com/settings/tokens"
    echo "3. 使用 Token 作为密码推送："
    echo "   git push -u origin main"
    exit 1
fi
