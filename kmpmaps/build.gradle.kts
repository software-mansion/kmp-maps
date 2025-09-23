plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
    androidTarget { publishLibraryVariants("release", "debug") }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KmpMaps"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.google.maps)
            implementation(libs.maps.compose)
        }

        androidMain.dependencies {
        }

        iosMain.dependencies {
        }

        commonTest.dependencies { implementation(libs.kotlin.test) }
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
