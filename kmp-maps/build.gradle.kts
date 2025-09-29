group = "com.swmansion.kmpmaps"

version = "0.1.0"

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetBrains.compose)
    alias(libs.plugins.jetBrains.dokka)
    alias(libs.plugins.jetBrains.kotlin.multiplatform)
    alias(libs.plugins.jetBrains.kotlin.plugin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
    androidTarget { publishLibraryVariants("release") }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "kmp-maps"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.jetBrains.androidX.lifecycle.runtimeCompose)
            implementation(libs.jetBrains.androidX.lifecycle.viewmodelCompose)
        }

        androidMain.dependencies {
            implementation(libs.google.android.gms.playServicesMaps)
            implementation(libs.google.maps.android.mapsCompose)
        }
    }
}

android {
    namespace = "com.swmansion.kmpmaps"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set(project.name)
            
            // Link to external documentation
            externalDocumentationLink {
                url.set(uri("https://developer.android.com/reference/").toURL())
                packageListUrl.set(uri("https://developer.android.com/reference/androidx/package-list").toURL())
            }
            
            externalDocumentationLink {
                url.set(uri("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
            }
        }
    }
}
