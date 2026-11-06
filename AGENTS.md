# Repository Guidance

## Intentional Fork Decisions

- This repository is Bedroom Launcher. Keep the Android application module named `BedroomLauncherMobile`; update references to that name rather than renaming it to upstream's `ZalithLauncher` module.
- `BedroomLauncherMobile/gradle.properties` intentionally uses `launcher_version_code=whatever it is + 10 for each push` and `launcher_version_name=indev`. The first Bedroom-specific value is `launcher_version_code=10`, and any later pushes should increase it by 10 without copying upstream Zalith Launcher 2.x release numbers into Bedroom's metadata without explicit direction.
- Keep project documentation English-only. `README.md` is the project README; do not restore `README_ZH_TW.md` or add Chinese-language README variants unless explicitly requested.
- Preserve accurate upstream attribution, license notices, and source-lineage references such as the name Zalith Launcher 2. That upstream project name is not Bedroom Launcher's version.
- The build workflow intentionally uses the `BedroomLauncherMobile` module paths and the configured Blacksmith runner. Do not revert those fork-specific workflow settings to upstream values.
- When syncing upstream changes, preserve these fork-specific decisions and other local customizations. Do not replace them wholesale with upstream files; ask before changing an intentional decision documented here.
