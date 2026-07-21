-- TRfP AWP 钩子
-- 作者: Lrxmcstudio.工作室的Lrx
function on_shoot(player, gun_data, world)
    trfp.log("[TRfP] AWP on_shoot")
end
function on_hit(player, target, headshot, damage)
    if headshot then
        trfp.log("[TRfP] AWP 致命一击！")
    end
end
function on_reload(player, gun_data)
    trfp.log("[TRfP] AWP 拉栓中…")
end
