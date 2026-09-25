<div align="center">

![Bee Mastery](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/banner.png)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1_|_1.21.1-62B47A?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.3.5+-FF6B35?style=for-the-badge)](https://files.minecraftforge.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.251+-E68A00?style=for-the-badge)](https://neoforged.net/)
[![Forestry CE](https://img.shields.io/badge/Forestry_CE-2.5.0+_|_3.0.0--alpha6+-FFD700?style=for-the-badge)](https://github.com/thedarkcolour/ForestryCE)
[![Version](https://img.shields.io/badge/Version-1.2.1-brightgreen?style=for-the-badge)](https://github.com/eliasnvx/BeeMastery/releases)

[![Modrinth](https://img.shields.io/badge/Modrinth-Download-1BD96A?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/bee-mastery)
[![CurseForge](https://img.shields.io/badge/CurseForge-Download-F16436?style=for-the-badge&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/bee-mastery)

**A beekeeping expansion for Forestry: Community Edition — 11 new bees, living auras,<br>legendary tools and frames, and a hive you wear on your back.**

[Portable Hive](#-portable-hive) • [Modules](#-upgrades--modules) • [Bees](#-bees--auras) • [Tools](#-tools--frames) • [Installation](#-installation) • [Development](#%EF%B8%8F-development)

</div>

---

## 📖 About

**Bee Mastery** is the spiritual successor to the beloved **ForestryExtras** by wasliebob, rebuilt from scratch for modern Minecraft. It layers a whole second breeding tree on top of Forestry: an engineered tandem line of bees, five living auras, top-tier tools and frames — and the **Portable Hive**, a bee house you carry on your back. An in-game guide book in 10 languages explains everything.

---

![Portable Hive](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/header_portable_hive.png)

## 🎒 Portable Hive

A Bee House you wear in the chestplate slot. Put in a queen (or a princess and a drone) and the bees keep working **wherever you walk** — biome, weather, light and flowers come from where you're standing. It runs Forestry's own beekeeping logic, so breeding, ageing and production behave exactly as in a normal hive.

- 🌼 **Flower slot** — carry a flower your bees like and they stop needing flowers around you
- 🎒 **3D hive on your back** with bees buzzing out of it — other players see them too
- 🛡️ **Built-in armour** — craft it with any chestplate; if the chestplate breaks, the hive stays
- 📊 **HUD panel** with the queen, her remaining life and the hive status; the GUI says exactly why bees stopped
- 🐝 **Your own hive never stings you** — harmful bee effects don't hit their owner
- ⌨️ Press **B** to open the hive on your back (rebindable)

![Portable Hive tiers](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/tiers.png)

---

![Upgrades & Modules](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/header_modules.png)

## 🧩 Upgrades & Modules

Upgrade the hive with the mod's metals — **Reinforced → Draconic → Legendary**. Each tier adds speed and a module slot; everything inside the hive is kept.

| Tier | Speed | Module slots | Upgrade |
|---|---|---|---|
| Basic | 25% (a Bee House's pace) | 1 | — |
| Reinforced | 35% | 2 | Reinforced ingots + propolis |
| Draconic | 50% | 3 | Draconic ingots + propolis |
| Legendary | 75% | 4 | Legendary ingots + propolis |

![Hive modules](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/modules.png)

Modules switch on Forestry's own housing mechanics. One of each kind per hive; the Mutagen and the Gene Stabilizer don't mix. All numbers are configurable (see [Config](#-config)).

---

![Bees & Auras](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/header_bees.png)

## 🐝 Bees & Auras

| Bee | Tier | Comb centrifuges into |
|---|---|---|
| 🥔 **Potato** | Agricultural | Potato 15%, Poisonous Potato 15% |
| 🥕 **Carrot** | Agricultural · Harvest aura | Carrot 15%, Golden Carrot 2% |
| 🐷 **Pig** | Livestock | Porkchop 15% |
| 🐄 **Cow** | Livestock | Beef 15%, Leather 10% |
| 🐑 **Sheep** | Livestock | String 15%, Wool 10% |
| 🏺 **Clayious** | Resource | Clay Ball 15%, Honey Drop 75% |
| 🛡️ **Reinforced** | Advanced | Reinforced Nugget 25%, Propolis 10%, Honey Drop 75% |
| ⚗️ **Mutated** | Advanced · Mutagenic aura | Mutated Iron Nugget 15%, Propolis 10%, Honey Drop 75% |
| 💀 **Witheria** | Elite · Withering aura | Witheria Nugget 15%, Propolis 10%, Honey Drop 75% |
| 🐉 **Draconic** | Elite · Draconic aura | Draconic Nugget 10%, Propolis 10%, Honey Drop 75% |
| 👑 **Legendary** | Elite · Legendary aura | Legendary Nugget 5%, Propolis 10%, Honey Drop 75% |

<details>
<summary><b>🧬 Mutations</b></summary>

```
Cultivated  + Meadows     → Potato      15%
Cultivated  + Forest      → Carrot      15%
Steadfast   + Carrot      → Pig         30%
Steadfast   + Potato      → Cow         30%
Carrot      + Potato      → Sheep       30%
Meadows     + Noble       → Clayious    15%
Valiant     + Noble       → Reinforced   5%
Majestic    + Reinforced  → Mutated      5%
Industrious + Mutated     → Witheria     5%
Witheria    + Reinforced  → Draconic     5%
Witheria    + Draconic    → Legendary    5%
```

</details>

**Five living auras** pulse from the hives of their bees while they work:

- 🔥 **Draconic** — damages nearby hostiles and grants Strength
- 💙 **Legendary** — Regeneration and Absorption for nearby players
- 💀 **Withering** — withers nearby hostiles
- 🧪 **Mutagenic** — a random buff every pulse
- 🌾 **Harvest** — fertilizes nearby crops like bone meal

Bottle an aura into an **Aura Charm** to carry it, socket up to two charms into an **Aura Belt**, and cluster hives of the same aura — they amplify each other.

---

![Tools & Frames](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/header_tools.png)

## 🔧 Tools & Frames

- 🥅 **Nets (Scoops)** — catch vanilla bees as Forestry drones; Reinforced, Draconic and Legendary tiers, 300 to 10,000 durability
- 🔪 **Grafters** — 3× to 10× sapling drops, forged from their tier's ingot
- 🖼️ **19 Bee Frames** — from Coal up to a tandem chain where each top frame is built from the ones below it, ending in the Legendary Frame (5× production) and the Frame of Mutation (10× mutation chance)
- 💎 **Five material lines** — Draconic, Legendary, Reinforced, Witheria and Mutated Iron: combs, nuggets, ingots, storage blocks and propolis

![All items](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/items.png)

---

![Guide Book](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/header_guide.png)

## 📖 Guide Book, JEI & Config

- **Guide book** (Patchouli; craft a Book + Reinforced Propolis) — breeding order, auras, frames, tools and the Portable Hive, in **English, Russian, Ukrainian, Belarusian, German, Dutch, Polish and Chinese** (Simplified, Taiwan, Hong Kong)
- **JEI** — bee breeding, frame and hive recipes, and a page for every aura

### ⚙ Config

`serverconfig/beemastery-server.toml` (synced to clients; ship defaults in `defaultconfigs/`): hive speed per tier, module strengths, collector interval, the lantern's light on/off and level, own-hive protection. Client options: show/hide the hive panel and pick its corner, bees around worn hives. On 1.21.1 there's also a **Config** button in the mod list.

---

![Requirements](https://raw.githubusercontent.com/eliasnvx/BeeMastery/1.21.1-dev/docs/images/header_requirements.png)

## 📦 Installation

| | Minecraft 1.20.1 | Minecraft 1.21.1 |
|---|---|---|
| Loader | [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html) 47.3.5+ | [NeoForge](https://neoforged.net/) 21.1.251+ |
| [Forestry: Community Edition](https://www.curseforge.com/minecraft/mc-mods/forestry-community-edition) | 2.5.0+ | 3.0.0-alpha6+ |
| [Patchouli](https://www.curseforge.com/minecraft/mc-mods/patchouli) | required | required (by Forestry) |
| [JEI](https://www.curseforge.com/minecraft/mc-mods/jei) | optional | optional |

1. Install the loader, Forestry CE and Patchouli for your Minecraft version.
2. Download Bee Mastery for the same version from [Releases](https://github.com/eliasnvx/BeeMastery/releases), Modrinth or CurseForge.
3. Drop the `.jar` into your `mods` folder and launch.

> The 1.21.1 version is released as a **beta** while Forestry itself is in alpha on 1.21.1.

---

## 🛠️ Development

| Branch | Minecraft | Loader | Java |
|---|---|---|---|
| [`1.20.1-dev`](https://github.com/eliasnvx/BeeMastery/tree/1.20.1-dev) | 1.20.1 | Forge (ForgeGradle) | 17 |
| [`1.21.1-dev`](https://github.com/eliasnvx/BeeMastery/tree/1.21.1-dev) | 1.21.1 | NeoForge (ModDevGradle) | 21 |

```bash
git clone -b 1.21.1-dev https://github.com/eliasnvx/BeeMastery.git
cd BeeMastery
./gradlew build              # jar in build/libs/
./gradlew runGameTestServer  # game tests: datapack, portable hive, recipes
./gradlew runClient          # dev client
```

Releases are built by GitHub Actions from tags `vX.Y.Z+<minecraft version>` (e.g. `v1.2.1+1.21.1`); the version, loader and requirements are read from `gradle.properties`, and the release notes from `CHANGELOG.md`.

---

## 🙏 Credits

- **Original mod:** [wasliebob](https://github.com/wasliebob) — ForestryExtras for Forestry 4
- **Rebuild & maintenance:** [Elias](https://github.com/eliasnvx)
- **Forestry CE:** [thedarkcolour](https://github.com/thedarkcolour) and contributors
- **Original Forestry:** SirSengir and the Forestry team

## 📜 License

GNU Lesser General Public License v3.0 (LGPL-3.0).

<div align="center">

**Made with 🐝 for the Minecraft modding community**

</div>
