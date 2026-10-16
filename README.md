# 分类相机

一个本地优先的 Android 相机原型：先选分类，再拍照；照片直接写入手机媒体库的 `DCIM/<分类名>`，可由手机相册应用读取。

照片不会上传。应用仅请求相机权限；分类名称保存在应用本地偏好设置中。删除分类只会从应用列表移除名称，不会删除相册目录或其中的照片。

## 构建

- Android Studio Ladybug 或更新版本
- JDK 17
- Android SDK 35

用 Android Studio 打开项目并同步 Gradle，然后运行 `app` 配置。手机需运行 Android 10 或更新版本。

也可以直接在项目目录运行 `gradlew.bat assembleRelease`。APK 输出到 `app/build/outputs/apk/release/app-release.apk`；连接开启 USB 调试的手机后可运行 `gradlew.bat installRelease`。当前 Release 变体为测试用途，使用本机 Android debug 密钥签名，不应用于正式商店发布。

## Release 签名保护

Release 构建使用独立的发布密钥；不要把密钥或密码提交到 Git。首次发布时生成一把长期保管的密钥：

```powershell
keytool -genkeypair -v -keystore camera-release.jks -alias camera -keyalg RSA -keysize 2048 -validity 10000
```

从该密钥导出 APK 签名证书 SHA-256 指纹：

```powershell
keytool -list -v -keystore camera-release.jks -alias camera
```

把 `SHA256` 的十六进制指纹（可带冒号）填入 `MainActivity.kt` 的 `RELEASE_CERT_SHA256`。在项目根目录创建未跟踪的 `keystore.properties`：

```properties
storeFile=camera-release.jks
storePassword=替换为密钥库密码
keyAlias=camera
keyPassword=替换为密钥密码
```

把 `camera-release.jks` 放在项目根目录，或将 `storeFile` 改为它相对项目根目录的路径，然后运行 `gradlew.bat assembleRelease`。Debug 构建跳过证书限制；Release 在应用功能入口校验证书 SHA-256，不匹配时显示签名错误并拒绝进入。APK 内的校验只能提高重打包成本，不能替代服务端授权；兑换码仍应由服务器签发并用独立私钥签名。丢失发布密钥会导致后续版本无法作为同一应用覆盖安装。

## 当前范围

- 创建和选择本地分类
- 内置后置相机预览与拍照
- 照片保存到 `DCIM/<分类名>`
- 相机权限请求

尚未实现导入旧照片、编辑分类名称、前置镜头或云端功能。
