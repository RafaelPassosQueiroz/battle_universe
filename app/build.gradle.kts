import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projetomarvel"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.projetomarvel"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val secretProperties = Properties()
        val secretFile = rootProject.file("local.properties")
        if (secretFile.exists()) {
            secretFile.inputStream().use { input ->
                secretProperties.load(input)
            }
        }
        val comicVineKey = secretProperties.getProperty("COMIC_VINE_API_KEY", "")
        buildConfigField("String", "COMIC_VINE_API_KEY", "\"${comicVineKey.replace("\\", "\\\\").replace("\"", "\\\"")}\"")
        val geminiKey = secretProperties.getProperty("GEMINI_API_KEY", System.getenv("GEMINI_API_KEY") ?: "")
        buildConfigField("String", "GEMINI_API_KEY", "\"${geminiKey.replace("\\", "\\\\").replace("\"", "\\\"")}\"")
    }
    buildFeatures { buildConfig = true }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.mlkit:translate:17.0.3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
