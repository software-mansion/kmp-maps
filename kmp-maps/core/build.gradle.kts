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
            baseName = "core"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.jetBrains.androidX.lifecycle.runtimeCompose)
            implementation(libs.jetBrains.androidX.lifecycle.viewmodelCompose)
            implementation(compose.materialIconsExtended)
        }

        androidMain.dependencies {
            implementation(libs.google.accompanist.permissions)
            implementation(libs.google.android.gms.playServicesMaps)
            implementation(libs.google.maps.android.mapsCompose)
            implementation(libs.google.maps.android.mapsComposeUtils)
        }

        desktopMain.dependencies { implementation(compose.desktop.currentOs) }
    }
}

android {
    namespace = "com.swmansion.kmpmaps.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
            )
            packageName = "com.swmansion.kmpmaps.core"
            packageVersion = "1.0.0"
        }
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
