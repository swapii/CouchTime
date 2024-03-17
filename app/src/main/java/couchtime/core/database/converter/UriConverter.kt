package couchtime.core.database.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class UriConverter {

    @TypeConverter
    fun fromString(value: String?): Uri? = value?.toUri()

    @TypeConverter
    fun toString(value: Uri?): String? = value?.toString()

}
