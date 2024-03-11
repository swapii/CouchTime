import org.gradle.api.initialization.resolve.MutableVersionCatalogContainer

@Suppress(
    "unused", // Used in root settings.gradle.kts
)
fun MutableVersionCatalogContainer.commonVersionCatalog(name: String) {

    catalog(name) {

        group("android") {

            val pluginVersion = "8.3.0"
            plugin("application", "com.android.application").version(pluginVersion)
            plugin("library", "com.android.library").version(pluginVersion)

            group("x") {

                library("activity.compose", "androidx.activity:activity-compose:1.8.2")

                group("compose") {
                    version("compiler", "1.5.9")
                    library("bom", "androidx.compose:compose-bom:2024.02.00")
                    library("material3", "androidx.compose.material3:material3")
                }

                group("media3", "1.2.1") {
                    library("exoplayer", "androidx.media3:media3-exoplayer:$version")
                    library("exoplayer.hls", "androidx.media3:media3-exoplayer-hls:$version")
                }

                group("room", "2.6.1") {
                    library("compiler", "androidx.room:room-compiler") { version(version) }
                    library("ktx", "androidx.room:room-ktx") { version(version) }
                }

                library("datastore", "androidx.datastore:datastore:1.0.0")

            }

        }

        group("store", "5.0.0-beta02") {
            library("org.mobilenativefoundation.store:store5") { version(version) }
        }

        group("dagger", "2.50") {
            plugin("hilt.android", "com.google.dagger.hilt.android").version(version)
            library("hilt.compiler", "com.google.dagger:hilt-compiler") { version(version) }
            library("hilt.android", "com.google.dagger:hilt-android") { version(version) }
        }

        group("kotlin", "1.9.22") {
            plugin("jvm", "org.jetbrains.kotlin.jvm").version(version)
            plugin("android", "org.jetbrains.kotlin.android").version(version)
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").version(version)
            plugin("ksp", "com.google.devtools.ksp").version("$version-1.0.17")
            group("x") {
                group("serialization", "1.6.3") {
                    library("json", "org.jetbrains.kotlinx:kotlinx-serialization-json") { version(version) }
                }
            }
        }

        library("retrosheet", "com.github.theapache64:retrosheet:2.0.1")

        group("retrofit", "2.9.0") {
            library("converter.kotlinx.serialization", "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
        }

        library("okhttp", "com.squareup.okhttp3:okhttp:4.12.0")

        group("protobuf", "3.25.3") {
            plugin("com.google.protobuf").version("0.9.4")
            library("javalite", "com.google.protobuf:protobuf-javalite") { version(version) }
        }

        library("timber", "com.jakewharton.timber:timber:5.0.1")

    }

}
