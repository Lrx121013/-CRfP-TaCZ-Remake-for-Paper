package studio.lrxmc.trfp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.combat.BulletRayTrace;
import studio.lrxmc.trfp.gun.FireMode;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemNBT;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 战斗监听：负责右键开火、左键近战 / 切副手、潜行瞄准触发。
 */
public class CombatListener implements Listener {

    private final TRfPPlugin plugin;
    private final Map<UUID, Long> lastShoot = new HashMap<>();
    private final Map<UUID, Integer> burstShots = new HashMap<>();

    public CombatListener(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack hand = event.getItem();
        if (hand == null || !GunItemNBT.isGunItem(hand)) return;
        GunData data = plugin.getGunRegistry().get(GunItemNBT.getGunId(hand));
        if (data == null) return;
        Action a = event.getAction();
        // 右键开火
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            if (p.isSneaking() && data.isHasBayonet()) {
                doBayonet(p, data);
            } else {
                tryShoot(p, hand, data);
            }
        } else if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
            // 左键近战
            if (data.isHasMelee()) {
                event.setCancelled(true);
                doMelee(p, data);
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        // 切枪时重置 burst 计数
        burstShots.remove(event.getPlayer().getUniqueId());
    }

    private void tryShoot(Player p, ItemStack hand, GunData data) {
        // 弹药检查
        int ammo = GunItemNBT.getAmmo(hand);
        if (ammo <= 0) {
            // dry fire 提示
            p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5f, 1.6f);
            p.sendActionBar(Component.text("弹药耗尽 — 按 R 换弹").color(NamedTextColor.RED));
            return;
        }
        // 射速检查
        long now = System.currentTimeMillis();
        long interval = Math.max(20, 60_000L / Math.max(1, data.getFireRate()));
        Long last = lastShoot.get(p.getUniqueId());
        if (last != null && now - last < interval) return;

        // FireMode 状态
        FireMode mode = GunItemNBT.getFireMode(hand);
        if (mode == FireMode.BURST) {
            int shot = burstShots.getOrDefault(p.getUniqueId(), 0);
            if (shot >= 3) {
                burstShots.put(p.getUniqueId(), 0);
                return;
            }
            burstShots.put(p.getUniqueId(), shot + 1);
        } else if (mode == FireMode.SEMI) {
            // 半自动：玩家松开右键后才能再次发射；这里通过 lastShoot 自然节流
        }

        // 发射
        doShoot(p, hand, data);
        lastShoot.put(p.getUniqueId(), now);
    }

    private void doShoot(Player p, ItemStack hand, GunData data) {
        GunItemNBT.setAmmo(hand, GunItemNBT.getAmmo(hand) - 1);
        // 后坐力
        double recoilUp = data.getRecoilUp();
        double recoilHor = data.getRecoilHorizontal();
        if (recoilUp != 0 || recoilHor != 0) {
            Vector v = p.getLocation().getDirection();
            v.rotateAroundX(Math.toRadians(-recoilUp));
            v.rotateAroundY(Math.toRadians((Math.random() - 0.5) * recoilHor));
            p.teleport(p.getLocation().setDirection(v));
        }
        // 播放音效
        if (data.getShootSound() != null) {
            try {
                p.playSound(p.getLocation(), data.getShootSound(), 1.0f, 1.0f);
            } catch (Exception e) {
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 2.0f);
            }
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.2f, 2.0f);
        }
        // 粒子
        Location eye = p.getEyeLocation();
        p.getWorld().spawnParticle(Particle.FLAME, eye, 4, 0.05, 0.05, 0.05, 0.02);
        // 弹道
        for (int i = 0; i < Math.max(1, data.getBulletsPerShot()); i++) {
            Vector dir = p.getLocation().getDirection();
            if (data.getSpread() > 0) dir = BulletRayTrace.applySpread(dir, data.getSpread());
            BulletRayTrace.HitResult hit = BulletRayTrace.trace(p, eye, dir);
            if (hit != null) {
                double dmg = BulletRayTrace.calculateDamage(p, data, hit, GunItemNBT.getAttachments(hand));
                BulletRayTrace.applyDamage(p, hit.entity, dmg, hit.headshot);
            } else {
                // 击中方块
                Vector step = dir.clone().multiply(2);
                p.getWorld().spawnParticle(Particle.BLOCK, eye.clone().add(step), 4, 0.1, 0.1, 0.1, 0.01,
                        org.bukkit.Material.STONE.createBlockData());
            }
        }
        // 弹药提示
        p.sendActionBar(Component.text("弹药: " + GunItemNBT.getAmmo(hand) + "/" + data.getMagazineSize()).color(NamedTextColor.YELLOW));
    }

    private void doMelee(Player p, GunData data) {
        p.swingMainHand();
        p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.2f);
        var ents = p.getNearbyEntities(2.0, 2.0, 2.0);
        for (var e : ents) {
            if (e instanceof org.bukkit.entity.LivingEntity le && !le.getUniqueId().equals(p.getUniqueId())) {
                BulletRayTrace.applyDamage(p, le, data.getMeleeDamage(), false);
            }
        }
    }

    private void doBayonet(Player p, GunData data) {
        p.swingMainHand();
        p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0f, 0.8f);
        var ents = p.getNearbyEntities(3.0, 3.0, 3.0);
        for (var e : ents) {
            if (e instanceof org.bukkit.entity.LivingEntity le && !le.getUniqueId().equals(p.getUniqueId())) {
                BulletRayTrace.applyDamage(p, le, data.getBayonetDamage(), false);
            }
        }
        p.sendActionBar(Component.text("刺刀突刺！").color(NamedTextColor.GOLD));
    }
}
