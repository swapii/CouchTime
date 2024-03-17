package couchtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import couchtime.feature.sync.SyncChannels
import kotlinx.coroutines.launch
import timber.log.Timber

class SetupActivity : ComponentActivity() {

    private val syncChannels: SyncChannels
            by lazy {
                SyncChannels(
                    context = applicationContext,
                )
            }

    init {
        Timber.d("init")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)

        setContent {
            Button(
                onClick = {
                    Timber.i("Sync channels button clicked")
                    lifecycleScope.launch {
                        syncChannels()
                    }
                },
            ) {
                Text(text = "Sync channels")
            }
        }

    }

}
