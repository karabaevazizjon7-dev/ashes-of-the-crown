package com.ashes.crown

/**
 * Prevents the outgoing Activity from writing stale in-memory state after a manual
 * save slot has already been applied to SharedPreferences and Activity recreation starts.
 */
internal class SaveLifecycleGate {
    private var skipNextStopSave = false

    fun suppressNextStopSave() {
        skipNextStopSave = true
    }

    /** Returns false exactly once after suppression; subsequent stops save normally. */
    fun shouldSaveOnStop(): Boolean {
        if (!skipNextStopSave) return true
        skipNextStopSave = false
        return false
    }
}
