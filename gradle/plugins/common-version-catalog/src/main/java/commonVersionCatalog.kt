import org.gradle.api.initialization.resolve.MutableVersionCatalogContainer

@Suppress(
    "unused", // Used in root settings.gradle.kts
)
fun MutableVersionCatalogContainer.commonVersionCatalog(name: String) {

    catalog(name) {

        group("android") {

            plugin("application", "com.android.application", "8.2.2")

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

            }

        }

        group("store", "5.0.0-beta02") {
            library("org.mobilenativefoundation.store:store5") { version(version) }
        }

        group("dagger", "2.50") {
            plugin("hilt.android", "com.google.dagger.hilt.android", version)
            library("hilt.compiler", "com.google.dagger:hilt-compiler") { version(version) }
            library("hilt.android", "com.google.dagger:hilt-android") { version(version) }
        }

        plugin("ksp", "com.google.devtools.ksp", "1.9.22-1.0.17")

        group("kotlin", "1.9.22") {
            plugin("android", "org.jetbrains.kotlin.android", version)
        }

        library("timber", "com.jakewharton.timber:timber:5.0.1")

    }

}
