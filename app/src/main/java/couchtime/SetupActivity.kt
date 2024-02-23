package couchtime

import android.app.Activity
import android.media.tv.TvInputInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import couchtime.feature.sync.GetPlaylistChannels
import couchtime.feature.sync.SyncChannels
import kotlinx.coroutines.launch
import timber.log.Timber

class SetupActivity : ComponentActivity() {

    private val syncChannels: SyncChannels
            by lazy {
                SyncChannels(
                    context = applicationContext,
                    getPlaylistChannels = GetPlaylistChannels(applicationContext),
                )
            }

    init {
        Timber.d("init")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)

        val inputId: String = intent.getStringExtra(TvInputInfo.EXTRA_INPUT_ID)!!
        Timber.d("Input ID [$inputId]")

        setContent {
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier
                    .padding(32.dp)
            ) {

                Button(
                    onClick = {
                        Timber.i("Sync channels button clicked")
                        lifecycleScope.launch {
                            syncChannels(inputId)
                        }
                    },
                ) {
                    Text(text = "Sync channels")
                }

                Button(
                    onClick = {
                        Timber.i("OK button clicked")
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                ) {
                    Text(text = "OK")
                }

            }
        }

    }

}
