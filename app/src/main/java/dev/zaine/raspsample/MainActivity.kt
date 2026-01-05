package dev.zaine.raspsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import dev.zaine.raspsample.freerasp.CertificateGenerator
import dev.zaine.raspsample.freerasp.RuntimeThreatReporterImpl
import dev.zaine.raspsample.freerasp.ThreatDetectedActivity
import dev.zaine.raspsample.freerasp.ThreatReport
import dev.zaine.raspsample.ui.theme.RASPSampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //call this in onCreate()
    private val runtimeThreatReporter by lazy {
        RuntimeThreatReporterImpl(
            context = this,
            isProduction = true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Generate sign hash key
        //Log.i("Certificate", CertificateGenerator.computeSigningCertificateHash(this))
        enableEdgeToEdge()
        setContent {
            RASPSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            runtimeThreatReporter.subscribeThreats().collect {
                if (it is ThreatReport.Found) {
                    Log.i("Report", it.report)
                    finishAffinity()
                    startActivity(ThreatDetectedActivity.newIntent(this@MainActivity, it.report))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RASPSampleTheme {
        Greeting("Android")
    }
}