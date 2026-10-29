# No Recipe Book System
This mod removes the recipe book buttom and the recipe system of Minecraft, just like what [Not Enough Recipe Book](https://github.com/Octo-Studios/nerb) do.
This mod uses a different way to build source. Which means you need to just download one jar to ALL modloaders!

## How to compile
1. Clone this repository
2. `./gradlew universalJar`

## Optional recipe viewers
EMI and REI integration uses compile-only implementation APIs and optional client Mixins, not handwritten reflection. The viewer APIs are not bundled in the universal jar.

Each loader detects installed mods before enabling compatibility Mixins. Loader-specific compatibility configurations have separate names so Fabric's relocated, intermediary-mapped configuration cannot be overwritten by the Forge or NeoForge configuration.

Existing behavior is preserved:
- When EMI is installed, it remains responsible for the recipe book button.
- Loading `ui.recipe-book-action: default` does not overwrite EMI's current action; the default choice is also excluded from its config UI. Other values and settings are unchanged.
- REI's recipe book removal getter remains false, while the vanilla recipe book option is removed from its main configuration screen and Display Settings menu.

The compile-time API baselines are EMI 1.1.24+1.21.1 and REI 16.0.799. EMI publishes release builds for Fabric and NeoForge; REI marks its Fabric build as release and NeoForge build as beta. Fabric uses their Fabric artifacts; the common, Forge, and NeoForge sources compile against their official-mapped NeoForge artifacts. These are compile-only dependencies, not a requirement to install either viewer.

There are no matching official Forge 1.21.1 releases of these selected viewers. Using their NeoForge APIs for Forge compilation does not claim compatibility with a nonexistent Forge release. For actual REI installations, also install its required Cloth Config and Architectury versions.

Non-public REI menu entries are accessed through a Mixin accessor. Changes to upstream internal fields or injected methods require checking and updating the compatibility layer.

## Upstream source baselines
- [EMI 1.1.24+1.21.1 release sources](https://maven.terraformersmc.com/releases/dev/emi/emi-neoforge/1.1.24+1.21.1/emi-neoforge-1.1.24+1.21.1-sources.jar): `EmiConfig.assignField`, `ConfigScreen`, and `EnumWidget` define the config assignment and UI-filter contracts.
- [REI 16.0.799 release sources](https://maven.shedaniel.me/me/shedaniel/RoughlyEnoughItems-neoforge/16.0.799/RoughlyEnoughItems-neoforge-16.0.799-sources.jar): the config constructor copies its option tree; the display submenu uses immutable child lists, so filtering copies and replaces the list instead of modifying it in place.
