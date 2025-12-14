import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

// --- 安全读取 key.properties（如果存在） ---
val keystorePropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

// 安全获取属性，返回 null 或 非空字符串
fun propOrNull(name: String): String? {
    return keystoreProperties.getProperty(name)?.takeIf { it.isNotBlank() }
}

android {
    namespace = "com.xycz.simple_live"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.xycz.simple_live"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    // 仅在 storeFile 非空时创建 signingConfigs.release
    val storeFilePath = propOrNull("storeFile")
    if (!storeFilePath.isNullOrBlank()) {
        signingConfigs {
            create("release") {
                // 这里 file(...) 只在 storeFilePath 非空时调用，避免 file("") 抛错
                storeFile = file(storeFilePath)
                storePassword = propOrNull("storePassword")
                keyAlias = propOrNull("keyAlias")
                keyPassword = propOrNull("keyPassword")
                isV1SigningEnabled = true
                isV2SigningEnabled = true
            }
        }
    }

    // 在 buildTypes.release 中仅在 signingConfigs 有 release 时才应用签名配置
    buildTypes {
        getByName("release") {
            // 保持原有 release 配置（如 minifyEnabled/ProGuard）
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            // 未配置签名：保持不设置 signingConfig，从而输出 unsigned APK
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                // Default file with automatically generated optimization rules.
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

flutter {
    source = "../.."
}
