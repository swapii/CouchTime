package couchtime.feature.channel.domain.model

@JvmInline
value class ChannelId(val value: String)

fun String.asChannelId() = ChannelId(this)
