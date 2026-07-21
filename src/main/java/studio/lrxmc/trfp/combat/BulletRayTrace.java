package studio.lrxmc.trfp.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.AttachmentData;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemNBT;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 子弹射线检测与伤害计算。
 * <p>
 * 步进长度 0.05，命中顺序：先 block，再 living entity。
 */
public final class BulletRayTrace {

    private static final double STEP = 0.05;
    private static final double MAX_DISTANCE = 256.0;

    private BulletRayTrace() {}

    public static class HitResult {
        public final LivingEntity entity;
        public final boolean headshot;
        public final double distance;
        public HitResult(LivingEntity entity, boolean headshot, double distance) {
            this.entity = entity;
            this.headshot = headshot;
            this.distance = distance;
        }
    }

    public static HitResult trace(Player shooter, Location origin, Vector direction) {
        Vector dir = direction.clone().normalize();
        double traveled = 0;
        Location cur = origin.clone();
        while (traveled < MAX_DISTANCE) {
            cur.add(dir.clone().multiply(STEP));
            traveled += STEP;
            // 命中方块
            if (!cur.getBlock().isPassable()) {
                // 命中非穿透方块
                return null;
            }
            // 命中生物
            for (var e : cur.getWorld().getNearbyEntities(cur, 0.6, 0.6, 0.6, ent -> ent instanceof LivingEntity le && !ent.getUniqueId().equals(shooter.getUniqueId()))) {
                if (e instanceof LivingEntity le) {
                    boolean head = isHeadshot(le, cur);
                    return new HitResult(le, head, traveled);
                }
            }
        }
        return null;
    }

    private static boolean isHeadshot(LivingEntity le, Location hitLoc) {
        double eyeY = le.getEyeLocation().getY();
        double headTop = le.getHeight() + le.getLocation().getY() - 0.2;
        if (le instanceof Player p) {
            headTop = p.getEyeLocation().getY() + 0.3;
        }
        return hitLoc.getY() >= eyeY - 0.15;
    }

    /**
     * 计算一次命中的最终伤害（应用距离衰减、爆头倍率、附件、config 倍率）。
     */
    public static double calculateDamage(Player shooter, GunData gun, HitResult hit, Map<String, String> attachments) {
        TRfPPlugin plugin = TRfPPlugin.getInstance();
        double base = gun.getDamage();
        if (gun.getDamageNear() > 0 && gun.getDamageFar() > 0 && gun.getDamageFar() > gun.getDamageNear()) {
            // 简单线性插值
            double t = Math.min(1.0, hit.distance / 64.0);
            base = gun.getDamageNear() + (gun.getDamageFar() - gun.getDamageNear()) * t;
        }
        // 附件修正
        if (attachments != null) {
            for (String aid : attachments.values()) {
                AttachmentData a = plugin.getAttachmentRegistry() == null ? null : null; // 由外部注入
                if (a != null) base *= a.getDamageModifier();
            }
        }
        if (hit.headshot) {
            base *= gun.getHeadshotMultiplier() * (plugin.getConfigManager().getHeadshotMultiplier());
        }
        base *= plugin.getConfigManager().getDamageMultiplier();
        // 距离惩罚
        base *= Math.max(0.2, 1.0 - (hit.distance / 80.0) * 0.6);
        return Math.max(0.1, base);
    }

    public static void applyDamage(Player shooter, LivingEntity target, double damage, boolean headshot) {
        // 取消 Paper 自带的护甲公式（v1.21.1 已无 vanilla attribute damage），直接传 EntityDamageEvent
        EntityDamageEvent event = new EntityDamageEvent(target, EntityDamageEvent.DamageCause.PROJECTILE, damage);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        double finalDmg = event.getFinalDamage();
        target.setLastDamageCause(event);
        target.damage(finalDmg, shooter);
        target.setNoDamageTicks(0);
        if (headshot) {
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.7f, 1.6f);
        } else {
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 1.0f);
        }
        // 击退
        Vector kb = shooter.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.2);
        target.setVelocity(target.getVelocity().add(kb));
        // 命中粒子
        target.getWorld().spawnParticle(Particle.BLOCK, target.getEyeLocation(), 6, 0.2, 0.2, 0.2, 0.01, org.bukkit.Material.REDSTONE_BLOCK.createBlockData());
    }

    public static Vector applySpread(Vector base, double spreadDeg) {
        double r = ThreadLocalRandom.current().nextDouble() * spreadDeg;
        double a = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;
        Vector v = base.clone();
        v.rotateAroundAxis(getPerpendicular(v), Math.toRadians(r));
        v.rotateAroundAxis(v, a);
        return v;
    }

    private static Vector getPerpendicular(Vector v) {
        Vector up = new Vector(0, 1, 0);
        Vector perp = v.clone().crossProduct(up);
        if (perp.lengthSquared() < 1e-6) perp = v.clone().crossProduct(new Vector(1, 0, 0));
        return perp.normalize();
    }
}
