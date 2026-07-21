# [TaCZ] REMAKE For Paper (TRfP) Spec

## Why
原版 TACZ（Timeless and Classics Guns Zero）是一款基于 Minecraft Forge 1.20.1 的枪械模组，玩家群体庞大、玩法与枪械数据成熟，但仅支持 Forge 生态，Paper 服务端用户无法直接使用。本项目的目标是把 TACZ 的核心玩法与全部官方枪械移植到 Paper 1.21.1 插件平台，并重塑品牌为 [TaCZ] REMAKE For Paper（简称 TRfP），由 Lrxmcstudio 工作室的 Lrx 主导发布，让 Paper 服主也能给玩家带来原汁原味的 TACZ 体验。

## What Changes
- 在 Paper 1.21.1 上以纯插件形式提供原 TACZ 的全部官方枪械与玩法机制（射击、换弹、配件、附魔、弹药、瞄准等）。
- **BREAKING** 完全移除 Forge 依赖，改为 Paper 插件（plugin.yml + onEnable 启动）。
- 重写玩家可辨识文本：插件名、作者、简介、命令、Permission、Tab 提示、聊天前缀、HUD 文案、Lua 脚本作者署名、错误提示等均替换为 TRfP/Lrx/Lrxmcstudio.工作室署名；同时保留 TACZ 的玩法名、枪械代号、机制名（例如 "Gun"、"Reload"、"Attachment"、"Magazine" 等通用术语）以保证玩家可以认出这是 TACZ 的复刻。
- 资源（模型、贴图、音效）沿用 TACZ 官方资源以保留视觉/听觉体验，仅在 `plugin.yml` 与 lang 文件中替换品牌字符串。
- 采用 GPL-3.0（与原项目一致）发布代码；资源以 CC BY-NC-ND 4.0 标注并保留原作者署名。
- 通过 Lua 脚本系统（迁移自原 mod 的 lua 目录）支持自定义枪械行为。

## Impact
- Affected specs（无既有 spec；本规范即首个 capability）：
  - 枪械数据（GunData / GunItem / Attachment / Ammo / Magazine）
  - 战斗系统（射击 / 伤害 / 弹道 / 命中反馈）
  - 换弹与持枪逻辑（ReloadController / Aim / Charge / Melee）
  - 渲染层（HUD、准星、持枪姿态、动画）—— 由于 Bukkit 不支持自定义模型，本规范通过 Resource Pack + ArmorStand/ItemDisplay 伪模型实现。
  - 持久化（玩家枪械配置、附件、弹药数据）
  - 资源（assets 全部以 Resource Pack 形式分发）
  - Lua 引擎（迁移原 mod 的 lua 目录，集成到插件 ClassPath）
- Affected code:
  - 新建 `TRfP-core/`：Java 源码主目录（package: `studio.lrxmc.trfp`）
  - 新建 `TRfP-resourcepack/`：客户端资源包（assets、lang、models、textures、sounds）
  - 新建 `TRfP-lua/`：从 TACZ 移植的全部 .lua 脚本
  - 新建 `pom.xml` / `build.gradle`：构建脚本（Paper 1.21.1 API）
  - `plugin.yml`：插件元数据
  - `README.md`、`LICENSE`：重写后的品牌文档

## ADDED Requirements

### Requirement: 插件骨架与品牌重塑
插件 SHALL 以 `studio.lrxmc.trfp` 为主包，遵循 Paper 1.21.1 API 实现 `JavaPlugin`，在 `plugin.yml` 中声明：
- `name: [TaCZ]REMAKEForPaper`
- `version: 1.0.0`
- `main: studio.lrxmc.trfp.TRfPPlugin`
- `author: [Lrxmcstudio.工作室的Lrx]`
- `description: TRfP（Lrxmcstudio.工作室的Lrx 出品）—— Paper 1.21.1 平台上的 TACZ 复刻插件，保留原版全部枪械与玩法。`
- `api-version: '1.21'`
- `load: POSTWORLD`
- `commands`：`/trfp`（主命令）、`/trfp reload`、`/trfp give`、`/trfp version`
- `permissions`：`trfp.use`、`trfp.admin`、`trfp.command.give`

#### Scenario: 启动成功
- **WHEN** Paper 服务器启动并加载本插件
- **THEN** 控制台输出 `[TRfP] [TaCZ] REMAKE For Paper v1.0.0 已启用 — 作者 Lrxmcstudio.工作室的Lrx`，且 `plugin.yml` 中 `name` 与 `description` 字段对玩家可见。

#### Scenario: 玩家执行 /trfp version
- **WHEN** 玩家在聊天栏输入 `/trfp version`
- **THEN** 收到消息 `§6[TRfP] §f[TaCZ] REMAKE For Paper §7v1.0.0 §7| §f作者 §bLrxmcstudio.§3工作室的Lrx`。

### Requirement: 枪械物品系统
插件 SHALL 实现 `GunItem`（自定义 `ItemStack` 元数据 + 持久化 NBT），至少包含以下字段：`GunId`、`FireMode`、`Attachments`、`AmmoCount`、`DummyAmmoCount`、`Level`、`Skin`。

#### Scenario: 玩家获得枪械
- **WHEN** 玩家执行 `/trfp give <player> <gunId> [amount]`
- **THEN** 目标玩家获得对应枪械物品，NBT 写入完整 `GunId` 与默认 `AmmoCount=0`、默认配件槽。

#### Scenario: 持久化
- **WHEN** 玩家退出服务器
- **THEN** 其 `GunItem` 上的 `Attachments`、`Level`、`Skin` 写入玩家持久化文件；重新登录后保持一致。

### Requirement: 射击与战斗系统
插件 SHALL 提供：开火、弹道、伤害、命中反馈、爆头倍率、穿透、击退、灼烧、暴击、伤害衰减等机制；并暴露 `GunShootEvent` 供其他插件监听。

#### Scenario: 单发射击
- **WHEN** 玩家右键（默认 trigger key）触发枪械
- **THEN** 玩家以 NBT 中的 `FireMode`（半自动 / 全自动 / 三连发）发射子弹，并播放对应音效、生成粒子、施加后坐力。

#### Scenario: 弹道检测
- **WHEN** 子弹沿枪口方向飞行
- **THEN** 插件在 0.05s 步进内进行射线检测（Block + LivingEntity），命中时调用 TACZ 原版伤害公式并应用部位加成。

### Requirement: 换弹、瞄准、近战、刺刀系统
插件 SHALL 实现原版所有持枪副行为：换弹进度、瞄准 FOV/灵敏度缩放、近战挥击、刺刀刺击、副手切枪、潜行瞄准。

#### Scenario: 换弹
- **WHEN** 玩家按下 R 键且弹药不足
- **THEN** 进入换弹状态，按枪械 `ReloadData`（`ReloadTime`、`MagazineType`、`Feed`）从背包 / 弹药盒 / 副手拉取弹药并播放动画。

#### Scenario: 瞄准
- **WHEN** 玩家按住潜行（默认 aim key）
- **THEN** 玩家 FOV 由 normal 渐变至 aim 状态（每帧插值），持枪姿态从 `aiming=false` 切换为 `aiming=true`。

### Requirement: 附件（Attachment）系统
插件 SHALL 实现 4 类附件槽：`Scope`、`Grip`、`Stock`、`Barrel`、`Magazine`（枪械内置），并通过 NBT 持久化。

#### Scenario: 安装瞄准镜
- **WHEN** 玩家在工作台合成或在 GUI 中将 Scope 附件放入枪械的 `Scope` 槽
- **THEN** 枪械 `Attachments.Scope` 字段更新，玩家准星 HUD 切换为该 Scope 的 overlay 贴图。

### Requirement: HUD 与客户端资源
插件 SHALL 通过 Resource Pack 提供：
- `assets/trfp/textures/gui/...`：所有枪械 GUI、附件 GUI、弹药盒 GUI 贴图
- `assets/trfp/models/item/...`：枪械物品模型（ItemModel JSON）
- `assets/minecraft/font/...`（如需自定义字体）
- `assets/trfp/lang/zh_cn.json` 与 `en_us.json`：所有玩家可见字符串

#### Scenario: 自动下发资源包
- **WHEN** 玩家首次进入服务器
- **THEN** 服务器通过 `ResourcePackSendEvent` 或登录时下发 `TRfP-resourcepack.zip`，并提示 `§6[TRfP] §f客户端资源已下发，若未生效请使用 §a/resource pack §f重载。`

### Requirement: Lua 行为系统
插件 SHALL 内嵌 Lua 解释器（按原 mod 的实现选择 `luaj` 或 `NLua`），并按原 mod `src/main/resources/assets/tacz/scripts/` 目录组织 `TRfP-lua/`：
- `common/`：通用工具
- `gun/`：每把枪械的 on_shoot / on_reload / on_hit 钩子
- `attachment/`：附件属性覆盖

#### Scenario: 调用 Lua 钩子
- **WHEN** 玩家开火
- **THEN** 插件读取该枪械对应的 `gun/<GunId>.lua` 并调用 `on_shoot(player, gun_data, world)`；返回值用于调整伤害 / 弹道 / 命中反馈。

### Requirement: 配方与合成
插件 SHALL 通过 `CraftBukkit` 配方系统（`ShapedRecipe` / `ShapelessRecipe` / `FurnaceRecipe` / `SmithingTransformRecipe`）注册原 mod 中所有工作台、锻造台、熔炉配方。

#### Scenario: 合成枪械
- **WHEN** 玩家在工作台按枪械配方摆放材料
- **THEN** 输出槽出现对应 `GunItem`，NBT 完整。

### Requirement: 数据持久化
插件 SHALL 使用 `plugins/TRfP/data/<uuid>.yml` 存储玩家每把枪的 NBT（`GunId`、`Attachments`、`Level`、`Skin`、`Enchantments`）。

#### Scenario: 离线后再登录
- **WHEN** 玩家下线、重新上线
- **THEN** 其背包 / 末影箱中枪械的所有 NBT 字段与下线时一致。

### Requirement: 配置文件
插件 SHALL 在首次启动时生成 `plugins/TRfP/config.yml`，提供：
- `debug: false`
- `resource-pack-url: <默认 URL>`
- `resource-pack-sha1: <默认 SHA1>`
- `enable-lua: true`
- `max-attach-distance: 32`
- `damage-multiplier: 1.0`
- `headshot-multiplier: 1.5`
- `default-fire-mode: SEMI`
- `lang: zh_cn`

#### Scenario: 修改倍率
- **WHEN** 管理员修改 `damage-multiplier` 为 2.0 并 `/trfp reload`
- **THEN** 之后所有命中事件的伤害 ×2 应用。

## MODIFIED Requirements
无（首个版本，不存在既有需求被修改）。

## REMOVED Requirements
无（首个版本，不存在被移除的需求）。

## Out of Scope
- 1.20.1 Forge 兼容（不提供）。
- 自定义 3D 枪械模型（受 Paper 平台限制，使用 ItemDisplay/ArmorStand 伪模型 + Resource Pack 模型变体）。
- 服务端侧物理引擎（弹道、抛壳采用简化射线 + 粒子替代）。
- 客户端 mod（需玩家使用配套 Resource Pack，不强制 OptiFine / Sodium）。

## 风险与决策点（提交后用户需确认）
1. **Paper 1.21.1 实际版本**：Paper 当前 1.21.1 已发布；如需 1.21.4 / 1.21.11（用户笔误？）请确认目标 Paper 版本号。
2. **资源来源**：TACZ 美术资源 CC BY-NC-ND 4.0，**禁止商用**且**禁止衍生**。这与"重塑品牌但不修改资源"的要求一致，但**不允许把美术资源二次创作后**直接以 TRfP 名义发布二创材质。TRfP 分发的资源包将完整保留所有原作者署名（NekoCrane / Receke / Pos_2333）。
3. **Lua 引擎选择**：原 mod 使用 `org.luaj:luaj`；建议沿用以减少移植工作量。
4. **构建工具**：建议使用 Gradle（与原 mod 风格一致）。
5. **范围控制**：原 mod 有 80+ 把枪、20+ 附件、5+ 弹药类型；建议分阶段实施。
