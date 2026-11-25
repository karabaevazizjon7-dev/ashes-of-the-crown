# V59 audit — Battle victory callback idempotency

**Status:** Fixed.

Added a single guard requiring a named enemy at exactly zero HP before a victory can be finalized. Delayed/double callbacks after the encounter is cleared are ignored.
