enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    includeBuild("gradle/plugins/common-version-catalog")

}

plugins {
    id("common-version-catalog")
}

dependencyResolutionManagement {

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        commonVersionCatalog("libs")
    }

}

include(
    ":app",
    ":common:m3u",
    ":tool:m3u-to-csv",
)
