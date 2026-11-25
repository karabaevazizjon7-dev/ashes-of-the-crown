# V64 audit — World-day settlement marker

**Status:** Fixed.

Legacy snapshots without the upkeep marker initialize it to their saved world day. Current-schema snapshots with mismatched world/upkeep days are rejected instead of replaying a huge or inconsistent daily backlog.
