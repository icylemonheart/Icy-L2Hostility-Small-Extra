# Changelog

## 1.0.2

### Added
- **Fusion Charm**: crafted in the crafting grid from any 4 Pandora charms (including the Charm of Luck; duplicates allowed) + 4 Reality suppression traits + 1 Master trait. The Fusion Charm inherits all attributes of the 4 charms (including their upgrade levels), with attribute lines formatted exactly like the vanilla charms.
- **Damage-reduction soft cap**: config option `[fusion_charm].damageReductionCap`, default 0.99 (at most 99% damage reduction). Set to 1 to allow full 100% immunity. Applies globally to the damage-taken attribute.

### Changed
- Upgrade level cap generalized: after raising `maxLevel` in the config, charms can be upgraded further. Multipliers for higher levels are extrapolated geometrically from the tier II / III ratios, and item names display Roman numerals for IV, V, and beyond.
- Fusion recipe is now data-driven: ingredient composition and charm count are defined in JSON, so modpacks can override them via data packs (swap materials or change the number of fused charms).

## 1.0.1

### Added
- **Pandora charm tiered upgrades** (CurseOfPandora integration): all 11 Pandora charms and the Charm of Luck support I -> II -> III upgrades.
  - I -> II: 1 tier I charm + 4 of the charm's vanilla trait materials + 4 grouped trait materials (health/armor/speed/protection: Undying, Repelling; damage/archery/magic/explosion: Pulling, Dispel; accuracy/critical/heavy weapon: Reprint, Teleport).
  - II -> III: 1 tier II charm + 4 Arena traits + 4 Ragnarok traits.
  - Charm of Luck: I -> II costs Miracle Powder; II -> III costs Heirophant Green.
- Tier II / III attribute multipliers and the maximum level are all configurable; every recipe supports JEI display and data-pack overrides.

## 1.0.0

### Initial release
- 4 L2Hostility expansion accessories: Beyond Physics (physical penetration), Sealed Magic (magic penetration), Space Lock (suppresses enderman teleportation), No More Immortal (counters the Undying trait and applies curse).
- Hostility system interactions: right-clicking mobs with a Bottle of Sanity or Hostility Essence purifies or increases their hostility level; the Looting Charm supports right-click item extraction and loot selection.
