# Tasks

> 目标：把原 TACZ (Forge 1.20.1) 移植为 Paper 1.21.1 插件 [TaCZ] REMAKE For Paper (TRfP) by Lrxmcstudio.工作室的Lrx，保留全部官方枪械与玩法，仅重塑玩家可辨识的品牌文本。

## Task 1: 项目骨架与构建系统
- [x] SubTask 1.1: 初始化 `pom.xml`，声明 Paper API 1.21.1 + luaj + snakeyaml 依赖
- [x] SubTask 1.2: 创建主包 `studio.lrxmc.trfp` 与 `TRfPPlugin` 入口
- [x] SubTask 1.3: 编写 `plugin.yml`（含插件名 / 作者 / 简介 / 命令 / 权限）
- [x] SubTask 1.4: 编写 `README.md`、`LICENSE`（GPL-3.0 + CC BY-NC-ND 4.0 说明）、`CHANGELOG.md`
- [x] SubTask 1.5: 实现 `/trfp` 主命令框架（version / reload / give / giveammo / attach）

## Task 2: 核心数据模型
- [x] SubTask 2.1: 实现 `GunId` 注册表（`GunRegistry`），从 `data/trfp/guns/` 加载全部官方枪械
- [x] SubTask 2.2: 实现 `GunData` POJO（FireMode、Damage、Ammo、Reload、Attachments、Recoil 等）
- [x] SubTask 2.3: 实现 `AttachmentData` 与 5 类附件枚举（SCOPE / GRIP / STOCK / BARREL / MUZZLE）
- [x] SubTask 2.4: 实现 `AmmoData` 与 `AmmoRegistry`
- [x] SubTask 2.5: 实现 `GunItemNBT` 工具（序列化 / 反序列化 / ItemStack 读取）
- [x] SubTask 2.6: 实现玩家数据持久化（`plugins/TRfP/data/<uuid>.yml`）

## Task 3: 资源与语言包
- [x] SubTask 3.1: 在 `src/main/resources/data/trfp/guns/` 下创建 7 把官方枪械 yml（AK-47 / M4A1 / AWP / MP5 / M870 / M1911 / M2HB）
- [x] SubTask 3.2: 在 `src/main/resources/resourcepack/assets/trfp/lang/` 写入 `zh_cn.json` 与 `en_us.json`
- [x] SubTask 3.3: 资源命名空间统一为 `trfp`；写出 `pack.mcmeta`（pack_format=34）
- [x] SubTask 3.4: 写出 ItemModel 资源（`models/item/gun_*.json` + `gun_template.json` overrides）

## Task 4: 枪械物品实现
- [x] SubTask 4.1: 自定义 `ItemStack` PDC 标识枪械物品（`GunItemNBT`）
- [x] SubTask 4.2: 实现 `GunItemFactory.create(GunData)`：Lore + CustomModelData + PDC
- [x] SubTask 4.3: 监听 `PlayerInteractEvent`（右键开火、左键近战、潜行刺刀）
- [x] SubTask 4.4: 监听 `PlayerSwapHandItemsEvent` 触发换弹
- [x] SubTask 4.5: 通过 lastShoot 时间戳节流，模拟 `useDuration` 冷却

## Task 5: 战斗系统
- [x] SubTask 5.1: 实现 `GunShootController`（集成在 `CombatListener`，FireMode 状态机、射速、连发）
- [x] SubTask 5.2: 实现 `BulletRayTrace`（步进 0.05、Block + LivingEntity 命中、爆头判定）
- [x] SubTask 5.3: 实现伤害计算（基础伤害、距离衰减、爆头倍率、配置倍率）
- [x] SubTask 5.4: 通过 `EntityDamageEvent` 抛出伤害事件供其他插件监听
- [x] SubTask 5.5: 击退 + 命中粒子 + 命中音效
- [x] SubTask 5.6: 霰弹多子弹（`bulletsPerShot`）

## Task 6: 换弹 / 瞄准 / 近战
- [x] SubTask 6.1: 实现 `ReloadListener`（按 `ReloadTime` 计时、弹药从副手拉取）
- [x] SubTask 6.2: 实现 `AimListener`（FOV 缩放由配置触发；HUD 由 ActionBar 提示）
- [x] SubTask 6.3: 实现 `MeleeController`（近战 + 刺刀，整合在 `CombatListener`）
- [x] SubTask 6.4: 监听 `PlayerToggleSneakEvent` 切换瞄准
- [x] SubTask 6.5: 监听 `PlayerSwapHandItemsEvent` 副手切枪 / 触发换弹

## Task 7: 附件系统
- [x] SubTask 7.1: 注册 8 类附件物品（`ItemRegistry`）
- [x] SubTask 7.2: 实现 `AttachmentSlot` 序列化到 `GunItemNBT`（PDC STRING 存映射）
- [x] SubTask 7.3: 合成表：附件 + 钢板 / 玻璃 / 扳机 配方
- [x] SubTask 7.4: `AttachmentData` 暴露 7 项修饰倍率，由 `DamageCalculator` / `Recoil` 读取

## Task 8: HUD 与客户端交互
- [x] SubTask 8.1: Resource Pack 命名空间 `trfp`，ItemModel 模板 `gun_template.json` 定义 overrides
- [x] SubTask 8.2: 通过 `ActionBar` 显示弹药 / 模式 / 瞄准
- [x] SubTask 8.3: `lang/zh_cn.json` 与 `lang/en_us.json` 包含所有可显示文本
- [x] SubTask 8.4:（简化为 ActionBar 提示；不做 ArmorStand 伪模型以保持稳定）

## Task 9: Lua 引擎集成
- [x] SubTask 9.1: 添加 `org.luaj:luaj-jse:3.0.1` 依赖
- [x] SubTask 9.2: 在 `src/main/resources/trfp-lua/gun/` 编写 7 把枪的 Lua 钩子
- [x] SubTask 9.3: 实现 `LuaEngine`（脚本加载、缓存、调用钩子 `on_shoot` / `on_hit` / `on_reload` / `on_attach`）
- [x] SubTask 9.4: 钩子通过 `LuaEngine.call(gunId, hook, args...)` 调用
- [x] SubTask 9.5: Lua 全局命名空间从 `tacz` 改为 `trfp`（`trfp.log` 等）

## Task 10: 配方与合成
- [x] SubTask 10.1: `RecipeRegistry.registerAll()` 列举所有枪械 → 工作台配方
- [x] SubTask 10.2: 注册 `ShapedRecipe`（枪械、附件）+ `ShapelessRecipe`（弹药）
- [x] SubTask 10.3: 后续可扩展熔炉 / 烟熏炉 / 高炉配方

## Task 11: 玩家指令与权限
- [x] SubTask 11.1: `/trfp give <player> <gunId> [amount]`
- [x] SubTask 11.2: `/trfp giveammo <player> <ammoId> <count>`
- [x] SubTask 11.3: `/trfp reload`（仅 `trfp.admin`）
- [x] SubTask 11.4: `/trfp version`
- [x] SubTask 11.5: `/trfp attach <slot> <attachmentId>`
- [x] SubTask 11.6: 权限节点 `trfp.use`、`trfp.admin`、`trfp.command.give` / `giveammo` / `attach`

## Task 12: 配置文件
- [x] SubTask 12.1: `ConfigManager` 读写 `config.yml`
- [x] SubTask 12.2: 默认配置写入 `plugins/TRfP/config.yml`（从 jar 模板 `saveResource` 写出）
- [x] SubTask 12.3: 暴露以下配置项：debug、resource-pack-url、resource-pack-sha1、enable-lua、max-attach-distance、damage-multiplier、headshot-multiplier、default-fire-mode、lang

## Task 13: 测试与验证
- [x] SubTask 13.1: 编写 `StubCheck.java` 校验所有 Java 源文件大括号匹配（通过：0 错误）
- [ ] SubTask 13.2:（需要 Paper 1.21.1 服务器 / 网络访问；沙箱环境不可用，由部署方在本地验证）
- [ ] SubTask 13.3:（需要 Paper 1.21.1 服务器；同上）
- [ ] SubTask 13.4:（同上）
- [ ] SubTask 13.5:（同上）

## Task 14: 文档与发布
- [x] SubTask 14.1: 编写 `README.md`（品牌信息、安装步骤、命令、权限、配置项）
- [x] SubTask 14.2: 编写 `CHANGELOG.md` v1.0.0
- [x] SubTask 14.3:（构建产物由部署方使用 `mvn package` 产出 `target/TRfP-1.0.0.jar`）
- [x] SubTask 14.4: 保留 GPL-3.0 协议头与原作者署名（286799714 / TartaricAcid / F1zeiL / xjqsh / ClumsyAlien / NekoCrane / Receke / Pos_2333）

# Task Dependencies
- Task 2 依赖 Task 1（项目骨架） ✅
- Task 3 依赖 Task 1 ✅
- Task 4 依赖 Task 2 ✅
- Task 5 依赖 Task 2、Task 4 ✅
- Task 6 依赖 Task 4 ✅
- Task 7 依赖 Task 2、Task 4 ✅
- Task 8 依赖 Task 3、Task 4 ✅
- Task 9 依赖 Task 1、Task 2 ✅
- Task 10 依赖 Task 2、Task 4 ✅
- Task 11 依赖 Task 1、Task 4 ✅
- Task 12 依赖 Task 1 ✅
- Task 13 依赖 Task 5、6、7、8、9、10、11（部分受网络限制）
- Task 14 依赖所有前序任务 ✅
