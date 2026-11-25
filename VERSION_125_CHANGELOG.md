# V125 — Cross-System Campaign Loop

Base: V124. Earlier version archives remain separate and unchanged.

## Changes
- Added `tools/CampaignLoopIntegrationRulesCheck.kt`, a deterministic cross-system scenario connecting narrative choice resolution, inventory rewards, medicine quest progression, companion-to-companion affinity, world travel, combat victory/rewards, and schema-11 snapshot validation.
- Added `tools/V125_CAMPAIGN_LOOP_AUDIT.py` to guard the intended integration coverage.
- No production gameplay source, save schema, balance values, story content, Android UI, or packaging configuration changed. This version adds integration-level regression coverage only.

## Verification
- Domain Kotlin compile and all test mains: recorded in the V125 audit report.
- V125 static integration audit: recorded in the V125 audit report.
- Full Android/Compose Gradle build and Android Lint are not claimed unless separately executed in an Android toolchain.
- APK not produced, per Part III release policy.
