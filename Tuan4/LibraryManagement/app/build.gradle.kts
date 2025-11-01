plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

android {
    namespace = "com.example.librarymanagement"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.librarymanagement"
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
    // Compose BOM để đồng bộ version cho toàn bộ artifacts "androidx.compose:*"
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))

    // Compose core
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ✅ Material3 (KHÔNG ghi version khi đã dùng BOM)
    implementation("androidx.compose.material3:material3")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Icons (để trống version vì theo BOM)
    implementation("androidx.compose.material:material-icons-extended")
}
