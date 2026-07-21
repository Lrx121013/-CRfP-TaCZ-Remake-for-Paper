# [TaCZ] REMAKE For Paper (TRfP)

> Paper 1.21.1 平台上的 TACZ 复刻插件，保留原版全部枪械与玩法。

## 项目信息

- **插件名**：`[TaCZ] REMAKE For Paper`（简称 `TRfP`）
- **作者**：`Lrxmcstudio.工作室的Lrx`
- **原项目**：[TACZ (MCModderAnchor)](https://github.com/MCModderAnchor/TACZ) — Timeless and Classics Guns Zero
- **目标平台**：Paper 1.21.1+（兼容 Spigot 1.21.1；Folia 可用）
- **开源协议**：代码 Apache License 2.0；资源 CC BY-NC-ND 4.0（沿用原 TACZ 协议）

## 致谢（必须保留）

本项目是 **TACZ (Timeless and Classics Guns Zero)** 的 Paper 平台移植版本，所有玩法与数据均基于原 mod 移植。
原项目地址：https://github.com/MCModderAnchor/TACZ
- 程序员：`286799714`、`TartaricAcid`、`F1zeiL`、`xjqsh`、`ClumsyAlien`
- 美工：`NekoCrane`、`Receke`、`Pos_2333`

我们仅对**品牌信息**（插件名、作者、玩家可辨识的文字列）进行重塑，以方便 Paper 服主和玩家使用；**玩法、枪械数据、机制名沿用原版**，因此原 TACZ 玩家可以无缝迁移到本插件。

## 安装

1. 下载对应 Paper 1.21.1 服务端。
2. 将本插件的 `jar` 放入 `plugins/`。
3. 启动服务器生成 `plugins/TRfP/config.yml`，将 `resource-pack-url` 指向你上传到 CDN 的资源包（首次启动会在 `plugins/TRfP/resourcepack/` 提取模板）。
4. 重启服务器。

## 命令

| 命令 | 说明 | 权限 |
|------|------|------|
| `/trfp version` | 显示插件版本与作者 | `trfp.use` |
| `/trfp reload` | 重载配置 / 枪械 / 附件 / 配方 / Lua | `trfp.admin` |
| `/trfp give <玩家> <gunId> [数量]` | 给予枪械 | `trfp.command.give` |
| `/trfp giveammo <玩家> <ammoId> <数量>` | 给予弹药 | `trfp.command.giveammo` |
| `/trfp attach <slot> <attachmentId>` | 给自己当前手持枪装附件 | `trfp.command.attach` |

## 玩法

- **右键** 持枪开火（左键近战；潜行时右键刺刀）。
- **副手持对应口径弹药 + F 键** 触发换弹。
- **潜行** 进入瞄准状态。
- **爆头**：命中头部触发倍率伤害。

## 自定义内容

将你自己的 `yml` 文件放入下列目录即可覆盖/新增数据：

```
plugins/TRfP/
  ├── guns/             # 枪械数据
  ├── attachments/      # 附件数据
  ├── ammo/             # 弹药数据
  ├── recipes/          # 自定义配方
  └── data/<uuid>.yml   # 玩家数据
```

## 协议

- 代码：[Apache License 2.0](LICENSE)
- 资源：[CC BY-NC-ND 4.0](https://creativecommons.org/licenses/by-nc-nd/4.0/)

**禁止商用**、**禁止衍生**美术资源。  
代码允许在保留 Apache 2.0 协议头的前提下进行二次开发与分发。
