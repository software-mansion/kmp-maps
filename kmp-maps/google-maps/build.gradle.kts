plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetBrains.compose)
    alias(libs.plugins.jetBrains.dokka)
    alias(libs.plugins.jetBrains.kotlin.multiplatform)
    alias(libs.plugins.jetBrains.kotlin.plugin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
    kotlin("native.cocoapods")
}

kotlin {
    explicitApi()
    jvmToolchain(17)
    androidTarget { publishLibraryVariants("release") }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "google-maps"
            isStatic = true
        }
    }

    cocoapods {
        summary = "Universal map component for Compose Multiplatform."
        homepage = "https://github.com/software-mansion/kmp-maps"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "google-maps"
            isStatic = true
        }
        pod("GoogleMaps") { version = "10.4.0" }
        pod("Google-Maps-iOS-Utils") {
            version = "6.1.3"
            moduleName = "GoogleMapsUtils"
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
            implementation(project(":kmp-maps:core"))
        }
    }
}

android {
    namespace = "com.swmansion.kmpmaps.googlemaps"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dokka { dokkaPublications.configureEach { suppressInheritedMembers = true } }

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    pom {
        name = "KMP Maps"
        description = "Universal map component for Compose Multiplatform."
        url = "https://github.com/software-mansion/kmp-maps"
        licenses {
            license {
                name = "The MIT License"
                url = "http://www.opensource.org/licenses/mit-license.php"
            }
        }
        scm {
            connection = "scm:git:git://github.com/software-mansion/kmp-maps.git"
            developerConnection = "scm:git:ssh://github.com/software-mansion/kmp-maps.git"
            url = "https://github.com/software-mansion/kmp-maps"
        }
        developers {
            developer {
                id = "arturgesiarz"
                name = "Artur Gęsiarz"
                email = "artur.gesiarz@swmansion.com"
            }
            developer {
                id = "marekkaput"
                name = "Marek Kaput"
                email = "marek.kaput@swmansion.com"
            }
            developer {
                id = "patrickmichalik"
                name = "Patrick Michalik"
                email = "patrick.michalik@swmansion.com"
            }
            developer {
                id = "justynagreda"
                name = "Justyna Gręda"
                email = "justyna.greda@swmansion.com"
            }
        }
    }
}
