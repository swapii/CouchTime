package couchtime.core.googlesheet.domain.model

import android.net.Uri

data class GoogleSheetChannel(
    val displayNumber: GoogleSheetChannelDisplayNumber,
    val name: String,
    val address: Uri,
)
