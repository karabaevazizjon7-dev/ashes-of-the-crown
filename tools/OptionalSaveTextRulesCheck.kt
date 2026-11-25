package com.ashes.crown

object OptionalSaveTextRulesCheck {
    @JvmStatic
    fun main(args: Array<String>) {
        check(SaveDataRules.encodeOptionalText(null) == "")
        check(SaveDataRules.encodeOptionalText(" ") == " ")
        check(SaveDataRules.encodeOptionalText("Встреча завершена") == "Встреча завершена")
        check(SaveDataRules.decodeOptionalText(null) == null)
        check(SaveDataRules.decodeOptionalText("") == null)
        check(SaveDataRules.decodeOptionalText("   ") == null)
        check(SaveDataRules.decodeOptionalText("Встреча завершена") == "Встреча завершена")
        println("OptionalSaveTextRulesCheck: PASS")
    }
}
