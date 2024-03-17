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

}

dependencies {

    implementation(libs.timber)

}
