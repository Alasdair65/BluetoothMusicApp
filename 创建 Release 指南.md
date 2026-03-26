# 🎉 GitHub Release 创建指南

## ✅ 代码已推送成功

你的代码已经在 GitHub 上：
**https://github.com/Alasdair65/BluetoothMusicApp**

---

## ⚠️ 安全提醒

**重要**：你的 Token 已泄露，建议立即：

1. **删除当前 Token**：
   https://github.com/settings/tokens
   
2. **创建新 Token**（如果需要）：
   - 访问：https://github.com/settings/tokens
   - 点击 `Generate new token (classic)`
   - 名称：`BluetoothMusicApp-2`
   - 权限：`repo`
   - **不要分享给任何人**

---

## 📦 创建 Release 步骤

### 步骤 1：访问 Release 页面

打开浏览器访问：
```
https://github.com/Alasdair65/BluetoothMusicApp/releases/new
```

### 步骤 2：填写 Release 信息

| 字段 | 填写内容 |
|------|----------|
| **Tag version** | `v1.0.0` |
| **Release title** | `v1.0.0 - 初始版本` |
| **Description** | (复制下方内容) |

**Release 描述：**
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
1. 安装 APK
2. 授予蓝牙和通知权限
3. 开启无障碍权限（设置 → 无障碍）
4. 配置 BGM 歌曲和播放列表
5. 连接蓝牙测试

详见 README.md 和 QUICKSTART.md
```

### 步骤 3：上传 APK 文件

**先构建 APK：**

1. **克隆仓库到本地电脑**：
   ```bash
   git clone https://github.com/Alasdair65/BluetoothMusicApp.git
   cd BluetoothMusicApp
   ```

2. **用 Android Studio 打开**：
   - 启动 Android Studio
   - `File → Open` → 选择文件夹
   - 等待 Gradle 同步

3. **配置 SDK 路径**：
   编辑 `local.properties`：
   ```properties
   # Windows:
   sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
   
   # macOS:
   sdk.dir=/Users/你的用户名/Library/Android/sdk
   
   # Linux:
   sdk.dir=/home/你的用户名/Android/Sdk
   ```

4. **构建 APK**：
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

5. **找到 APK**：
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

**上传 APK：**
1. 将 `app-debug.apk` 拖到 Release 页面的上传区域
2. 等待上传完成

### 步骤 4：发布

点击 **`Publish release`** 按钮

---

## 📥 用户下载链接

发布后，用户可以访问：

| 内容 | 链接 |
|------|------|
| 仓库首页 | https://github.com/Alasdair65/BluetoothMusicApp |
| 下载 APK | https://github.com/Alasdair65/BluetoothMusicApp/releases/tag/v1.0.0 |
| 源码克隆 | `git clone https://github.com/Alasdair65/BluetoothMusicApp.git` |

---

## 🎯 快速测试流程

```
1. 访问 Release 页面
   ↓
2. 下载 app-debug.apk
   ↓
3. 传输到手机
   ↓
4. 安装 APK
   ↓
5. 打开应用，授予权限
   ↓
6. 开启无障碍权限（重要！）
   ↓
7. 配置 BGM 和播放列表
   ↓
8. 连接蓝牙测试
```

---

## ⚙️ 无障碍权限设置（必须）

安装后必须开启无障碍权限：

```
设置 → 无障碍 → 已下载的服务 → 蓝牙音乐控制 → 开启
```

**不同品牌手机：**
- **小米**: 设置 → 更多设置 → 无障碍
- **华为**: 设置 → 辅助功能 → 无障碍
- **OPPO**: 设置 → 系统设置 → 无障碍
- **vivo**: 设置 → 快捷与辅助 → 无障碍
- **三星**: 设置 → 辅助功能

---

## 📋 检查清单

- [ ] 访问 Release 创建页面
- [ ] 填写 Tag: v1.0.0
- [ ] 填写标题和描述
- [ ] 在本地构建 APK
- [ ] 上传 APK 文件
- [ ] 点击 Publish
- [ ] 测试下载链接
- [ ] 删除泄露的 Token

---

## 🔒 安全最佳实践

1. **不要在聊天中分享 Token**
2. **使用环境变量存储 Token**
3. **定期轮换 Token**
4. **使用最小权限原则**

---

**现在请按照步骤创建 Release！** 🚀
