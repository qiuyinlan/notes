# 随心记

独立的本地随记 Android 应用，包名为 `com.qiuyinlan.suixinji`。

构建：

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot'
$env:ANDROID_HOME='C:\Users\29785\AppData\Local\Android\Sdk'
$env:Path="$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:Path"
.\gradlew.bat assembleRelease
```
