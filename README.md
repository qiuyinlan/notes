# 随心记

一个本地优先的 Android 个人记录应用：先记录，再整理。首屏可以直接写下随记，使用 `#生活/阅读` 自动建立层级分类，也可以在整理台逐条归类。

随心记使用独立包名 `com.qiuyinlan.suixinji`，与原相册应用 `com.localalbum.camera` 可以同时安装，不会互相覆盖。

随记、分类、日记和未来的信都保存在手机本地，不需要账号或网络。Markdown 日记保留原文；未来的信按设备本地日期展示。

## 构建

- Android Studio Ladybug 或更新版本
- JDK 17
- Android SDK 35

用 Android Studio 打开项目并同步 Gradle，然后运行 `app` 配置。手机需运行 Android 10 或更新版本。

也可以直接在项目目录运行 `gradlew.bat assembleRelease`。APK 输出到 `app/build/outputs/apk/release/app-release.apk`；连接开启 USB 调试的手机后可运行 `gradlew.bat installRelease`。项目存在 `keystore.properties` 时使用发布密钥；没有发布密钥时回退到 Android debug 密钥，仅用于真机测试。

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

- 快速随记和本地保存
- `#一级/二级` 分类路径和分类空间
- 随记箱整理、归类、跳过、删除
- Markdown 日记编辑
- 未来的信和到期展示

相册整理代码仍保留在项目中作为平台能力参考，当前启动入口为随心记。
