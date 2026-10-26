plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetBrains.compose)
    alias(libs.plugins.jetBrains.kotlin.plugin.compose)
}

val googleMapsApiKey: String by parent!!.extra

android {
    namespace = "com.swmansion.kmpmaps.sample.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.swmansion.kmpmaps.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = googleMapsApiKey
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes { getByName("release") { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":sample"))
    implementation(libs.androidX.activity.compose)
    implementation(libs.jetBrains.compose.runtime)
    implementation(libs.jetBrains.compose.uiToolingPreview)
    debugImplementation(libs.jetBrains.compose.uiTooling)
}
