import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetBrains.compose)
    alias(libs.plugins.jetBrains.kotlin.multiplatform)
    alias(libs.plugins.jetBrains.kotlin.plugin.compose)
    alias(libs.plugins.buildkonfig)
}

fun readMapsApiKey(fileName: String): String? =
    providers
        .fileContents(rootProject.layout.projectDirectory.file(fileName))
        .asText
        .orNull
        ?.let { Properties().apply { load(it.reader()) }.getProperty("MAPS_API_KEY") }

val googleMapsApiKey: String by
    extra(readMapsApiKey("local.properties") ?: readMapsApiKey("local.defaults.properties") ?: "")

kotlin {
    jvmToolchain(17)
    android {
        namespace = "com.swmansion.kmpmaps.sample"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources { enable = true }
        withHostTest {}
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    iosArm64()
    iosSimulatorArm64()
    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries {
                framework {
                    baseName = "Sample"
                    isStatic = true
                    binaryOption("bundleId", "com.swmansion.kmpmaps.sample")
                }
            }
        }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetBrains.compose.components.resources)
            implementation(libs.jetBrains.compose.uiToolingPreview)
            implementation(libs.jetBrains.compose.foundation)
            implementation(libs.jetBrains.compose.material3)
            implementation(libs.jetBrains.compose.runtime)
            implementation(libs.jetBrains.compose.ui)
            implementation(libs.jetBrains.androidX.lifecycle.runtimeCompose)
            implementation(libs.jetBrains.androidX.lifecycle.viewmodelCompose)
            implementation(libs.jetBrains.compose.materialIconsExtended)
            implementation(project(":kmp-maps:core"))
            implementation(project(":kmp-maps:google-maps"))
        }
        commonTest.dependencies { implementation(libs.jetBrains.kotlin.test) }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.jetBrains.kotlinX.coroutinesSwing)
            implementation(libs.kcef)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.swmansion.kmpmaps.sample.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.swmansion.kmpmaps.sample.MainKt"
            packageVersion = "1.0.0"
        }

        jvmArgs(
            "--add-opens",
            "java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens",
            "java.desktop/sun.lwawt=ALL-UNNAMED",
            "--add-opens",
            "java.desktop/sun.lwawt.macosx=ALL-UNNAMED",
        )
    }
}

tasks.withType<JavaExec> {
    jvmArgs(
        "--add-opens",
        "java.desktop/sun.awt=ALL-UNNAMED",
        "--add-opens",
        "java.desktop/sun.lwawt=ALL-UNNAMED",
        "--add-opens",
        "java.desktop/sun.lwawt.macosx=ALL-UNNAMED",
    )
}

buildkonfig {
    packageName = "com.swmansion.kmpmaps.sample"

    defaultConfigs { buildConfigField(STRING, "MAPS_API_KEY", googleMapsApiKey) }
}

tasks.named("compileKotlinMetadata") { dependsOn("generateBuildKonfig") }
