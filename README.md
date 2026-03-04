# All Documents Reader

Jetpack Compose Android document viewer using SAF (no all-files permission).

## Features
- Onboarding: language, intro, SAF permission screen.
- Folder selection with persisted URI permission.
- Category tabs: ALL/PDF/WORD/EBOOK/EXCEL/PPT/TXT.
- Recent + Favorites with Room.
- Sort preferences and onboarding prefs with DataStore.
- In-app viewer route for supported formats.

## Build & Run
1. Open project in Android Studio Iguana+.
2. Let Gradle sync.
3. Run `app` on Android 8.0+ device/emulator.
4. On first launch choose language, proceed, and grant folder access using picker.

## Notes
- No `MANAGE_EXTERNAL_STORAGE` permission is used.
- Access is scoped to the folder selected through SAF.
