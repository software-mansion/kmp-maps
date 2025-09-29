plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetBrains.compose) apply false
    alias(libs.plugins.jetBrains.dokka) apply false
    alias(libs.plugins.jetBrains.kotlin.multiplatform) apply false
    alias(libs.plugins.jetBrains.kotlin.plugin.compose) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

buildscript {
    dependencies { classpath(libs.google.android.libraries.mapsPlatform.secretsGradlePlugin) }
}
