package dev.zaine.raspsample.freerasp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.zaine.raspsample.R
import kotlin.system.exitProcess

class ThreatDetectedActivity : ComponentActivity() {

    companion object {
        private const val THREAT_MESSAGE = "extra-threat-message"

        fun newIntent(context: Context, threatMessage: String): Intent {
            val intent = Intent(context, ThreatDetectedActivity::class.java)
            intent.putExtra(THREAT_MESSAGE, threatMessage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mappedMessage = intent.getStringExtra(THREAT_MESSAGE).orEmpty()

        setContent {
            ThreatDetectedDialog(mappedMessage)
        }
    }

    @Composable
    private fun ThreatDetectedDialog(mappedMessage: String) {
        AlertDialog(
            onDismissRequest = { exitProcess(0) },
            title = { Text(text = stringResource(id = R.string.title_threat_detected)) },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.message_threat_detected,
                        mappedMessage
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        exitProcess(0)
                    }
                ) {
                    Text(text = stringResource(id = R.string.lbl_close))
                }
            }
        )
    }
}