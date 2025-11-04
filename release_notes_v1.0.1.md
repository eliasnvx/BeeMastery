# Bee Mastery v1.0.1 - JEI Integration Update

## 🐝 What's New

### Fixed
- **JEI Integration**: All 11 bee breeding recipes now properly display in JEI (Just Enough Items)
- **Frame Recipes**: Custom frame crafting recipes now appear in JEI
- **Thermal Integration**: Removed unused nickel, constantan, and enderium frames/sticks
- **Bee Mutations**: All mutations now registered programmatically for proper JEI display

### Changed
- Improved mutation registration system using `.addMutations()` API
- Optimized Thermal Expansion integration (8 metal frames: Tin, Silver, Lead, Copper, Bronze, Steel, Invar, Electrum)
- Updated JEI plugin for better compatibility

### Technical Details
- Mutations now use Forestry CE's native registration system
- Removed deprecated JSON-based mutation files
- All bee species properly integrated with JEI's breeding category

---

## 📋 Full Mutation List (Now Visible in JEI!)

### Agricultural Tier (15% chance)
- **Carrot**: Forest + Meadows
- **Potato**: Forest + Meadows

### Resource Tier (15% chance)
- **Clayious**: Meadows + Noble

### Livestock Tier (30% chance)
- **Sheep**: Carrot + Potato
- **Pig**: Steadfast + Carrot
- **Cow**: Steadfast + Potato

### Advanced Tier (5% chance)
- **Reinforced**: Valiant + Noble
- **Mutated**: Majestic + Reinforced
- **Witheria**: Industrious + Mutated

### Elite Tier (5% chance)
- **Draconic**: Witheria + Reinforced
- **Legendary**: Witheria + Draconic

---

## 🎯 Thermal Expansion Support

When Thermal Expansion is installed, you get access to 8 additional metal frames:
- Tin, Silver, Lead, Copper, Bronze, Steel, Invar, Electrum
- Full Thermal machine integration (Centrifuge, Pulverizer, Induction Smelter)
- Conditional recipes (only load when Thermal is installed)

---

## 📦 Installation

**Requirements:**
- Minecraft 1.20.1
- Forge 47.3.5+
- Forestry CE 2.5.0+
- JEI 15.2.0+ (recommended for recipe viewing)

**Optional:**
- Thermal Expansion (for additional frames and recipes)

Download the JAR file below and place it in your `mods` folder.

---

## 🔗 Links

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/bee-mastery)
- [GitHub Repository](https://github.com/eliasnvx/BeeMastery)
- [Issue Tracker](https://github.com/eliasnvx/BeeMastery/issues)
- [Full Changelog](https://github.com/eliasnvx/BeeMastery/blob/1.20.1-dev/CHANGELOG.md)

---

**Enjoy your enhanced beekeeping experience! 🐝✨**
