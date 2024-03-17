package couchtime

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView

class SetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
