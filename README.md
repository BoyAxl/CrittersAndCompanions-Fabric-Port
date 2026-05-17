# Critters and Companions - Fabric 26.1.2 Port

This repository contains an unofficial Fabric port of **Critters and Companions** for:

- Minecraft `26.1.2`
- Java `25`
- Fabric Loader `0.19.2`
- Fabric API `0.149.0+26.1.2`
- GeckoLib `5.5.1`
- Forge Config API Port `26.1.4`

The original mod is a vanilla-style creature mod that adds new animals and companion interactions to the overworld.

## Attribution

This port is based on the original **Critters and Companions** project by Bonsai Studios and its contributors.

Original project links:

- CurseForge: https://www.curseforge.com/minecraft/mc-mods/critters-and-companions
- Source repository: https://github.com/bonsaistudi0s/CrittersAndCompanions

Public credits from the original project page:

- Josh / Joosh: art
- EterDelta: coding
- Scratchy: sound effects
- Lytho_: original Fabric port

The public CurseForge team also lists possible_triangle, Lytho, AlexNijjar, CodexAdrian, EterDelta, and BonsaiStudios Mascot as project members.

All original design, assets, names, mobs, textures, animations, and gameplay concepts belong to the original authors and contributors. This repository is only a compatibility port for a newer Minecraft/Fabric/Java target.

## Porting Notes

This port was produced with assistance from **OpenAI Codex** through iterative local debugging and testing. The work focused on updating the existing code and resources to run on Minecraft `26.1.2` rather than redesigning the mod.

Notable porting work includes:

- Updating Fabric, Minecraft, Java, GeckoLib, and Gradle configuration.
- Migrating entity rendering and GeckoLib resource paths.
- Updating item, block, loot table, recipe, resource-pack, and language resource formats for Minecraft `26.1.2`.
- Fixing mixins and access patterns that changed in newer Minecraft mappings.
- Restoring item models, spawn egg models, translations, and the arachnophobia-friendly built-in resource pack.
- Verifying core interactions such as taming, dragonfly armor, pearl necklace behavior, sea bunny slime collection, and relevant mob AI crashes found during testing.

## Building

Build the mod with:

```sh
sh ./gradlew build --no-daemon --console=plain
```

The Fabric jar is generated in:

```text
build/libs/
```

## Status

This is currently a local development port intended for testing before publishing to a separate GitHub repository. Treat it as experimental until more in-game coverage is completed.
