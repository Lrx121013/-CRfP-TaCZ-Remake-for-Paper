-- TRfP M2HB 重机枪钩子
-- 作者: Lrxmcstudio.工作室的Lrx
function on_shoot(player, gun_data, world)
    trfp.log("[TRfP] M2HB on_shoot")
end
function on_hit(player, target, headshot, damage)
    trfp.log("[TRfP] M2HB hit " .. tostring(damage))
end
