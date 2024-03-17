enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    includeBuild("gradle/plugins/gradle-common")
    includeBuild("gradle/plugins/common-version-catalog")

}

plugins {
    id("gradle-common")
    id("common-version-catalog")
    id("com.github.swapii.gmai") version "47a644ec"
}

dependencyResolutionManagement {

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    versionCatalogs {
        commonVersionCatalog("libs")
    }

}
