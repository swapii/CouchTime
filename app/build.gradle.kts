import com.google.protobuf.gradle.id
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    plugin(libs.plugins.android.application)
    plugin(libs.plugins.kotlin.ksp)
    plugin(libs.plugins.kotlin.android)
    plugin(libs.plugins.dagger.hilt.android)
    plugin(libs.plugins.protobuf)
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

androidComponents {
    // https://github.com/google/ksp/issues/1590#issuecomment-1846036028
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val capName = variant.name.capitalized()
            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
                setSource(tasks.getByName("generate${capName}Proto").outputs)
            }
        }
    }
}

protobuf {
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {

    implementation(libs.android.x.activity.compose)
    implementation(libs.android.x.media3.exoplayer)
    implementation(libs.android.x.media3.exoplayer.hls)

    implementation(platform(libs.android.x.compose.bom))
    implementation(libs.android.x.compose.material3)

    implementation(libs.store)

    implementation(libs.retrosheet)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.moshi.kotlin)

    implementation(libs.android.x.room.ktx)
    ksp(libs.android.x.room.compiler)

    implementation(libs.android.x.datastore)
    ksp(libs.android.x.datastore)

    implementation(libs.protobuf.javalite)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.timber)

}
