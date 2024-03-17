pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            plugin("android.application", "com.android.application").version("8.2.2")
            plugin("kotlin.android", "org.jetbrains.kotlin.android").version("1.9.22")
            library("timber", "com.jakewharton.timber:timber:5.0.1")
        }
    }
}

include(":app")
