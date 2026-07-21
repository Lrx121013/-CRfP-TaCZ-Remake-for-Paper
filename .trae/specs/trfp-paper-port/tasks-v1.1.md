# Tasks (扩展 v1.1)

> 在 v1.0 基础上：从原 TACZ 仓库拉取完整 assets（textures/models/sounds）、重命名命名空间 tacz → trfp、改用 Apache 2.0 license、编译 jar、推 GitHub release。

## Task 15: 拉取原 TACZ 完整 assets
- [ ] SubTask 15.1: 克隆 https://github.com/MCModderAnchor/TACZ.git（branch 1.20.1）
- [ ] SubTask 15.2: 将 `src/main/resources/assets/tacz/textures/` 复制为 `resourcepack/assets/trfp/textures/`
- [ ] SubTask 15.3: 将 `src/main/resources/assets/tacz/models/` 复制为 `resourcepack/assets/trfp/models/`
- [ ] SubTask 15.4: 将 `src/main/resources/assets/tacz/sounds/` 复制为 `resourcepack/assets/trfp/sounds/`
- [ ] SubTask 15.5: 将 `src/main/resources/assets/tacz/lang/` 中的所有 key 重命名 `tacz.` → `trfp.` 后合并入 `resourcepack/assets/trfp/lang/`
- [ ] SubTask 15.6: 把 `sounds.json` 与所有模型 JSON 中的 `tacz:` 命名空间替换为 `trfp:`
- [ ] SubTask 15.7: 把 `data/tacz/` recipes / loot_tables 等数据迁移到 `data/trfp/`（命名空间重写）

## Task 16: 替换 LICENSE 为 Apache 2.0
- [ ] SubTask 16.1: 用 Apache 2.0 完整文本覆盖 `/workspace/LICENSE`
- [ ] SubTask 16.2: 更新 `README.md` 协议声明为 Apache 2.0（保留原 TACZ 资源 CC BY-NC-ND 4.0 的署名说明）
- [ ] SubTask 16.3: 更新 `plugin.yml` 中无冲突字段
- [ ] SubTask 16.4: 更新 `pom.xml` 的 `licenses` 节为 Apache 2.0

## Task 17: 重新编译并产出 jar
- [ ] SubTask 17.1: 修复编译错误（如有）
- [ ] SubTask 17.2: `mvn clean package` 产出 `target/TRfP-1.0.0.jar`
- [ ] SubTask 17.3: 打包 `TRfP-resourcepack.zip`（`resourcepack/` 目录）

## Task 18: 推送到 GitHub + release
- [ ] SubTask 18.1: 用提供的 token 创建 GitHub 仓库 `Lrxmcstudio/TRfP`（若已存在则推送）
- [ ] SubTask 18.2: 提交所有源代码 + 资源 + jar + resourcepack.zip
- [ ] SubTask 18.3: 创建 release v1.0.0，附 jar 与 resourcepack.zip 作为资产

# Task Dependencies
- Task 15 → Task 16, 17（先有完整资源才能编译）
- Task 17 → Task 18（先有 jar 才能 release）
