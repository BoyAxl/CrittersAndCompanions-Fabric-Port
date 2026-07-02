# Changelog

## v26.1.2-0.2.1-fabric - Unreleased

### Added

- Added `crittersandcompanions-spawns.toml`, matching the official configurable spawn file with biome/tag, weight, min, and max entries per entity.

### Changed

- Natural spawn registration now reads from the dedicated spawn config instead of hardcoded Fabric entries.
- Updated default spawn entries to match the official 1.21.1 per-biome values where the old Fabric port used flatter defaults.
- Migrates customized legacy `spawn_rates` values from `crittersandcompanions-common.toml` when generating the new spawn config for the first time.
- Increased ferret walk speed to match the official 1.21.1 behavior.

### Fixed

- Added the missing Shima Enaga spawn placement registration.
- Fixed Koi Fish and baby Roly-Poly texture z-fighting on flat model planes.
- Fixed Shima Enaga wing texture z-fighting on GeckoLib 5.
- Renamed the legacy necklace config typo from `swim_sped` to `swim_speed`, preserving customized existing values.
- Aligned subtitle keys with the official `entity.*` translation format so sound subtitles resolve correctly.

## v26.1.2-0.2.0-fabric - 2026-07-02

### Added

- Ported the new Critters and Companions 1.21.1 content into the Fabric 26.1.x port.
- Added ladybugs, roly-polies, snails, stag beetles, stick bugs, weevils, mud balls, acorns, the acorn hat, snail slime, and roly-poly chest support.
- Added natural spawns for the new insects in forest and lush biomes, matching the official 1.21.1 defaults.
- Added updated jumping spider variants.
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
