# Changelog

All notable changes to Bee Mastery will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.1+1.21.1] - 2026-09-25

### Fixed
- **Portable Hive duplicated drones:** with two or more drones in the drone slot, a princess mating in the hive didn't use one up. Mating now takes exactly one drone.

## [1.2.0+1.21.1] - 2026-09-25

First release for **Minecraft 1.21.1 / NeoForge**: everything from 1.1.0 (the aura system, Aura Belt, hive synergy, frame tandem, guide book and all 11 bees) plus the new **Portable Hive**.

### Added
- **Portable Hive:** a bee house you wear on your back in the chestplate slot. Put in a queen (or princess + drone) and a frame, and it keeps producing as you walk around — biome, weather and light come from where you are. Production starts at a Bee House's pace and grows as you upgrade the hive.
  - **Flower slot:** carry a flower your bees like (a poppy for Forest bees, nether wart for Nether bees…) and they no longer need flowers around you. The empty slot shows which flower the current bee wants. Doesn't apply to bees that want daylight instead of a plant.
  - Press **B** (rebindable) to open the worn hive, or right-click it in your hand. Shift+right-click to wear it — the action bar reminds you of the key when you put it on.
  - The GUI explains itself: faded example items in the empty slots, a hint on hovering any slot, a status icon above the arrow telling you why the bees aren't working (too hot, no flowers, not worn…), a progress bar (queen's life, or mating progress for a princess), an arrow that animates while the bees work, and a **?** button with the full how-to.
  - On your back it's a small 3D beehive — bottom board with a landing board (and a drop of honey), brood box with the entrance, honey super with handholds, and an overhanging roof.
  - Bees buzz out of the hive on your back while it's working — Forestry's own hive particles, visible to other players too.
  - While worn, a small HUD panel in the top-left corner shows the queen, the progress bar and the hive status, so you can see what your bees are doing without opening it.
  - Craft it together with any chestplate to get that chestplate's armor while wearing it; craft the armored hive alone to take the chestplate back. If the chestplate breaks, the hive stays. An armoured hive shows its chestplate as a small icon in the corner of its slot.
  - Recipe: Bee House, leather, string and an iron nugget.
  - New **Portable Hive** chapter in the guide book: wearing and opening it, the slots and status, built-in armour, upgrades and all nine modules with their recipes.
- **Portable Hive upgrades & modules** (Thermal-style augments):
  - Upgrade the hive in a crafting grid with Reinforced → Draconic → Legendary ingots and propolis. Each tier adds a module slot (1 → 4) and speed (25% → 35% → 50% → 75%, the basic tier matching Forestry's Bee House). Everything inside the hive — bees, frame, flower, modules, built-in armour — is kept.
  - Each tier looks the part: its own icon with metal fittings (steel, red-black, blue with gold rivets — the Legendary one shimmers), metal corners in the tier's colours on the hive on your back, and metal corner brackets plus the tier icon in the hive window.
  - 9 modules for the side panel of the hive GUI, each switching on Forestry's own housing mechanics: **Lantern** (a lantern on the hive that lights up the area around you; bees work day and night), **Canopy** (work in rain), **Artificial Sun** (work underground), **Climate Stabilizer** (ignore biome climate), **Booster** (+25% production), **Longevity** (queen lives twice as long), **Mutagen** (2× mutation chance), **Gene Stabilizer** (no mutations — can't be combined with Mutagen) and **Collector** (products go straight into your inventory). One of each kind per hive.
- **Your own hive never hurts you:** Forestry bee effects (aggressive, radioactive, ignition, creeper, phasing, harmful potions) no longer hit the player wearing the hive. Other hives still do.
- **Config** (`serverconfig/beemastery-server.toml`, synced to clients, and a Config button in the mod list): speed per hive tier, module strengths, collector interval, the lantern's light on/off and level, own-hive protection. Client options: show/hide the hive panel and pick its corner, bees around worn hives on/off. Module tooltips show the configured numbers.
- **The guide book is translated** into all the mod's languages: Russian, Ukrainian, Belarusian, German, Dutch, Polish and Chinese (Simplified, Taiwan, Hong Kong). It opens in your game language, with the in-game item names.


### Changed
- **Potato and Carrot bees mutate from Cultivated now:** Potato from Cultivated + Meadows, Carrot from Cultivated + Forest (was Forest + Meadows for both, clashing with each other).


### Fixed
- Guide book: the Aura Belt page said a right-click with both hands empty shows the socketed charms — it's a right-click with the belt in one hand and the other hand empty.

### Port notes
- **Requires Forestry: Community Edition 3.0.0-alpha6+** (the only Forestry build for 1.21.1 so far) and **NeoForge 21.1.251+** — older NeoForge 21.1 builds crash on world creation because of Forestry.
- Bee mutations now use Forestry 3.x's data-driven mutation recipes, so they show up and breed exactly as on 1.20.1.
- **Metal frames (Tin, Silver, Lead, Bronze, Steel, Invar, Electrum)** are craftable whenever any mod in your pack provides the ingot (e.g. Mekanism). On 1.20.1 they required Thermal, which doesn't exist for 1.21.1.
- The in-game guide book is crafted with a regular recipe now (Book + Reinforced Propolis), since Patchouli dropped its special book recipe type.
- Released as a **beta** because Forestry itself is still in alpha on 1.21.1.

## [1.1.0] - 2026-07-05

### Added
- **Aura Belt:** new item that holds up to 2 Aura Charms — a bare charm no longer pulses on its own, it must be socketed in the belt (hold belt + charm, right-click to socket; sneak + right-click to remove). This is the balance gate for the aura system: no more free-riding all 5 auras at once from your inventory.
- **Hive Synergy:** apiaries running the same aura now amplify each other when clustered within ~12 blocks — up to +60% pulse area from 4 neighbouring hives of the same aura. Build a themed bee-yard, not a scattered one.
- **JEI Aura category:** every aura now has its own JEI page listing its effect, radius and pulse interval.
- **In-game guide book (Patchouli):** covers the breeding tandem, the aura system, frame progression and tools. Craft it from a Book + Reinforced Propolis.
- **Witheria Ingot & Witheria Block:** closes a gap where the Witheria tier had a comb, nugget, frame and stick but no ingot or storage block.
- **Ranch Frame:** a flavour frame for the farmyard bee line (Clayious/Pig/Cow/Sheep/Potato/Carrot), crafted from leather, wool, clay and carrot instead of a metal tier.
- New hand-drawn textures for propolis, grafters, scoops, ingots, nuggets, storage blocks and the aura belt, replacing tinted placeholder art.
- A small "wow" touch: an action-bar readout naming the active aura (colour-matched) while you're inside its pulse radius.

### Changed
- **Frame tandem recipes now escalate:** Reinforced Frame requires a Diamond Frame; Draconic Frame requires both a Witheria and a Reinforced Frame (mirroring the bee's own mutation parents); Legendary Frame requires a Witheria Frame, a Draconic Frame, and a Nether Star.
- **Grafter recipes reworked:** the ingot now sits at the blade tip with 2 sticks forming the handle, instead of 3 plain sticks.
- **Bees drop nuggets, not ingots:** Reinforced/Legendary/Mutated bees now drop their metal as a nugget (matching the refining chain); Draconic and Witheria bees gained a nugget drop they were previously missing entirely.
- **Propolis is obtainable again:** centrifuging Draconic/Legendary/Reinforced/Witheria/Mutated combs now has a 10% chance of propolis, alongside the nugget and honey.
- Aura Charms are now crafted from the matching propolis instead of a bee comb.

### Fixed
- **Copper frame/stick recipes** no longer require the Thermal mod to be loaded — they only ever needed the vanilla copper ingot tag.
- **Witheria Frame was uncraftable:** its stick recipe depended on an Ingot that was never registered. Added the missing ingot end-to-end (item, model, textures, recipes, localization).
- Removed ~160 unused legacy texture files (old pre-refactor art, duplicate asset folders, orphaned tint sources) left over from earlier iterations.
- Removed dead duplicate bee-mutation JSON files that mirrored the real (Java-registered) mutation data but were never actually loaded by anything.

## [1.0.1] - 2025-11-04

### Fixed
- **JEI Integration:** All 11 bee breeding recipes now properly display in JEI
- **Frame Recipes:** Custom frame crafting recipes now appear in JEI
- **Thermal Integration:** Removed unused nickel, constantan, and enderium frames/sticks
- **Bee Mutations:** All mutations now registered programmatically for proper JEI display

### Changed
- Improved mutation registration system using `.addMutations()` API
- Optimized Thermal Expansion integration (8 metal frames: Tin, Silver, Lead, Copper, Bronze, Steel, Invar, Electrum)
- Updated JEI plugin for better compatibility

### Technical
- Mutations now use Forestry CE's native registration system
- Removed deprecated JSON-based mutation files
- All bee species properly integrated with JEI's breeding category

## [1.0.0] - 2025-11-03

### Added
- **11 Unique Bee Species:**
  - Agricultural Tier: Potato, Carrot
  - Livestock Tier: Pig, Cow, Sheep
  - Resource Tier: Clayious
  - Advanced Tier: Reinforced, Mutated
  - Elite Tier: Witheria, Draconic, Legendary

- **Advanced Tools:**
  - Reinforced Scoop (300 durability, 1.5x mining speed)
  - Draconic Scoop (700 durability, 2.0x mining speed)
  - Legendary Scoop (10,000 durability, 3.0x mining speed)
  - Reinforced Grafter (300 durability, 3x sapling modifier)
  - Draconic Grafter (700 durability, 5x sapling modifier)
  - Legendary Grafter (10,000 durability, 10x sapling modifier)

- **Bee Frames (10 types):**
  - Coal Frame (1.1x production, 100 durability)
  - Iron Frame (1.2x production, 200 durability)
  - Gold Frame (1.3x production, 150 durability)
  - Diamond Frame (1.4x production, 250 durability)
  - Emerald Frame (1.5x production, 300 durability)
  - Reinforced Frame (1.4x production, 500 durability)
  - Witheria Frame (2.0x production, 750 durability)
  - Draconic Frame (2.5x production, 1000 durability)
  - Mutation Frame (10x mutation chance, 40 durability)
  - Legendary Frame (5.0x production, 10,000 durability)

- **New Materials:**
  - Draconic Ingot, Nugget, and Block
  - Legendary Ingot, Nugget, and Block
  - Reinforced Ingot, Nugget, and Block
  - Witheria Ingot, Nugget, and Block
  - Mutated Iron Ingot, Nugget, and Block

- **Colored Propolis (5 types):**
  - Reinforced, Witheria, Mutated, Draconic, Legendary
  - Legendary and Draconic variants have enchantment glint

- **Complete Recipe System:**
  - 11 bee mutation recipes with balanced progression
  - 11 centrifuge recipes for comb processing
  - Crafting recipes for all tools, frames, and materials
  - Stick crafting for all material types

- **Full Localization:**
  - English (en_us)
  - Russian (ru_ru)

### Technical
- Built for Minecraft 1.20.1
- Requires Forge 47.2.0+
- Requires Forestry: Community Edition 7.0.0+
- Full integration with Forestry CE API
- Scoops properly tagged for hive breaking
- Grafters implement IToolGrafter interface
- All items properly colored and rendered

### Notes
- This is a spiritual successor to ForestryExtras by wasliebob
- Original textures and design philosophy preserved
- Completely rebuilt for modern Minecraft
- All features tested and working in-game

---

## Future Plans

### Planned for 1.1.0
- Thermal Foundation integration (Tin, Bronze, Silver, etc.)
- Additional frame types for modded materials
- More bee species variants
- Quality of life improvements

### Under Consideration
- JEI/REI integration improvements
- Patchouli guidebook
- Additional mutation paths
- Backpack integration
- More centrifuge products

---

## Legacy (ForestryExtras History)

This mod is built upon the foundation of ForestryExtras by wasliebob:
- Original mod for Minecraft 1.7.10 - 1.12.2
- Featured similar bee species and progression
- Included Thaumcraft and Tinkers' Construct integration
- Community favorite for Forestry 4 expansion

Bee Mastery brings these beloved features to modern Minecraft while expanding and improving upon the original vision.
