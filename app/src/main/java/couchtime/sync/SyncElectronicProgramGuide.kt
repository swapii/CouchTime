package couchtime.sync

import timber.log.Timber
import javax.inject.Inject

class SyncElectronicProgramGuide @Inject constructor(
) {

    suspend operator fun invoke() {
        Timber.i("SyncElectronicProgramGuide")
    }

}
