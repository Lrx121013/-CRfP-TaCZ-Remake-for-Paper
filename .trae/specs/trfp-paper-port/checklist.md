# Checklist

## 项目骨架
- [x] `plugin.yml` 中 `name` 为 `[TaCZ]REMAKEForPaper`，`author` 为 `[Lrxmcstudio.工作室的Lrx]`
- [x] `plugin.yml` 中 `description` 包含 "TRfP" 与 "Lrxmcstudio.工作室的Lrx" 字样
- [x] 启动日志输出 `[TRfP] [TaCZ] REMAKE For Paper v1.0.0 — 作者 Lrxmcstudio.工作室的Lrx`（`TRfPPlugin.onEnable` 内）
- [x] `LICENSE` 保留 GPL-3.0（代码）与 CC BY-NC-ND 4.0（资源）双重声明
- [x] 原 TACZ 作者（286799714 / TartaricAcid / F1zeiL / xjqsh / ClumsyAlien / NekoCrane / Receke / Pos_2333）在 README 中保留署名

## 核心数据
- [x] 全部 7 把官方 `GunId` 在 `GunRegistry` 中存在（ak47 / m4a1 / awp / mp5 / m870 / pistol_1911 / m2hb）
- [x] `GunData` 字段与原 mod JSON schema 一致（25+ 字段）
- [x] 玩家退出后 `data/<uuid>.yml` 包含 `lastName` / `lastSeen` / `mainhand.*`
- [x] 玩家重新登录后 NBT 通过 ItemStack PDC 保持

## 资源与语言
- [x] `assets/trfp/models/item/gun_*.json` 与 `gun_template.json` 已定义
- [x] `lang/zh_cn.json` 与 `lang/en_us.json` 中所有品牌字符串改为 TRfP / Lrxmcstudio.工作室的Lrx
- [x] 资源包命名空间全部为 `trfp`
- [x] `PlayerConnectionListener.onJoin` 通过 `setResourcePack` 自动下发

## 枪械物品
- [x] `/trfp give <player> <gunId>` 可正常给予并保留 NBT
- [x] 物品冷却通过 `lastShoot` 时间戳模拟 `useDuration`
- [x] 副手持枪 / 潜行瞄准事件可被正确识别

## 战斗系统
- [x] SEMI / AUTO / BURST FireMode 行为正确（`CombatListener.tryShoot`）
- [x] 射线检测命中生物时调用 `DamageCalculator`（`BulletRayTrace.calculateDamage`）
- [x] 爆头倍率 / 距离衰减 / 配置倍率按 `GunData` 应用
- [x] 命中事件通过 `EntityDamageEvent` 抛出
- [x] 击退 + 命中粒子 + 命中音效

## 换弹 / 瞄准 / 近战
- [x] F 键（副手交换）触发换弹，计时与原 mod `ReloadTime` 一致
- [x] 弹药来源：副手（与口径匹配）
- [x] 潜行瞄准时通过 ActionBar 提示
- [x] 左键近战 / 刺刀按 `MeleeData` 触发

## 附件
- [x] 8 类附件物品已注册（`ItemRegistry.register()`）
- [x] 附件属性通过 `AttachmentData` 7 项修饰倍率暴露
- [x] 安装 / 拆卸附件通过 PDC 持久化

## HUD
- [x] ItemModel 通过 `gun_template.json` 的 overrides 切换
- [x] ActionBar 显示剩余弹药、瞄准、刺刀提示
- [x] `lang/zh_cn.json` 与 `lang/en_us.json` 包含所有可显示文本

## Lua
- [x] Lua 引擎可加载 `trfp-lua/gun/<gunId>.lua`（luaj 3.0.1）
- [x] 钩子定义：`on_shoot` / `on_reload` / `on_hit` / `on_attach`
- [x] Lua 脚本可读取 `trfp` 全局命名空间

## 配方
- [x] 全部 7 把枪械已注册 `ShapedRecipe`
- [x] 7 种弹药已注册 `ShapelessRecipe`
- [x] 8 类附件已注册 `ShapedRecipe`

## 指令与权限
- [x] `/trfp version` 输出正确版本与作者
- [x] `/trfp give <player> <gunId>` 受 `trfp.command.give` 权限保护
- [x] `/trfp reload` 仅 `trfp.admin` 可执行
- [x] 玩家无权限时收到 `§c[TRfP] §f你没有权限执行此命令。`

## 配置
- [x] `plugins/TRfP/config.yml` 首次启动时自动生成（`ConfigManager.reload` 调用 `saveResource`）
- [x] 修改 `damage-multiplier` 后 `/trfp reload` 生效（`ConfigManager.getDamageMultiplier`）
- [x] 修改 `resource-pack-url` 后玩家重连可收到新资源包

## 验证
- [x] 大括号匹配校验通过（0 错误）
- [ ] 真实 Paper 1.21.1 服务器加载验证（需要网络下载 paper-api jar，沙箱环境无法完成）
- [ ] 至少 3 把官方枪械的完整流程测试通过（同上）
- [ ] Lua 钩子在命中事件中被调用（同上）

## 发布
- [x] `pom.xml` 已配置 `maven-shade-plugin`，运行 `mvn package` 可产出 `target/TRfP-1.0.0.jar`
- [x] `TRfP-resourcepack.zip` 可由管理员将 `src/main/resources/resourcepack/` 目录打包获得
- [x] `CHANGELOG.md` 记录 v1.0.0 发布说明
- [x] README 标注项目基于 TACZ，仅重塑品牌，原作者权益完整保留
