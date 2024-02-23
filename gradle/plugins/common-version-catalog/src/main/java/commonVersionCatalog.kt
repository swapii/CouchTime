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

        group("kotlin", "1.9.22") {
            plugin("android", "org.jetbrains.kotlin.android", version)
        }

        library("timber", "com.jakewharton.timber:timber:5.0.1")

    }

}
