import org.jetbrains.kotlin.gradle.dsl.JvmTarget

version = "0.7.0"

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetBrains.compose)
    alias(libs.plugins.jetBrains.kotlin.multiplatform)
    alias(libs.plugins.jetBrains.kotlin.plugin.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("native.cocoapods")
}

kotlin {
    jvmToolchain(22)
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Sample"
            isStatic = true
            binaryOption("bundleId", "com.swmansion.kmpmaps.sample")
        }
    }

    jvm("desktop")

    cocoapods {
        summary = "Universal map component for Compose Multiplatform."
        homepage = "https://github.com/software-mansion/kmp-maps"
        version = "0.7.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "Sample"
            isStatic = true
        }

        pod("GoogleMaps") { version = "10.4.0" }
        pod("Google-Maps-iOS-Utils") {
            version = "6.1.3"
            moduleName = "GoogleMapsUtils"
        }
    }

    sourceSets {
        val desktopMain by getting
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidX.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.jetBrains.androidX.lifecycle.runtimeCompose)
            implementation(libs.jetBrains.androidX.lifecycle.viewmodelCompose)
            implementation(compose.materialIconsExtended)
            implementation(project(":kmp-maps:core"))
            implementation(project(":kmp-maps:google-maps"))
        }
        commonTest.dependencies { implementation(libs.jetBrains.kotlin.test) }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("org.openjfx:javafx-controls:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")
            implementation("org.openjfx:javafx-swing:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")
            implementation("org.openjfx:javafx-web:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")
            implementation("org.openjfx:javafx-graphics:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")
            implementation("org.openjfx:javafx-base:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")
            implementation("org.openjfx:javafx-media:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")
            implementation("org.openjfx:javafx-fxml:${project.properties["javafxVersion"]}:${project.properties["javafxClassifier"]}")


        }
    }
}

android {
    namespace = "com.swmansion.kmpmaps.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.swmansion.kmpmaps.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes { getByName("release") { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { buildConfig = true }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
            )
            packageName = "com.swmansion.kmpmaps.sample"
            packageVersion = "1.0.0"
        }
        jvmArgs += listOf("--add-modules", "javafx.controls,javafx.swing,javafx.web")
    }
}

dependencies { debugImplementation(compose.uiTooling) }

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}
