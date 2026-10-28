import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val signingProperties = Properties().apply {
    val propertiesFile = rootProject.file("keystore.properties")
    if (propertiesFile.exists()) propertiesFile.inputStream().use { load(it) }
}
val releaseKeystorePath = signingProperties.getProperty("storeFile")
val internalSigningPropertiesFile = providers.gradleProperty("internalSigningPropertiesFile")
    .orNull?.let { rootProject.file(it) }
val internalSigningProperties = Properties().apply {
    internalSigningPropertiesFile?.takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val membershipApiBaseUrl = providers.gradleProperty("membershipApiBaseUrl")
    .orElse("https://replace-with-membership-api.example")
val appUpdateManifestUrl = providers.gradleProperty("appUpdateManifestUrl")
    .orElse("https://api.github.com/repos/qiuyinlan/notes/contents/updates/stable.json?ref=main")
val internalUpdateManifestUrl = providers.gradleProperty("internalUpdateManifestUrl")
    .orElse("https://api.github.com/repos/qiuyinlan/notes/contents/updates/latest.json?ref=main")

android {
    namespace = "com.qiuyinlan.suixinji"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.qiuyinlan.suixinji"
        minSdk = 29
        targetSdk = 35
        versionCode = 42
        versionName = "0.1.14"
        buildConfigField("String", "MEMBERSHIP_API_BASE_URL", "\"${membershipApiBaseUrl.get()}\"")
        buildConfigField("String", "APP_UPDATE_MANIFEST_URL", "\"${appUpdateManifestUrl.get()}\"")
    }

    buildFeatures { buildConfig = true }

    signingConfigs {
        create("release") {
            if (!releaseKeystorePath.isNullOrBlank()) {
                storeFile = rootProject.file(releaseKeystorePath)
                storePassword = signingProperties.getProperty("storePassword")
                keyAlias = signingProperties.getProperty("keyAlias")
                keyPassword = signingProperties.getProperty("keyPassword")
            }
        }
        create("internal") {
            val internalKeystorePath = internalSigningProperties.getProperty("storeFile")
            if (!internalKeystorePath.isNullOrBlank()) {
                val isAbsolutePath = internalKeystorePath.startsWith("/") ||
                    Regex("^[A-Za-z]:[\\\\/]").containsMatchIn(internalKeystorePath)
                storeFile = if (isAbsolutePath) rootProject.file(internalKeystorePath) else
                    internalSigningPropertiesFile?.parentFile?.resolve(internalKeystorePath) ?: rootProject.file(internalKeystorePath)
                storePassword = internalSigningProperties.getProperty("storePassword")
                keyAlias = internalSigningProperties.getProperty("keyAlias")
                keyPassword = internalSigningProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = if (releaseKeystorePath.isNullOrBlank()) {
                signingConfigs.getByName("debug")
            } else {
                signingConfigs.getByName("release")
            }
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
        create("internal") {
            initWith(getByName("release"))
            applicationIdSuffix = ".internal"
            versionNameSuffix = "-internal"
            signingConfig = signingConfigs.getByName("internal")
            buildConfigField("String", "APP_UPDATE_MANIFEST_URL", "\"${internalUpdateManifestUrl.get()}\"")
            matchingFallbacks += listOf("release")
        }
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.media3:media3-exoplayer:1.6.0")
    implementation("androidx.media3:media3-ui:1.6.0")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.mlkit:text-recognition-chinese:16.0.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
}
