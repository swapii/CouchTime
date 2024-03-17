package couchtime.core.tvcontract.domain.model

data class TvContractChannel(
    val id: TvContractChannelId? = null,
    val inputId: String,
    val type: String,
    val serviceType: String,
    val displayNumber: TvContractDisplayNumber?,
    val displayName: String?,
    val internalProviderId: String?,
)
