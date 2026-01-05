package dev.zaine.raspsample.freerasp

import kotlinx.coroutines.flow.StateFlow

/**
 * Created by Zin Min Tun
 * Android Developer
 * Free RASP [https://github.com/talsec/Free-RASP-Android]
 * A Summary of the interface is to provide android runtime threat detection with coroutine flow.
 */
interface RuntimeThreatReporter {

    /**
     *  register lifecycle owner*
     *  register Talsec onResume()
     *  unregister Talsec onDestory()
     *
     *  observe various Threats to know reported by Talsec
     * **/
    fun subscribeThreats(): StateFlow<ThreatReport>
}
