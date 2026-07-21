# Checklist

## 项目骨架
- [ ] `plugin.yml` 中 `name` 为 `[TaCZ]REMAKEForPaper`，`author` 为 `[Lrxmcstudio.工作室的Lrx]`
- [ ] `plugin.yml` 中 `description` 包含 "TRfP" 与 "Lrxmcstudio.工作室的Lrx" 字样
- [ ] 启动日志输出 `[TRfP] [TaCZ] REMAKE For Paper v1.0.0 — 作者 Lrxmcstudio.工作室的Lrx`
- [ ] `LICENSE` 保留 GPL-3.0（代码）与 CC BY-NC-ND 4.0（资源）双重声明
- [ ] 原 TACZ 作者（286799714 / TartaricAcid / F1zeiL / xjqsh / ClumsyAlien / NekoCrane / Receke / Pos_2333）在 README 中保留署名

## 核心数据
- [ ] 全部官方 `GunId` 在 `GunId` 注册表中存在
- [ ] `GunData` 字段与原 mod JSON schema 一致
- [ ] 玩家退出后 `data/<uuid>.yml` 包含全部 `GunId` / `Attachments` / `Level` / `Skin`
- [ ] 玩家重新登录后 NBT 与下线前一致

## 资源与语言
- [ ] `assets/trfp/textures/`、`models/`、`sounds/` 从原 mod 完整迁移
- [ ] `lang/zh_cn.json` 与 `lang/en_us.json` 中所有品牌字符串改为 TRfP / Lrxmcstudio.工作室的Lrx
- [ ] 资源包命名空间全部为 `trfp`
- [ ] 玩家首次进入服务器自动收到 `TRfP-resourcepack.zip`

## 枪械物品
- [ ] `/trfp give <player> <gunId>` 可正常给予并保留 NBT
- [ ] 物品冷却与原 mod `useDuration` 一致
- [ ] 副手持枪、潜行瞄准事件可被正确识别

## 战斗系统
- [ ] 半自动 / 全自动 / 三连发 FireMode 行为正确
- [ ] 射线检测命中生物时调用 `DamageCalculator`
- [ ] 爆头倍率 / 距离衰减 / 护甲穿透按 `GunData` 应用
- [ ] 命中事件抛出 `GunShootEvent` / `GunHitEntityEvent` / `GunHitBlockEvent`
- [ ] 击退、灼烧、凋零、暴击等效果按原 mod 数据应用

## 换弹 / 瞄准 / 近战
- [ ] R 键触发换弹，计时与原 mod `ReloadTime` 一致
- [ ] 弹药来源按 `MagazineType` 优先级尝试：副手 > 背包 > 弹药盒
- [ ] 潜行瞄准时 FOV 插值
- [ ] 左键近战 / 刺刀按 `MeleeData` 触发

## 附件
- [ ] 4 类附件物品可注册
- [ ] 附件属性覆盖 `GunData` 字段（damage / recoil / zoom 等）
- [ ] 安装 / 拆卸附件通过 NBT 持久化

## HUD
- [ ] 准星 / 弹药 / 持枪 overlay 通过 Resource Pack 显示
- [ ] 瞄准时按 Scope 类型切换准星
- [ ] 玩家位置上的 ItemDisplay / ArmorStand 持枪姿态与玩家朝向同步

## Lua
- [ ] Lua 引擎可加载 `TRfP-lua/gun/<GunId>.lua`
- [ ] 开火、命中、换弹、附件变更点能调用对应钩子
- [ ] Lua 脚本可读取 `trfp` 全局命名空间

## 配方
- [ ] 全部原 mod 配方注册成功
- [ ] 工作台 / 锻造台 / 熔炉 / 烟熏炉 / 高炉配方按预期输出

## 指令与权限
- [ ] `/trfp version` 输出正确版本与作者
- [ ] `/trfp give <player> <gunId>` 受 `trfp.command.give` 权限保护
- [ ] `/trfp reload` 仅 `trfp.admin` 可执行
- [ ] 玩家无权限时收到 `§c[TRfP] §f你没有权限执行此命令。`

## 配置
- [ ] `plugins/TRfP/config.yml` 首次启动时自动生成
- [ ] 修改 `damage-multiplier` 后 `/trfp reload` 生效
- [ ] 修改 `resource-pack-url` 后玩家重连可收到新资源包

## 验证
- [ ] 在 Paper 1.21.1 上成功加载插件
- [ ] 玩家登录、获得枪械、开火、换弹、安装附件、退出、再登录 数据保持
- [ ] 至少 3 把官方枪械的完整流程测试通过
- [ ] Lua 钩子在命中事件中被调用

## 发布
- [ ] `target/TRfP-1.0.0.jar` 可被 Paper 加载
- [ ] `TRfP-resourcepack.zip` 可被客户端加载
- [ ] `CHANGELOG.md` 记录 v1.0.0 发布说明
- [ ] README 标注项目基于 TACZ，仅重塑品牌，原作者权益完整保留
