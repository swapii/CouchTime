plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {

    namespace = "couchtime"

    compileSdk = 34

    defaultConfig {

        applicationId = "couch.time"
        versionCode = 1
        versionName = "1.0.0"

        minSdk = 31

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.android.x.compose.compiler.get()
    }

}

dependencies {

    implementation(libs.android.x.activity.compose)
    implementation(libs.android.x.media3.exoplayer)
    implementation(libs.android.x.media3.exoplayer.hls)

    implementation(platform(libs.android.x.compose.bom))
    implementation(libs.android.x.compose.material3)

    implementation(libs.timber)

}
