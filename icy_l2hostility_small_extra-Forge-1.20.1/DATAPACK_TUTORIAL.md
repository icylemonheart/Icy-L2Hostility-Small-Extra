# KubeJS 教程：修改配方与创建新等级

本模组（icy_l2hostility_small_extra）的所有配方都由数据包驱动。借助 KubeJS 的虚拟数据包功能，无需新建数据包、无需修改 jar，直接在 `kubejs/data` 下放 JSON 文件即可覆盖原有配方、调整材料，甚至扩展 III 级以上的新强化等级。

本教程适用于 Minecraft 1.20.1 Forge + KubeJS 6。

---

## 一、基本用法

KubeJS 会把 `kubejs/data` 目录当作一个虚拟数据包加载。把配方 JSON 按"命名空间/路径"放进去即可，例如：

```
.kubejs/
└─ data/
   └─ icy_l2hostility_small_extra/
      └─ recipes/
         └─ （配方 JSON 放这里）
```

游戏内执行 `/reload` 即可生效，无需重启。

**核心规则**：文件路径与模组内置配方的路径（即配方 ID）完全相同 → 覆盖原版配方；使用新文件名 → 新增配方。

---

## 二、修改升级配方（I→II、II→III）

### 文件位置

```
kubejs/data/icy_l2hostility_small_extra/recipes/charm_upgrade/<文件名>.json
```

模组内置 24 个文件，每个宝珠两个：

| 文件名前缀 | 宝珠 |
|---|---|
| charm_of_health | 生命 |
| charm_of_armor | 护甲 |
| charm_of_speed | 速度 |
| charm_of_protection | 守护 |
| charm_of_damage | 伤害 |
| charm_of_archery | 射手 |
| charm_of_magic | 魔能 |
| charm_of_explosion | 爆破 |
| charm_of_accuracy | 凝神 |
| charm_of_critical | 狂暴 |
| charm_of_heavy_weapon | 重装 |
| charm_of_luck | 幸运护符 |

例如覆盖生命宝珠的 I→II 配方，路径就是：

```
kubejs/data/icy_l2hostility_small_extra/recipes/charm_upgrade/charm_of_health.json
```

### JSON 字段说明

```json
{
  "type": "icy_l2hostility_small_extra:charm_upgrade",
  "conditions": [
    { "type": "forge:mod_loaded", "modid": "curseofpandora" }
  ],
  "level": 0,
  "charm": { "item": "curseofpandora:charm_of_health" },
  "materials": [
    { "ingredient": { "item": "l2hostility:tank" }, "count": 4 },
    { "ingredient": { "item": "l2hostility:undying" }, "count": 2 },
    { "ingredient": { "item": "l2hostility:repelling" }, "count": 2 }
  ]
}
```

| 字段 | 说明 |
|---|---|
| `type` | 固定为 `icy_l2hostility_small_extra:charm_upgrade` |
| `level` | 输入宝珠必须达到的等级：`0` = I→II，`1` = II→III，`2` = III→IV，以此类推 |
| `charm` | 要升级的宝珠（Ingredient 格式，见第四节） |
| `materials` | 材料数组，每项由 `ingredient`（材料）和 `count`（数量）组成 |

合成台会精确校验：恰好 1 个对应等级的宝珠 + 每种材料的指定数量。

---

## 三、修改融合配方

融合配方只有一个，覆盖路径：

```
kubejs/data/icy_l2hostility_small_extra/recipes/fusion_charm.json
```

### JSON 字段说明

```json
{
  "type": "icy_l2hostility_small_extra:fusion_charm",
  "conditions": [
    { "type": "forge:mod_loaded", "modid": "curseofpandora" }
  ],
  "charm": { "tag": "icy_l2hostility_small_extra:fusion_charms" },
  "charm_count": 4,
  "materials": [
    { "ingredient": { "item": "l2hostility:master" }, "count": 1 },
    { "ingredient": { "item": "curseofpandora:reality" }, "count": 3 },
    { "ingredient": { "item": "caerula_arbor:moist_star" }, "count": 1 }
  ]
}
```

| 字段 | 说明 |
|---|---|
| `charm` | 可被融合的宝珠（可用 tag 批量指定） |
| `charm_count` | 需要几个宝珠，融合宝珠会继承全部这些宝珠的属性 |
| `materials` | 其余材料及数量 |

注意：普通工作台只有 3×3 = **9 格**，`charm_count` 与所有材料数量之和不能超过 9。

### 让其他饰品也能参与融合

`charm` 字段直接决定可融合范围。例如换成自定义标签：

```json
"charm": { "tag": "mypack:my_fusion_inputs" }
```

然后在 `kubejs/data/mypack/tags/items/my_fusion_inputs.json` 中列出物品。被列入的物品只要是 Curios 饰品，其属性就会被融合宝珠继承。

---

## 四、Ingredient 写法参考

`charm` 和每项 `ingredient` 都使用原版 Ingredient 格式，常用写法：

```json
{ "item": "l2hostility:tank" }
```
指定单个物品。

```json
{ "tag": "icy_l2hostility_small_extra:fusion_charms" }
```
匹配一个物品标签下的全部物品（标签内任意物品都算数）。

本模组常用物品 ID：

| 物品 | ID |
|---|---|
| 现实压制 | `curseofpandora:reality` |
| 领主 | `l2hostility:master` |
| 领域 | `l2hostility:arena` |
| 诸神黄昏 | `l2hostility:ragnarok` |
| 奇迹粉末 | `l2hostility:miracle_powder` |
| 法皇之绿 | `l2complements:heirophant_green` |
| 受潮的下界之星 | `caerula_arbor:moist_star` |

> 引用不存在的物品 ID 会导致该配方加载失败（JEI 中消失），修改后请务必进游戏核对。

---

## 五、创建新等级（IV、V……）

模组默认只到 III 级。扩展新等级需要**两步**。

### 第一步：提高配置上限

修改配置文件 `config/icy/icy_pandora_charm-common.toml`：

```toml
[pandora_charm]
    maxLevel = 3
```

`maxLevel` 是允许达到的最高 NBT 等级：`2` = III 级封顶，`3` = 可到 IV 级，`4` = 可到 V 级。

> 这是总闸门：即使写了升级配方，`maxLevel` 没提高，合成台也不会识别。

### 第二步：添加升级配方

新建 III→IV 配方，例如守护宝珠，路径：

```
kubejs/data/icy_l2hostility_small_extra/recipes/charm_upgrade/charm_of_protection_iv.json
```

内容：

```json
{
  "type": "icy_l2hostility_small_extra:charm_upgrade",
  "conditions": [
    { "type": "forge:mod_loaded", "modid": "curseofpandora" }
  ],
  "level": 2,
  "charm": { "item": "curseofpandora:charm_of_protection" },
  "materials": [
    { "ingredient": { "item": "l2hostility:arena" }, "count": 4 },
    { "ingredient": { "item": "l2hostility:ragnarok" }, "count": 4 }
  ]
}
```

关键是 `"level": 2`——表示输入必须是 III 级宝珠。再往上，IV→V 用 `"level": 3`，依此类推。每种宝珠都需要单独一个配方文件。

### 新等级的属性怎么算

各等级倍率为等比数列：

```
倍率(n) = tier1Multiplier × (tier2Multiplier / tier1Multiplier)^(n−1)，n ≥ 2
```

默认配置（II = 1.5，III = 2.0）下：

| 等级 | 倍率 |
|---|---|
| II | 1.5 |
| III | 2.0 |
| IV | 2.667 |
| V | 3.556 |
| VI | 4.741 |

物品名后缀自动显示 IV、V 等罗马数字；融合宝珠继承这些等级时同样按缩放后的数值生效。

减伤软上限（`[fusion_charm].damageReductionCap`，默认 0.99）对新等级依然有效。

---

## 六、生效方式

1. 把 JSON 按上述路径放入 `kubejs/data/`
2. 进入世界，执行 `/reload`（开发环境也可直接重启游戏）
3. JEI 中查看配方确认修改已生效

---

## 七、排错

| 现象 | 原因与处理 |
|---|---|
| JEI 里配方消失 | 多半是引用了不存在的物品/标签 ID，或 JSON 语法错误；检查逗号、引号 |
| JEI 能看到配方但合成台不识别 | 检查宝珠 NBT 等级是否与 `level` 字段一致；新等级还要确认 `maxLevel` 已提高 |
| 覆盖没生效 | 确认文件路径（配方 ID）与内置文件完全一致；已执行 `/reload` |
| 修改融合配方后材料数量对不上 | 清点 `charm_count` + 全部材料数量，总和须 ≤ 9 |
| `/reload` 报错 | 查看 `logs/latest.log` 中含对应文件名的错误行，按提示修正 JSON |
