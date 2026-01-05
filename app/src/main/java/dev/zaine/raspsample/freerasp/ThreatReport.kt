package dev.zaine.raspsample.freerasp

sealed interface ThreatReport {
    data class Found(val report: String) : ThreatReport
    object NotFound : ThreatReport
    object NotChecked : ThreatReport
    object Checking : ThreatReport
}