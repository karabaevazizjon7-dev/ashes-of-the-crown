# V58 audit — Save schema downgrade protection

**Status:** Fixed.

Missing or explicit-null schema markers can no longer make a complete current snapshot enter the checksum-free legacy path. Added regression cases for both forms.
