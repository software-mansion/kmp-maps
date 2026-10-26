import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetBrains.compose)
    alias(libs.plugins.jetBrains.dokka)
    alias(libs.plugins.jetBrains.kotlin.multiplatform)
    alias(libs.plugins.jetBrains.kotlin.plugin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.jetBrains.kotlin.plugin.serialization)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
    android {
        namespace = "com.swmansion.kmpmaps.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    iosArm64()
    iosSimulatorArm64()
    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries {
                framework {
                    baseName = "core"
                    isStatic = true
                }
            }
        }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetBrains.compose.components.resources)
            implementation(libs.jetBrains.compose.foundation)
            implementation(libs.jetBrains.compose.material3)
            implementation(libs.jetBrains.compose.runtime)
            implementation(libs.jetBrains.compose.ui)
            implementation(libs.jetBrains.androidX.lifecycle.runtimeCompose)
            implementation(libs.jetBrains.androidX.lifecycle.viewmodelCompose)
            implementation(libs.jetBrains.compose.materialIconsExtended)
            implementation(libs.jetBrains.kotlinX.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.google.accompanist.permissions)
            implementation(libs.google.android.gms.playServicesMaps)
            implementation(libs.google.maps.android.mapsCompose)
            implementation(libs.google.maps.android.mapsComposeUtils)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.jetBrains.kotlinX.coroutinesSwing)
            implementation(libs.kevinnZou.composeWebViewMultiplatformDesktop)
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
