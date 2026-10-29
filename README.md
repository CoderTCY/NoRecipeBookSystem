# No Recipe Book System
This mod removes the recipe book buttom and the recipe system of Minecraft, just like what [Not Enough Recipe Book](https://github.com/Octo-Studios/nerb) do.
This mod uses a different way to build source. Which means you need to just download one jar to ALL modloaders!

## How to compile
1. Clone this repository
2. `./gradlew universalJar`

## Branches
`latest` follows the newest supported Minecraft version, currently 26.3. `26.1` is the separate 26.1 maintenance branch; `1.21.1` retains its own older-loader integrations.

## Optional recipe viewers
This branch targets Minecraft 26.3 on Fabric and NeoForge. Viewer implementation APIs are `compileOnly`: they are not bundled or required to run this mod.

Each loader's Mixin plugin detects installed viewers before enabling their client-only compatibility mixins. Public methods are called directly; non-public RRV caches and REI menu entries use Mixin accessors. No handwritten reflection is used.

Readiness rules for supported viewers are unchanged:
- JEI: its current connection reports JEI on the server; an uninitialized connection is not ready.
- RRV: the local fallback is built and its raw recipe map is nonempty, or recipes have been synced and its server-entry map is nonempty.
- REI: installed on the client.

The compile-time API baselines are JEI 31.17.0.98, REI 26.3.824, and RRV 8.11.2+26.3. The selected NeoForge JEI and REI builds are beta; RRV is a release build. Install each viewer's actual 26.3 build and its required dependencies. EIV and EMI are not integrated on this branch; artifacts for older Minecraft versions are not used as substitutes.

Development uses Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, and NeoForge 26.3.0.10-beta. Build tooling pins NeoFormRuntime 2.0.31 so external patch and access-transformer data participate in intermediate cache keys when changing NeoForge versions.

Accessors intentionally bind to upstream internal APIs. Viewer updates that change those fields or injected methods require checking and updating the compatibility layer.

## Upstream source baselines
- [JEI 31.17.0.98 release sources](https://maven.blamejared.com/mezz/jei/jei-26.3-neoforge/31.17.0.98/jei-26.3-neoforge-31.17.0.98-sources.jar): the connection getter's uninitialized case is handled without reflection.
- [REI 26.3.824 release sources](https://maven.shedaniel.me/me/shedaniel/RoughlyEnoughItems-neoforge/26.3.824/RoughlyEnoughItems-neoforge-26.3.824-sources.jar): only the vanilla recipe-book option and the matching Display Settings toggle are removed.
- [RRV 8.11.2+26.3 release sources](https://cdn.modrinth.com/data/5VolwT6c/versions/PaMonYLb/reliable-recipe-viewer-8.11.2%2B26.3-neoforge-sources.jar): direct boolean getters and access to the raw maps preserve readiness semantics; `getRecipes()` is not substituted because it filters disabled categories.
