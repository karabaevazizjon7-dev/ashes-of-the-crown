package com.ashes.crown

/** Pure key-boundary rules shared by manual-slot snapshot and restore operations. */
internal object SaveSlotRules {
    const val SLOT_PREFIX = "slot_"
    const val MUSIC_KEY = "music_enabled"

    /** Campaign snapshots exclude app-wide audio preference and the three slot containers. */
    fun includeInCampaignSnapshot(key: String): Boolean =
        key != MUSIC_KEY && !key.startsWith(SLOT_PREFIX)

    /** Applying a campaign snapshot must never overwrite audio preference or slot containers. */
    fun restoreIntoCampaign(key: String): Boolean =
        key != MUSIC_KEY && !key.startsWith(SLOT_PREFIX)
}
