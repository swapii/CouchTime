package couchtime.core.m3u

import android.net.Uri
import androidx.core.net.toUri
import timber.log.Timber

internal fun Sequence<String>.parsePlaylist(): Sequence<PlaylistChannelData> {

    val iterator = iterator()

    val header = iterator.next()
    require(header == "#EXTM3U")

    return iterator.asSequence()
        .chunked(3)
        .mapIndexed { index, lines: List<String> ->

            if (index % 250 == 0) {
                Timber.v("Parse playlist channel index $index")
            }

            require(lines.size == 3)

            val addressString = lines[2]
            val addressSuffix = "/index.m3u8"
            require(addressString.endsWith(addressSuffix))

            val id: Long = addressString.removeSuffix(addressSuffix).takeLastWhile { it.isDigit() }.toLong()

            val extInf = lines[0].parseAsExtInf()
            val extGrp = lines[1].parseAsExtGrp()
            val address = addressString.toUri()

            PlaylistChannelData(
                id = id,
                name = extInf.displayTitle,
                group = extGrp,
                address = address,
            )
        }
}

data class PlaylistChannelData(
    val id: Long,
    val name: String,
    val group: String,
    val address: Uri,
)

data class ExtInf(
    val runtimeSeconds: Int,
    val displayTitle: String,
    val properties: Map<String, String>,
)

fun String.parseAsExtGrp(): String {
    val prefix = "#EXTGRP:"
    require(startsWith(prefix))
    return drop(prefix.length)
}

fun String.parseAsExtInf(): ExtInf {

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

    return ExtInf(
        runtimeSeconds = runtimeSeconds,
        displayTitle = displayTitle,
        properties = properties,
    )
}
