package couchtime.core.tvcontract.domain.model

@JvmInline
value class TvContractDisplayNumber(val value: String)

fun String.asTvContractDisplayNumber() =
    TvContractDisplayNumber(this)
