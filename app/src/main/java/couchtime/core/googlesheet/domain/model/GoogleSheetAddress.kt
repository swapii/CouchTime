package couchtime.core.googlesheet.domain.model

import android.net.Uri
import androidx.core.net.toUri

@JvmInline
value class GoogleSheetAddress(val value: Uri)

fun String.asGoogleSheetAddress() = GoogleSheetAddress(this.toUri())
