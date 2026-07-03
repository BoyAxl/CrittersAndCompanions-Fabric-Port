# Critters and Companions - Fabric 26.2.x Port

<p align="center">
  <img src="docs/assets/title.png" alt="Critters and Companions official title artwork" width="540">
</p>

This repository contains an unofficial Fabric port of **Critters and Companions** for the Minecraft `26.2.x` line:

- Minecraft `26.2.x`
- Java `25`
- Fabric Loader `0.19.3`
- Fabric API `0.154.0+26.2`
- GeckoLib `5.5.3+`
- Forge Config API Port `26.2.1`

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
- L2: Modded Omelet resource pack spawn egg textures adapted for this port

The public CurseForge team also lists possible_triangle, Lytho, AlexNijjar, CodexAdrian, EterDelta, and BonsaiStudios Mascot as project members.

All original design, assets, names, mobs, textures, animations, and gameplay concepts belong to the original authors and contributors. This repository is only a compatibility port for a newer Minecraft/Fabric/Java target.

## Porting Notes

This port was produced with assistance from **OpenAI Codex** through iterative local debugging and testing. The work focused on updating the existing code and resources to run on Minecraft `26.2.x` rather than redesigning the mod.

Notable porting work includes:

- Updating Fabric, Minecraft, Java, GeckoLib, and Gradle configuration.
- Migrating entity rendering and GeckoLib resource paths.
- Updating item, block, loot table, recipe, resource-pack, and language resource formats for Minecraft `26.2.x`.
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

The first public release of this unofficial port is available on GitHub:

- `v26.2.0-0.1.0-fabric`
- https://github.com/BoyAxl/CrittersAndCompanions-Fabric-Port/releases

The port has been tested in-game for startup, world creation, item rendering, selected recipes, selected entity rendering, and several core interactions. It should still be treated as experimental until broader gameplay coverage is completed.
