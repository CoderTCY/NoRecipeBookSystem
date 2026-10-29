# No Recipe Book System
This mod removes the recipe book buttom and the recipe system of Minecraft, just like what [Not Enough Recipe Book](https://github.com/Octo-Studios/nerb) do.
This mod uses a different way to build source. Which means you need to just download one jar to ALL modloaders!

## How to compile
1. Clone this repository
2. `./gradlew universalJar`

## Optional recipe viewers
This branch targets Minecraft 26.1.2 on Fabric and NeoForge. Viewer implementation APIs are `compileOnly`: they are not bundled or required to run this mod.

Each loader's Mixin plugin detects installed viewers before enabling their client-only compatibility mixins. Public methods are called directly; non-public recipe caches and REI menu entries are exposed through Mixin accessors. No handwritten reflection is used.

Readiness rules are unchanged:
- JEI: its current connection reports JEI on the server; an uninitialized connection is not ready.
- RRV: the local fallback is built and its recipe map is nonempty, or recipes have been synced and its server-entry map is nonempty.
- REI: installed on the client.
- EIV: its recipe map is nonempty.

The compile-time API baselines are JEI 29.53.0.112, REI 26.1.819, RRV 8.11.2, and EIV 8.0.0, using their real Minecraft 26.1.2 artifacts. The selected NeoForge JEI and REI builds are beta; RRV and EIV are release builds. These are not additional runtime dependencies. Install a viewer with its own required dependencies: JEI requires NeoForge 26.1.2.99 or newer and Mezz Config 0.6.3 or newer; RRV requires NeoForge 26.1.2.13-beta or newer.

Development uses Fabric Loader 0.19.5, Fabric API 0.155.0+26.1.2, and NeoForge 26.1.2.99. Development versions are separate from the mod's declared runtime minima, which remain Fabric Loader 0.18.6 and NeoForge 26.1.2.7-beta. The existing declared Minecraft range `[26.1, 26.3)` is unchanged; verification targets 26.1.2.

Build tooling pins NeoFormRuntime 2.0.31 so patch and access-transformer data participate in intermediate cache keys. This avoids reusing Minecraft artifacts patched for an older NeoForge version when upgrading the development loader.

Accessors intentionally bind to those internal APIs. A viewer update that changes the accessed fields or injected methods requires checking and updating the compatibility layer.

## Upstream source baselines
- [JEI 29.53.0.112 release sources](https://maven.blamejared.com/mezz/jei/jei-26.1.2-neoforge/29.53.0.112/jei-26.1.2-neoforge-29.53.0.112-sources.jar): an uninitialized server connection throws `IllegalStateException`; only this initialization case is treated as not ready.
- [REI 26.1.819 release sources](https://maven.shedaniel.me/me/shedaniel/RoughlyEnoughItems-neoforge/26.1.819/RoughlyEnoughItems-neoforge-26.1.819-sources.jar): config option IDs and menu translation keys identify the removed entries; immutable submenu children are copied before replacement.
- [RRV 8.11.2+26.1.2 release sources](https://cdn.modrinth.com/data/5VolwT6c/versions/FZXHm7mi/reliable-recipe-viewer-8.11.2%2B26.1.2-neoforge-sources.jar): readiness uses the raw recipe maps, not `getRecipes()`, whose category filtering would change the result.
- [EIV source commit `22f7416`](https://github.com/liushmn/ExtendedItemView/tree/22f741634aefb4d353e88b1aaddff91a6b3494d8) was checked against the actual 8.0.0+26.1.2 release bytecode. No matching tagged release source archive was available; the pinned Git commit is not presented as an official release tag.
