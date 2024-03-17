package couchtime

import android.app.Activity
import android.media.tv.TvInputInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import couchtime.feature.sync.SyncChannels
import couchtime.sync.SyncElectronicProgramGuide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupActivity : ComponentActivity() {

    @Inject
    @Suppress("ProtectedInFinal")
    protected lateinit var syncChannels: SyncChannels

    @Inject
    @Suppress("ProtectedInFinal")
    protected lateinit var syncElectronicProgramGuide: SyncElectronicProgramGuide

    @Inject
    @Suppress("ProtectedInFinal")
    protected lateinit var settingsStore: DataStore<Settings>

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

                SettingsField(
                    label = "Google Sheet",
                    getValue = { googleSheetAddress },
                    setValue = { setGoogleSheetAddress(it) },
                )

                SettingsField(
                    label = "EPG",
                    getValue = { epgAddress },
                    setValue = { setEpgAddress(it) },
                )

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
                        Timber.i("Sync EPG button clicked")
                        lifecycleScope.launch {
                            syncElectronicProgramGuide()
                        }
                    },
                ) {
                    Text(text = "Sync EPG")
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

    @Composable
    private fun SettingsField(
        label: String,
        getValue: Settings.() -> String,
        setValue: Settings.Builder.(value: String) -> Unit,
    ) {
        Row {

            var isEditingNow: Boolean
                    by remember {
                        mutableStateOf(false)
                    }

            val address: String?
                    by remember {
                        settingsStore.data.map { it.getValue() }
                            .stateIn(lifecycleScope, SharingStarted.Eagerly, null)
                    }.collectAsState()

            Text(text = label)

            Text(
                text = address ?: "",
            )

            Button(
                onClick = {
                    Timber.i("Edit $label button clicked")
                    isEditingNow = !isEditingNow
                },
            ) {
                Text(text = "Edit")
            }

            if (!isEditingNow) {
                return@Row
            }

            Dialog(
                onDismissRequest = {
                    isEditingNow = false
                },
            ) {

                var addressEditState: String?
                        by remember {
                            mutableStateOf(address)
                        }

                Column {

                    Text(text = "EDIT DIALOG")

                    TextField(
                        value = addressEditState ?: "",
                        onValueChange = {
                            addressEditState = it.takeIf { it.isNotBlank() }
                        },
                    )

                    Button(
                        onClick = {
                            Timber.i("Save Google Sheet address button clicked")
                            lifecycleScope.launch {
                                settingsStore.updateData {
                                    Settings.newBuilder(it)
                                        .apply {
                                            setValue(addressEditState.toString())
                                        }
                                        .build()
                                }
                                isEditingNow = false
                            }
                        },
                    ) {
                        Text(text = "Save")
                    }

                }

            }

        }
    }

}
