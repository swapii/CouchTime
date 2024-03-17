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
        }
    }
}

include(":app")
