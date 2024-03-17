package couchtime.core.tvcontract.domain.model

data class TvContractChannel(
    val inputId: String,
    val type: String,
    val serviceType: String,
    val displayNumber: TvContractDisplayNumber?,
    val name: String?,
)
