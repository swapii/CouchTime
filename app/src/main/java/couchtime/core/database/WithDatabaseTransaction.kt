package couchtime.core.database

import androidx.room.withTransaction
import javax.inject.Inject

class WithDatabaseTransaction @Inject constructor(
    private val database: AppDatabase,
) {

    suspend operator fun <T> invoke(block: suspend () -> T): T =
        database.withTransaction(block)

}
