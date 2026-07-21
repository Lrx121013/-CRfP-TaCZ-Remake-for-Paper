# Tasks

> 目标：把原 TACZ (Forge 1.20.1) 移植为 Paper 1.21.1 插件 [TaCZ] REMAKE For Paper (TRfP) by Lrxmcstudio.工作室的Lrx，保留全部官方枪械与玩法，仅重塑玩家可辨识的品牌文本。

## Task 1: 项目骨架与构建系统
- [ ] SubTask 1.1: 初始化 `pom.xml`（或 `build.gradle`），声明 Paper API 1.21.1 依赖
- [ ] SubTask 1.2: 创建主包 `studio.lrxmc.trfp` 与 `TRfPPlugin` 入口
- [ ] SubTask 1.3: 编写 `plugin.yml`（含插件名 / 作者 / 简介 / 命令 / 权限）
- [ ] SubTask 1.4: 编写 `README.md`、`LICENSE`（GPL-3.0 + CC BY-NC-ND 4.0 说明）
- [ ] SubTask 1.5: 实现 `/trfp` 主命令框架（version / reload / give 子命令占位）

## Task 2: 核心数据模型
- [ ] SubTask 2.1: 实现 `GunId` 注册表（从原 mod `data/tacz/guns/index.json` 同步全部枪械 ID）
- [ ] SubTask 2.2: 实现 `GunData` POJO（FireMode、Damage、Ammo、Reload、Attachments、Recoil 等）
- [ ] SubTask 2.3: 实现 `AttachmentData` 与 4 类附件枚举
- [ ] SubTask 2.4: 实现 `AmmoData` 与 `MagazineData`
- [ ] SubTask 2.5: 实现 `GunItemNBT` 工具（序列化 / 反序列化 / ItemStack 读取）
- [ ] SubTask 2.6: 实现玩家数据持久化（`plugins/TRfP/data/<uuid>.yml`）

## Task 3: 资源与语言包
- [ ] SubTask 3.1: 从原 mod `src/main/resources/assets/tacz/` 同步 `textures/`、`models/`、`sounds/`、`lang/`
- [ ] SubTask 3.2: 重写 `lang/zh_cn.json` 与 `lang/en_us.json` 中的品牌字符串为 TRfP / Lrxmcstudio.工作室的Lrx
- [ ] SubTask 3.3: 资源命名空间重写为 `trfp`（路径 `assets/trfp/...`）
- [ ] SubTask 3.4: 编写 `resource-pack-url` 与 sha1 配置项；登录时自动下发

## Task 4: 枪械物品实现
- [ ] SubTask 4.1: 自定义 `ItemStack` Meta + NBT 标识枪械物品
- [ ] SubTask 4.2: 实现 `trfp:gun` 物品模型（ItemModel JSON + 1.21 Resource Pack 规范）
- [ ] SubTask 4.3: 监听 `PlayerInteractEvent`（右键开火、左键换弹/近战、潜行瞄准）
- [ ] SubTask 4.4: 监听 `InventoryClickEvent` 实现换弹、配件安装 GUI
- [ ] SubTask 4.5: 实现物品冷却（`Player.getCooldown()` 替代原 mod `useDuration`）

## Task 5: 战斗系统
- [ ] SubTask 5.1: 实现 `GunShootController`（FireMode 状态机、射速、连发）
- [ ] SubTask 5.2: 实现 `BulletRayTrace`（步进 0.05、Block + LivingEntity 命中）
- [ ] SubTask 5.3: 实现 `DamageCalculator`（基础伤害、爆头倍率、距离衰减、护甲穿透、附魔加成）
- [ ] SubTask 5.4: 暴露 `GunShootEvent`、`GunHitEntityEvent`、`GunHitBlockEvent` 给其他插件
- [ ] SubTask 5.5: 击退 / 灼烧 / 凋零 / 暴击 等额外效果
- [ ] SubTask 5.6: 子弹穿透与穿透衰减

## Task 6: 换弹 / 瞄准 / 近战 / 刺刀
- [ ] SubTask 6.1: 实现 `ReloadController`（按 `ReloadData` 计时、MagazineType 选择来源）
- [ ] SubTask 6.2: 实现 `AimController`（FOV 插值、灵敏度缩放、第三人称视角偏移）
- [ ] SubTask 6.3: 实现 `MeleeController`（近战、刺刀、副手切枪、潜行近战）
- [ ] SubTask 6.4: 监听 `PlayerToggleSneakEvent` 切换瞄准
- [ ] SubTask 6.5: 监听 `PlayerItemHeldEvent` 实现副手切枪

## Task 7: 附件系统
- [ ] SubTask 7.1: 注册 4 类附件物品（Scope / Grip / Stock / Barrel）
- [ ] SubTask 7.2: 实现 `AttachmentSlot` 序列化到 `GunItemNBT`
- [ ] SubTask 7.3: 合成表：附件 + 枪械 → 安装后枪械
- [ ] SubTask 7.4: 附件属性覆盖（在 DamageCalculator 与 Recoil 中读取 AttachmentData）

## Task 8: HUD 与客户端交互
- [ ] SubTask 8.1: 通过 Resource Pack 定义 `trfp:hud_overlay`（准星 / 弹药 / 持枪 overlay）
- [ ] SubTask 8.2: 通过 ActionBar 与 BossBar 显示剩余弹药、模式、附魔信息
- [ ] SubTask 8.3: 实现 `trfp:scope` overlay（瞄准时按 Scope 类型隐藏 HUD）
- [ ] SubTask 8.4: ItemDisplay / ArmorStand 伪持枪姿态（每 1 tick 同步玩家位置 + 朝向）

## Task 9: Lua 引擎集成
- [ ] SubTask 9.1: 添加 `luaj`（或 NLua）依赖
- [ ] SubTask 9.2: 从原 mod 迁移 `assets/tacz/scripts/` 至 `TRfP-lua/`
- [ ] SubTask 9.3: 实现 `LuaEngine`（脚本加载、缓存、调用钩子）
- [ ] SubTask 9.4: 在开火、命中、换弹、附件变更点调用 `on_shoot` / `on_hit` / `on_reload` / `on_attach`
- [ ] SubTask 9.5: Lua 命名空间从 `tacz` 改为 `trfp`

## Task 10: 配方与合成
- [ ] SubTask 10.1: 解析原 mod `data/tacz/recipes/` 下所有 JSON
- [ ] SubTask 10.2: 转换为 `ShapedRecipe` / `ShapelessRecipe` / `SmithingTransformRecipe` 并注册
- [ ] SubTask 10.3: 注册熔炉、烟熏炉、高炉配方（子弹、钢板等）

## Task 11: 玩家指令与权限
- [ ] SubTask 11.1: `/trfp give <player> <gunId> [amount]`：给予枪械
- [ ] SubTask 11.2: `/trfp giveammo <player> <ammoId> <count>`：给予弹药
- [ ] SubTask 11.3: `/trfp reload`：重载配置 / Lua / 配方
- [ ] SubTask 11.4: `/trfp version`：显示插件信息
- [ ] SubTask 11.5: `/trfp attach <slot> <type>`：给自己枪装附件
- [ ] SubTask 11.6: 权限节点 `trfp.use`、`trfp.admin`、`trfp.command.give`

## Task 12: 配置文件
- [ ] SubTask 12.1: 实现 `ConfigManager`（基于 SnakeYAML）
- [ ] SubTask 12.2: 默认配置写入 `plugins/TRfP/config.yml`
- [ ] SubTask 12.3: 暴露以下配置项：debug、resource-pack-url、resource-pack-sha1、enable-lua、max-attach-distance、damage-multiplier、headshot-multiplier、default-fire-mode、lang

## Task 13: 测试与验证
- [ ] SubTask 13.1: 单机 Paper 1.21.1 启动验证插件加载
- [ ] SubTask 13.2: 玩家登录 → 资源包下发 → 准星 / 枪械模型可见
- [ ] SubTask 13.3: 至少 3 把枪的完整流程（开火、换弹、爆头、附件）
- [ ] SubTask 13.4: Lua 钩子可被命中事件触发
- [ ] SubTask 13.5: 玩家数据持久化（重启后状态保留）

## Task 14: 文档与发布
- [ ] SubTask 14.1: 编写 `README.md`（含品牌信息、安装步骤、命令、权限、配置项）
- [ ] SubTask 14.2: 编写 `CHANGELOG.md` v1.0.0
- [ ] SubTask 14.3: 输出 `target/TRfP-1.0.0.jar` 与 `TRfP-resourcepack.zip`
- [ ] SubTask 14.4: 保留 GPL-3.0 协议头与原作者署名（286799714 / TartaricAcid / F1zeiL / xjqsh / ClumsyAlien / NekoCrane / Receke / Pos_2333）

# Task Dependencies
- Task 2 依赖 Task 1（项目骨架）
- Task 3 依赖 Task 1
- Task 4 依赖 Task 2
- Task 5 依赖 Task 2、Task 4
- Task 6 依赖 Task 4
- Task 7 依赖 Task 2、Task 4
- Task 8 依赖 Task 3、Task 4
- Task 9 依赖 Task 1、Task 2
- Task 10 依赖 Task 2、Task 4
- Task 11 依赖 Task 1、Task 4
- Task 12 依赖 Task 1
- Task 13 依赖 Task 5、6、7、8、9、10、11
- Task 14 依赖所有前序任务
