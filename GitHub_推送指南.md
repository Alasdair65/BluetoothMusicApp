# 🚀 发布到 GitHub - Alasdair65/BluetoothMusicApp

## ✅ 已完成配置

远程仓库已设置：
```
origin  https://github.com/Alasdair65/BluetoothMusicApp.git
```

---

## 📋 发布步骤

### 步骤 1：在 GitHub 创建仓库

1. **访问**: https://github.com/new
2. **仓库名称**: `BluetoothMusicApp`
3. **描述**: `🎵 安卓蓝牙音乐自动化应用 - 支持 Apple Music BGM→播放列表自动切换`
4. **可见性**: Public 或 Private（你的选择）
5. **不要**勾选 "Add a README file"
6. **不要**选择 .gitignore 或 License
7. 点击 **`Create repository`**

---

### 步骤 2：创建 Personal Access Token

由于 GitHub 不再支持账号密码推送，需要创建 Token：

1. **访问**: https://github.com/settings/tokens
2. 点击 **`Generate new token`** → **`Generate new token (classic)`**
3. **填写信息**:
   - **Note**: `BluetoothMusicApp`
   - **Expiration**: `90 days`（或更长）
   - **Scopes**: 勾选 **`repo`** (Full control of private repositories)
4. 点击 **`Generate token`**
5. **复制 Token**（以 `ghp_` 开头，只会显示一次！）
6. **保存到安全位置**

---

### 步骤 3：推送代码到 GitHub

**在服务器上执行：**

```bash
cd /home/admin/openclaw/workspace/BluetoothMusicApp

# 推送代码
git push -u origin main
```

**当提示输入凭据时：**
- **Username**: `Alasdair65`
- **Password**: 粘贴刚才复制的 **Personal Access Token**（不是账号密码！）

**或者，使用 URL 方式（推荐）：**

```bash
# 将 YOUR_TOKEN 替换为你的 Personal Access Token
git push https://Alasdair65:YOUR_TOKEN@github.com/Alasdair65/BluetoothMusicApp.git main
```

---

### 步骤 4：验证推送成功

1. 访问：https://github.com/Alasdair65/BluetoothMusicApp
2. 刷新页面
3. 应该能看到所有代码文件

---

### 步骤 5：创建 Release（上传 APK）

#### A. 先在本地构建 APK

**用 Android Studio：**
1. 克隆仓库到本地电脑：
   ```bash
   git clone https://github.com/Alasdair65/BluetoothMusicApp.git
   cd BluetoothMusicApp
   ```
2. 用 Android Studio 打开
3. `Build → Build APK(s)`
4. APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

#### B. 上传到 Release

1. 访问：https://github.com/Alasdair65/BluetoothMusicApp/releases/new
2. **Tag version**: `v1.0.0`
3. **Release title**: `v1.0.0 - 初始版本`
4. **描述**:
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
5. **上传 APK**: 将 `app-debug.apk` 拖到上传区域
6. 点击 **`Publish release`**

---

## 🎯 一键推送脚本

我已创建脚本 `push-to-github.sh`：

```bash
#!/bin/bash
cd /home/admin/openclaw/workspace/BluetoothMusicApp
echo "🚀 推送到 GitHub: Alasdair65/BluetoothMusicApp"
echo ""
echo "请输入你的 Personal Access Token:"
read -s TOKEN
echo ""
git push https://Alasdair65:$TOKEN@github.com/Alasdair65/BluetoothMusicApp.git main
if [ $? -eq 0 ]; then
    echo "✅ 推送成功！"
    echo "📱 访问：https://github.com/Alasdair65/BluetoothMusicApp"
else
    echo "❌ 推送失败，请检查 Token 是否正确"
fi
```

**使用方法：**
```bash
chmod +x push-to-github.sh
./push-to-github.sh
```

---

## 📥 用户下载链接

发布后，用户可以：

### 下载 APK
```
https://github.com/Alasdair65/BluetoothMusicApp/releases/tag/v1.0.0
```

### 克隆源码
```bash
git clone https://github.com/Alasdair65/BluetoothMusicApp.git
```

---

## ⚠️ 常见问题

### Q1: 推送时提示 "repository not found"
**解决**: 确保已在 GitHub 创建仓库

### Q2: 认证失败
**解决**: 
- 确认使用的是 Personal Access Token（不是账号密码）
- Token 是否已过期
- 是否勾选了 `repo` 权限

### Q3: 403 Forbidden
**解决**: 
- 检查 Token 权限
- 确认仓库所有者权限

---

## ✅ 发布检查清单

- [ ] 在 GitHub 创建仓库
- [ ] 创建 Personal Access Token
- [ ] 推送代码
- [ ] 验证仓库内容
- [ ] 本地构建 APK
- [ ] 创建 Release
- [ ] 上传 APK 文件
- [ ] 测试下载链接

---

## 🎉 完成后的链接

- **仓库首页**: https://github.com/Alasdair65/BluetoothMusicApp
- **Releases**: https://github.com/Alasdair65/BluetoothMusicApp/releases
- **Issues**: https://github.com/Alasdair65/BluetoothMusicApp/issues

---

**现在请在 GitHub 创建仓库，然后运行推送命令！** 🚀
