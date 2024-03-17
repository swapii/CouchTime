package couchtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import timber.log.Timber

class SetupActivity : ComponentActivity() {

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
                },
            ) {
                Text(text = "Sync channels")
            }
        }

    }

}
