plugins {
    alias(libs.plugins.android.application)
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
    
}
