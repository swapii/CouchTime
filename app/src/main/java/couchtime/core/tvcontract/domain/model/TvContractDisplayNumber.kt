package couchtime.core.tvcontract.domain.model

@JvmInline
value class TvContractDisplayNumber(val value: String)

fun String.toTvContractDisplayNumber() =
    TvContractDisplayNumber(this)
