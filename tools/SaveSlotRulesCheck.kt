package com.ashes.crown

object SaveSlotRulesCheck {
    @JvmStatic
    fun main(args: Array<String>) {
        check(SaveSlotRules.includeInCampaignSnapshot("gold"))
        check(SaveSlotRules.includeInCampaignSnapshot("save_schema_version"))
        check(!SaveSlotRules.includeInCampaignSnapshot("music_enabled"))
        check(!SaveSlotRules.includeInCampaignSnapshot("slot_1_data"))
        check(!SaveSlotRules.includeInCampaignSnapshot("slot_2_checksum"))
        check(!SaveSlotRules.includeInCampaignSnapshot("slot_3_label"))

        check(SaveSlotRules.restoreIntoCampaign("gold"))
        check(SaveSlotRules.restoreIntoCampaign("save_schema_version"))
        check(!SaveSlotRules.restoreIntoCampaign("music_enabled"))
        check(!SaveSlotRules.restoreIntoCampaign("slot_1_data"))
        check(!SaveSlotRules.restoreIntoCampaign("slot_2_checksum"))
        check(!SaveSlotRules.restoreIntoCampaign("slot_3_label"))
        println("SaveSlotRulesCheck: PASS")
    }
}
