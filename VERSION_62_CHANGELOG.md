# Working version 62 — Daily settlement treasury arithmetic

RealmDayProcessor now uses Long for the interim treasury calculation and clamps the final net balance after steward wages, avoiding Int overflow and preserving correct net income at the gold cap.
