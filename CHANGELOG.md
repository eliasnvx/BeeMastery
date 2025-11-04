# Changelog

All notable changes to Bee Mastery will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
