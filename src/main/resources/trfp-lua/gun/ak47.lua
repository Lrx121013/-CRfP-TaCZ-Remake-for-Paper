-- TRfP AK-47 钩子脚本
-- 作者: Lrxmcstudio.工作室的Lrx
-- 移植自原 TACZ ak47.lua，命名空间 tacz -> trfp
-- 钩子：on_shoot, on_reload, on_hit, on_attach

function on_shoot(player, gun_data, world)
    -- 每 3 发提醒弹匣余量
    -- 可在此处实现：额外伤害 / 弹道偏移 / 灼烧 / 击退 等
    trfp.log("[TRfP] AK-47 on_shoot from " .. tostring(player))
end

function on_reload(player, gun_data)
    trfp.log("[TRfP] AK-47 正在换弹")
end

function on_hit(player, target, headshot, damage)
    if headshot then
        trfp.log("[TRfP] 爆头！")
    end
end

function on_attach(player, slot, attachment_id)
    trfp.log("[TRfP] AK-47 安装 " .. slot .. " -> " .. attachment_id)
end
