package couchtime.feature.channel.domain.model

@JvmInline
value class ChannelDisplayNumber(val value: String)

fun String.toChannelDisplayNumber() = ChannelDisplayNumber(this)
