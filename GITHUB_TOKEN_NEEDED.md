# 🔐 需要 GitHub Token

为了自动推送代码和创建 Release，需要配置 GitHub 认证。

## 选项 1：提供 GitHub Token（推荐）

1. 访问：https://github.com/settings/tokens/new
2. 勾选权限：
   - ✅ `repo` (Full control of private repositories)
   - ✅ `workflow` (Update GitHub Action workflows)
3. 生成 token
4. 将 token 发送给我

然后我会：
- 推送代码到 GitHub
- 创建 v1.0.0 tag
- 触发自动构建
- APK 会自动上传到 Release

## 选项 2：在 GitHub 上手动操作

1. 访问：https://github.com/Alasdair65/BluetoothMusicApp
2. 点击 "Actions" 标签
3. 点击左侧 "Build APK"
4. 点击 "Run workflow" → "Run workflow"
5. 等待构建完成（约 5-10 分钟）
6. APK 会出现在 Release 或 Artifacts 中

## 选项 3：使用 GitHub Desktop

1. 下载：https://desktop.github.com
2. 克隆仓库
3. 拉取最新更改
4. 创建 Release 并上传 APK

---

**推荐选项 1**：给我 GitHub token，我全自动完成。
