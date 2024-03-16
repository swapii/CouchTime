package couchtime.xmltv

import android.net.Uri
import androidx.core.net.toUri
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.time.Instant
import java.util.zip.GZIPInputStream
import javax.xml.parsers.SAXParserFactory

class XmlTvHandler(
    private val handleChannel: (Channel) -> Unit,
) : DefaultHandler() {

    private var channelId: ChannelId? = null
    private var channelDisplayNames: MutableMap<Language, String>? = null
    private var channelDisplayNameLanguage: String? = null
    private var channelIcon: Uri? = null

    private var characters: StringBuilder? = null

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes,
    ) {
        when (qName) {
            "channel" -> {
                channelId = attributes.getValue("id")
                channelDisplayNames = mutableMapOf()
            }

            "display-name" -> {
                channelDisplayNameLanguage = attributes.getValue("lang")
                characters = StringBuilder()
            }

            "icon" -> {
                channelIcon = attributes.getValue("src").toUri()
            }
        }
    }

    override fun characters(
        ch: CharArray,
        start: Int,
        length: Int,
    ) {
        characters?.appendRange(ch, start, start + length)
    }

    override fun endElement(
        uri: String,
        localName: String,
        qName: String,
    ) {
        when (qName) {
            "display-name" -> {
                channelDisplayNames!! += channelDisplayNameLanguage!! to characters!!.toString()
                channelDisplayNameLanguage = null
                characters = null
            }

            "channel" -> {
                handleChannel(
                    Channel(
                        id = channelId!!,
                        displayNames = channelDisplayNames!!,
                        icon = channelIcon,
                    )
                )
                channelId = null
                channelDisplayNames = null
                channelIcon = null
            }
        }
    }

    data class Channel(
        val id: ChannelId,
        val displayNames: Map<Language, String>,
        val icon: Uri?,
    )

    data class Programme(
        val start: Instant,
        val stop: Instant,
        val channel: ChannelId,
        val title: Map<Language, String>,
        val description: Map<Language, String>,
        val category: String,
    )

}

typealias ChannelId = String

typealias Language = String

fun InputStream.parseXmlTvGzip(
    handleChannel: (XmlTvHandler.Channel) -> Unit
) {
    SAXParserFactory.newInstance().newSAXParser()
        .parse(
            /* is = */ GZIPInputStream(this),
            /* dh = */
            XmlTvHandler(
                handleChannel = handleChannel,
            ),
        )
}
