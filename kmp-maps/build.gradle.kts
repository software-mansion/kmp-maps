plugins { alias(libs.plugins.jetBrains.dokka) }

subprojects {
    group = "com.swmansion.kmpmaps"
    version = "0.2.2"
}

dependencies {
    dokka(project(":kmp-maps:kmp-maps"))
    dokka(project(":kmp-maps:kmp-gmaps"))
}

dokka {
    moduleName = "KMP Maps"
    pluginsConfiguration.html {
        footerMessage =
            """
            © <a href="https://swmansion.com" rel="noopener noreferrer" target="_blank">Software Mansion</a> 2025.
            All trademarks and copyrights belong to their respective owners.
            """
                .trimIndent()
        customStyleSheets.from("$rootDir/logo-styles.css")
    }
}
