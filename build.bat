@echo off
REM 蓝牙音乐自动化应用 - 快速构建脚本 (Windows)
REM ==========================================

echo.
echo 🎵 蓝牙音乐自动化应用 - 构建脚本
echo =================================

REM 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误：未找到 Java，请先安装 JDK 11 或更高版本
    exit /b 1
)

echo ✅ Java 已安装

REM 检查 ANDROID_HOME
if "%ANDROID_HOME%"=="" (
    if "%ANDROID_SDK_ROOT%"=="" (
        echo ⚠️  警告：未设置 ANDROID_HOME 或 ANDROID_SDK_ROOT
        echo    请确保已安装 Android SDK 并设置环境变量
        echo.
        pause
    )
)

REM 进入项目目录
cd /d "%~dp0"

REM 检查 Gradle Wrapper
if not exist "gradlew.bat" (
    echo ❌ 错误：未找到 gradlew.bat，请使用 Android Studio 打开项目
    exit /b 1
)

REM 构建
echo.
echo 🔨 开始构建...
echo.

if "%1"=="release" (
    echo 构建 Release 版本...
    call gradlew.bat assembleRelease
    echo.
    echo ✅ Release APK 生成位置：
    echo    app\build\outputs\apk\release\app-release-unsigned.apk
) else (
    echo 构建 Debug 版本...
    call gradlew.bat assembleDebug
    echo.
    echo ✅ Debug APK 生成位置：
    echo    app\build\outputs\apk\debug\app-debug.apk
)

echo.
echo 📱 安装到设备（需要 USB 连接和 ADB）：
echo    adb install app\build\outputs\apk\debug\app-debug.apk
echo.
pause
