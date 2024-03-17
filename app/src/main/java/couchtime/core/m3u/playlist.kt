package couchtime.core.m3u

data class M3uPlaylistItem(
    val extInf: ExtInf,
    val extGrp: String,
    val address: String,
) {

    data class ExtInf(
        val runtimeSeconds: Int,
        val displayTitle: String,
        val properties: Map<String, String>,
    )

}

internal fun Sequence<String>.parseM3uPlaylist(): Sequence<M3uPlaylistItem> {

    val iterator = iterator()

    val header = iterator.next()
    require(header == "#EXTM3U")

    return iterator.asSequence()
        .chunked(3)
        .map { lines: List<String> ->

            require(lines.size == 3)

            val extInf = lines[0].parseAsExtInf()
            val extGrp = lines[1].parseAsExtGrp()
            val addressString = lines[2]

            M3uPlaylistItem(
                extInf = extInf,
                extGrp = extGrp,
                address = addressString,
            )
        }
}

private fun String.parseAsExtInf(): M3uPlaylistItem.ExtInf {

    val prefix = "#EXTINF:"
    require(startsWith(prefix))

    val withoutPrefix = drop(prefix.length)

    val durationSecondsString = withoutPrefix.takeWhile { it.isDigit() }
    val runtimeSeconds = durationSecondsString.toInt()

    val withoutSeconds = withoutPrefix.drop(durationSecondsString.length)

    val (propertiesString, displayTitle) = withoutSeconds.split(',')
        .also {
            require(it.size == 2)
        }

    val properties: Map<String, String> =
        propertiesString
            .trim()
            .split(' ')
            .associate {
                val (propertyName, propertyValue) = it.split('=')
                propertyName to propertyValue
            }

    return M3uPlaylistItem.ExtInf(
        runtimeSeconds = runtimeSeconds,
        displayTitle = displayTitle,
        properties = properties,
    )
}

private fun String.parseAsExtGrp(): String {
    val prefix = "#EXTGRP:"
    require(startsWith(prefix))
    return drop(prefix.length)
}
