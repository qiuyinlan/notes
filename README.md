# 分类相机

一个本地优先的 Android 相机原型：先选分类，再拍照；照片直接写入手机媒体库的 `DCIM/<分类名>`，可由手机相册应用读取。

照片不会上传。应用仅请求相机权限；分类名称保存在应用本地偏好设置中。删除分类只会从应用列表移除名称，不会删除相册目录或其中的照片。

## 构建

- Android Studio Ladybug 或更新版本
- JDK 17
- Android SDK 35

用 Android Studio 打开项目并同步 Gradle，然后运行 `app` 配置。手机需运行 Android 10 或更新版本。

也可以直接在项目目录运行 `gradlew.bat assembleDebug`。APK 输出到 `app/build/outputs/apk/debug/app-debug.apk`；连接开启 USB 调试的手机后可运行 `gradlew.bat installDebug`。

## 当前范围

- 创建和选择本地分类
- 内置后置相机预览与拍照
- 照片保存到 `DCIM/<分类名>`
- 相机权限请求

尚未实现导入旧照片、编辑分类名称、前置镜头或云端功能。
