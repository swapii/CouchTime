package couchtime

import android.app.Application
import timber.log.Timber

class CouchTimeApplication : Application() {

    init {

        Timber.plant(
            Timber.DebugTree()
        )

        Timber.d("init")

    }

}
