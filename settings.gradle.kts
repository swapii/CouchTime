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
            library("android.x.activity.compose", "androidx.activity", "activity-compose").version("1.8.2")
            version("android.x.compose.compiler", "1.5.9")
            library("android.x.compose.bom", "androidx.compose", "compose-bom").version("2024.02.00")
            library("android.x.compose.material3", "androidx.compose.material3", "material3").withoutVersion()
            library("android.x.media3.exoplayer", "androidx.media3", "media3-exoplayer").version("1.2.1")
            library("android.x.media3.exoplayer.hls", "androidx.media3", "media3-exoplayer-hls").version("1.2.1")
        }
    }
}

include(":app")
