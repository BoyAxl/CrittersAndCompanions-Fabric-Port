# Changelog

## v26.1.2-0.2.0-fabric - 2026-07-02

### Added

- Ported the new Critters and Companions 1.21.1 content into the Fabric 26.1.x port.
- Added ladybugs, roly-polies, snails, stag beetles, stick bugs, weevils, mud balls, acorns, the acorn hat, snail slime, and roly-poly chest support.
- Added natural spawns for the new insects in forest and lush biomes, matching the official 1.21.1 defaults.
- Added updated jumping spider variants and jukebox dancing behavior.
- Added updated item models, spawn eggs, textures, animations, sounds, tags, recipes, loot hooks, and resource-pack assets for the new content.

### Fixed

- Fixed new bug entities failing to spawn naturally because their food and tempt tags were resolved before construction finished.
- Fixed duplicate taming and repeated heart feedback for non-breedable tamed bugs.
- Fixed spawn egg model and texture issues for the new mobs.
- Fixed ladybug spawn animation timing and roly-poly leg animation rendering.
- Fixed snail wake-up timing and climbing render posture.
- Fixed acorn hat armor rendering.
- Fixed the built-in arachnophobia resource pack for jumping spider variants.
- Fixed the mod version format so Fabric Loader no longer warns about non-SemVer `26.1.x`.

### Changed

- Migrated existing default spawn config values to the official updated defaults while preserving user-customized values.
- Kept the port Fabric-only and aligned with the existing Fabric 26.1.x code style and GeckoLib 5 patterns.

### Tested

- Built successfully with `./gradlew build`.
- Verified startup, world load, natural insect spawning, creative spawn eggs, and latest logs in the TESTFABRIC instance.
