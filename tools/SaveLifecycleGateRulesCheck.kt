package com.ashes.crown

object SaveLifecycleGateRulesCheck {
    @JvmStatic
    fun main(args: Array<String>) {
        val gate = SaveLifecycleGate()
        check(gate.shouldSaveOnStop()) { "Normal backgrounding must persist campaign state" }
        gate.suppressNextStopSave()
        check(!gate.shouldSaveOnStop()) { "Loading a slot must suppress the stale Activity save" }
        check(gate.shouldSaveOnStop()) { "Suppression must be consumed exactly once" }
        gate.suppressNextStopSave()
        gate.suppressNextStopSave()
        check(!gate.shouldSaveOnStop()) { "Repeated slot-load signal still suppresses one stop" }
        check(gate.shouldSaveOnStop()) { "Gate must reopen after the suppressed stop" }
        println("SaveLifecycleGateRulesCheck: PASS")
    }
}
