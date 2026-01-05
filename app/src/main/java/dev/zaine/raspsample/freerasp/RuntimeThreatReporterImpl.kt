package dev.zaine.raspsample.freerasp

import android.content.Context
import android.util.Log
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.ThreatListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RuntimeThreatReporterImpl(
    val context: Context,
    val isProduction: Boolean
) : RuntimeThreatReporter {

    companion object {
        private const val tag_threat_log = "main-threat-log"

        /**
         * Add watcher mail according to the Talsec requirement
         * For Alerts and Reports
         */
        private const val watcherMail = "your-email@example.com"

        /**
         * Just place your production built package name in expectedPackageName
         *  Don\'t use Context.getPackageName!
         */
        private val expectedProdPackageName = "dev.zaine.raspsample"


        /**
         * Generate SigningCertificateHashBase64 using CertificateGenerator
         *
         */
        private val expectedSigningCertificateHashBase64 =
            arrayOf(
                "vmLKgSsWh2f0CSm2+jnbnzoGY+OB0u6xbKAMDABvJuk=", //for debugging
                "vmLKgSsWh2f0CSm2+jnbnzoGY+OB0u6xbKAMDABvJuk=" //for playstore, you must replace this.
            )

        /**
         * Add to support alternative stores like samsung,huawei,etc
         * If only play store is allowed,put null,
         * Otherwise, see in Free RASP doc.
         */
        private val supportedAlternativeStores = arrayOf(
            "com.sec.android.app.samsungapps", // Samsung Store
            "com.android.packageinstaller", // Package Installer
            "com.google.android.packageinstaller" // Package Installer
        )
    }

    private val threatListener by lazy { ThreatListener(threatListenerCallback) }

    private lateinit var threatListenerCallback: ThreatListener.ThreatDetected

    private val config by lazy { getTalsecConfig() }

    private fun getTalsecConfig(): TalsecConfig {
        return TalsecConfig.Builder(
            expectedProdPackageName,
            expectedSigningCertificateHashBase64
        ).watcherMail(watcherMail)
            .supportedAlternativeStores(supportedAlternativeStores)
            .prod(isProduction)
            .build()
    }


    private val _mutableStateFlow = MutableStateFlow<ThreatReport>(ThreatReport.NotChecked)
    private val stateFlow = _mutableStateFlow.asStateFlow()

    init {
        if (isProduction) {
            setupThreatDetectedListener()
            startThreatManager()
        }
    }

    private fun startThreatManager() {
        try {
            // start Talsec
            Log.i(tag_threat_log, "Starting threat manager")
            Talsec.start(context, config)
            // add listener
            Log.i(tag_threat_log, "Threat registerListener")
            threatListener.registerListener(context)
        } catch (e: Exception) {

        }
    }

    private fun setupThreatDetectedListener() {
        _mutableStateFlow.value = ThreatReport.Checking
        threatListenerCallback = object : ThreatListener.ThreatDetected {
            override fun onRootDetected() {
                foundAThreat("Rooted Device")
            }

            override fun onEmulatorDetected() {
                foundAThreat("Emulator")
            }

            override fun onHookDetected() {
                foundAThreat("Hooking")
            }

            override fun onDeviceBindingDetected() {
                foundAThreat("Device Binding")
            }

            override fun onObfuscationIssuesDetected() {
                foundAThreat("Obfuscation Issues")
            }

            override fun onTamperDetected() {
                foundAThreat("CodeTamper Detected")
            }

            override fun onDebuggerDetected() {
                foundAThreat("Debugger Detected")
            }

            override fun onMalwareDetected(p0: List<SuspiciousAppInfo?>?) {
            }

            override fun onScreenshotDetected() {
            }

            override fun onScreenRecordingDetected() {
            }

            override fun onMultiInstanceDetected() {
            }

            override fun onUntrustedInstallationSourceDetected() {
            }
        }
    }

    private fun foundAThreat(threat: String) {
        Log.i(tag_threat_log, threat)
        _mutableStateFlow.value = ThreatReport.Found(report = threat)
        threatListener.unregisterListener(context)
    }

    override fun subscribeThreats() = stateFlow
}
