package couchtime.core.googlesheet.domain.source

import couchtime.core.googlesheet.domain.model.GoogleSheetChannel

interface GoogleSheetChannelsSource {

    suspend fun getAll(): List<GoogleSheetChannel>

}
