package couchtime

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CouchTimeApplication : Application() {

    init {

        Timber.plant(
            Timber.DebugTree()
        )

        Timber.d("init")

    }

}
