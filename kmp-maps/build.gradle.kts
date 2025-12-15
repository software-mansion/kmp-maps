plugins { alias(libs.plugins.jetBrains.dokka) }

subprojects {
    group = "com.swmansion.kmpmaps"
    version = "0.6.1"
}

dependencies {
    dokka(project(":kmp-maps:core"))
    dokka(project(":kmp-maps:google-maps"))
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
