### This project is to provide easy integration of Talsec RASP in android.

# RuntimeThreatReporter

`RuntimeThreatReporter` is an interface that provides Android runtime threat detection using coroutine flow. It is based on the Free RASP library.

## Implemented by

- `RuntimeThreatReporterImpl`

## Functions

### `subscribeThreats(): StateFlow<ThreatReport>`

This function registers the lifecycle owner, registers Talsec onResume(), unregisters Talsec onDestroy(), and observes various threats reported by Talsec.

## Configuration

The `RuntimeThreatReporterImpl` class has a companion object that holds the configuration for Talsec.

### `watcherMail`

This is a private constant that holds the watcher mail according to the Talsec requirement for alerts and reports. Developer must replace with your mail.

### `expectedProdPackageName`

This is a private value that holds your production built package name. **Do not use `Context.getPackageName()`!**. Developer must add your package name.

### `expectedSigningCertificateHashBase64`

This is a private value that holds the signing certificate hash base64. You can generate this using the `CertificateGenerator`. It can also hold the PlayStore Signing Certificate Hash.
For debugging mode,use CertificateGenerator to test it. For production, PlayStore Signing Certificate Hash.

## Usage

To use the `RuntimeThreatReporter`, you need to create an instance of `RuntimeThreatReporterImpl` in your `MainActivity`. You can then use `lifecycleScope` to collect the threats.

```kotlin
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
        //...
        lifecycleScope.launch(Dispatchers.Main) {
            runtimeThreatReporter.subscribeThreats().collect { report ->
                if (report is ThreatReport.Found) {
                    Log.i("Report", report.report)
                    finishAffinity()
                    startActivity(ThreatDetectedActivity.newIntent(this@MainActivity, report.report))
                }
            }
        }
    }
}
```

## AndroidManifest.xml

You also need to declare the `ThreatDetectedActivity` in your `AndroidManifest.xml` file.

```xml
<activity
    android:name="dev.zaine.raspsample.freerasp.ThreatDetectedActivity" 
/>
```
