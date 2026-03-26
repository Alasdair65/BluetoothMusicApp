# Android SDK 镜像源汇总

## ✅ 可用镜像站

### 1. AndroidDevTools.cn (推荐)
**网址**: https://www.androiddevtools.cn/

这是一个专门收集 Android 开发工具镜像的中文网站，提供：
- Android Studio 下载（官方直链）
- Android SDK Tools 下载
- Android Emulator 下载
- Gradle 下载

**Linux SDK Tools 直接下载链接**:
```
https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip (130 MB)
```

### 2. 阿里云镜像站
**网址**: https://developer.aliyun.com/mirror/

提供 Gradle、Maven 等镜像，可用于加速 Gradle 依赖下载。

### 3. 腾讯开源镜像站
**网址**: https://mirrors.cloud.tencent.com/

### 4. 清华大学 TUNA
**网址**: https://mirrors.tuna.tsinghua.edu.cn/

注意：AndroidStudio 路径已失效 (404)，但其他开源软件镜像仍可用。

### 5. 北京外国语大学 BFSU
**网址**: https://mirrors.bfsu.edu.cn/

注意：AndroidSDK 路径已失效 (404)。

---

## 🔧 推荐配置方案

### 方案 A：使用 androiddevtools.cn 下载 SDK

```bash
cd /home/admin/Android/Sdk

# 下载 SDK Tools
wget https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip

# 解压
unzip sdk-tools-linux-3859397.zip
mkdir -p cmdline-tools
mv tools cmdline-tools/latest

# 安装 Build Tools 和 Platform
yes | ./cmdline-tools/latest/bin/sdkmanager --install "build-tools;33.0.2" "platforms;android-33" "platform-tools"

# 接受许可证
yes | ./cmdline-tools/latest/bin/sdkmanager --licenses
```

### 方案 B：使用阿里云 Gradle 镜像

修改 `gradle/wrapper/gradle-wrapper.properties`:
```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
```

或在 `~/.gradle/gradle.properties` 添加：
```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true

# 阿里云 Maven 镜像
maven.repo.local=/home/admin/.m2/repository
```

### 方案 C：在 build.gradle 中配置镜像

修改项目级 `build.gradle.kts` 或 `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}
```

---

## 📝 注意事项

1. **不要使用迅雷下载** - androiddevtools.cn 明确提示避免使用迅雷下载，防止类似 XCodeGhost 的安全事件
2. **优先使用官方链接** - 大部分下载链接实际跳转到 Google 官方服务器
3. **配置国内镜像** - Gradle 依赖建议使用阿里云或腾讯云镜像加速

---

## 🔗 相关链接

- AndroidDevTools: https://www.androiddevtools.cn/
- 阿里云镜像：https://developer.aliyun.com/mirror/
- 腾讯镜像：https://mirrors.cloud.tencent.com/
- 清华大学镜像：https://mirrors.tuna.tsinghua.edu.cn/
