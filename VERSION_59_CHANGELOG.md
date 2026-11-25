# Working version 59 — Battle victory callback idempotency

Added a single guard requiring a named enemy at exactly zero HP before a victory can be finalized. Delayed/double callbacks after the encounter is cleared are ignored.
