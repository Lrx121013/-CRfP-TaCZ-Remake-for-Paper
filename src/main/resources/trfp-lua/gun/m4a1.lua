-- TRfP M4A1 钩子
-- 作者: Lrxmcstudio.工作室的Lrx
function on_shoot(player, gun_data, world)
    trfp.log("[TRfP] M4A1 on_shoot")
end
function on_hit(player, target, headshot, damage)
    if headshot then trfp.log("[TRfP] M4A1 爆头 x" .. tostring(damage)) end
end
