package couchtime

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import timber.log.Timber

class SetupActivity : Activity() {

    init {
        Timber.d("init")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(
            FrameLayout(this)
                .apply {
                    addView(
                        TextView(this@SetupActivity)
                            .apply {
                                this.text = "Hello TV!"
                            }
                    )
                }
        )
    }

}
