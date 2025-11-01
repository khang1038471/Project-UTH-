plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.example.dataflownav"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dataflownav"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables { useSupportLibrary = true }
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))

    // Compose core
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Material 3 (Compose)
    implementation("androidx.compose.material3:material3")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Coil để tải logo từ Internet
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Icons (tùy chọn)
    implementation("androidx.compose.material:material-icons-extended")

    // XML Material theme để set theme trong Manifest (tránh lỗi)
    implementation("com.google.android.material:material:1.12.0")
}
