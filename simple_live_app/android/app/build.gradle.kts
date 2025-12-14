import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

// --- Safely read key.properties (if exists) ---
val keystorePropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

// Safely get property, return null or non-empty string
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

    // Only create signingConfigs.release when storeFile is not empty
    val storeFilePath = propOrNull("storeFile")
    if (storeFilePath != null) {
        signingConfigs {
            create("release") {
                // Here file(...) is only called when storeFilePath is not empty, avoiding file("") error
                storeFile = file(storeFilePath)
                storePassword = propOrNull("storePassword")
                keyAlias = propOrNull("keyAlias")
                keyPassword = propOrNull("keyPassword")
                isV1SigningEnabled = true
                isV2SigningEnabled = true
            }
        }
    }

    // In buildTypes.release, only apply signing config when signingConfigs has release
    buildTypes {
        getByName("release") {
            // Keep original release configuration (like minifyEnabled/ProGuard)
            if (signingConfigs.findByName("release") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            // No signing configured: keep signingConfig unset, thus output unsigned APK
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
