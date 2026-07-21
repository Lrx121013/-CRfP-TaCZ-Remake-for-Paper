package studio.lrxmc.trfp.gun;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单把枪的全部数据定义（与原 TACZ gun json schema 对齐）。
 * <p>
 * 字段命名沿用原 mod 以方便 Lua 钩子与文档互通。
 *
 * @author Lrxmcstudio.工作室的Lrx
 */
public class GunData implements ConfigurationSerializable {

    private String id;
    private String displayName;
    private FireMode defaultFireMode;
    private double damage;
    private double damageNear;
    private double damageFar;
    private double headshotMultiplier;
    private int magazineSize;
    private String ammoId;
    private double reloadTimeTicks;
    private int fireRate;       // RPM
    private int bulletsPerShot; // 霰弹数量
    private double spread;      // 弹道散布
    private double recoilUp;
    private double recoilHorizontal;
    private double recoilDurationTicks;
    private double aimFovMultiplier;
    private List<AttachmentSlotType> attachmentSlots = new ArrayList<>();
    private boolean hasBolt;        // 拉栓枪
    private boolean hasMelee;       // 近战
    private boolean hasBayonet;     // 刺刀
    private int meleeDamage;
    private int bayonetDamage;
    private String meleeSound;
    private String shootSound;
    private String reloadSound;
    private String luaScript;       // gun/&lt;id&gt;.lua
    private int level;
    private String[] tags = new String[0];

    public GunData() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public FireMode getDefaultFireMode() { return defaultFireMode; }
    public void setDefaultFireMode(FireMode defaultFireMode) { this.defaultFireMode = defaultFireMode; }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }

    public double getDamageNear() { return damageNear; }
    public void setDamageNear(double damageNear) { this.damageNear = damageNear; }

    public double getDamageFar() { return damageFar; }
    public void setDamageFar(double damageFar) { this.damageFar = damageFar; }

    public double getHeadshotMultiplier() { return headshotMultiplier; }
    public void setHeadshotMultiplier(double headshotMultiplier) { this.headshotMultiplier = headshotMultiplier; }

    public int getMagazineSize() { return magazineSize; }
    public void setMagazineSize(int magazineSize) { this.magazineSize = magazineSize; }

    public String getAmmoId() { return ammoId; }
    public void setAmmoId(String ammoId) { this.ammoId = ammoId; }

    public double getReloadTimeTicks() { return reloadTimeTicks; }
    public void setReloadTimeTicks(double reloadTimeTicks) { this.reloadTimeTicks = reloadTimeTicks; }

    public int getFireRate() { return fireRate; }
    public void setFireRate(int fireRate) { this.fireRate = fireRate; }

    public int getBulletsPerShot() { return bulletsPerShot; }
    public void setBulletsPerShot(int bulletsPerShot) { this.bulletsPerShot = bulletsPerShot; }

    public double getSpread() { return spread; }
    public void setSpread(double spread) { this.spread = spread; }

    public double getRecoilUp() { return recoilUp; }
    public void setRecoilUp(double recoilUp) { this.recoilUp = recoilUp; }

    public double getRecoilHorizontal() { return recoilHorizontal; }
    public void setRecoilHorizontal(double recoilHorizontal) { this.recoilHorizontal = recoilHorizontal; }

    public double getRecoilDurationTicks() { return recoilDurationTicks; }
    public void setRecoilDurationTicks(double recoilDurationTicks) { this.recoilDurationTicks = recoilDurationTicks; }

    public double getAimFovMultiplier() { return aimFovMultiplier; }
    public void setAimFovMultiplier(double aimFovMultiplier) { this.aimFovMultiplier = aimFovMultiplier; }

    public List<AttachmentSlotType> getAttachmentSlots() { return attachmentSlots; }
    public void setAttachmentSlots(List<AttachmentSlotType> attachmentSlots) { this.attachmentSlots = attachmentSlots; }

    public boolean isHasBolt() { return hasBolt; }
    public void setHasBolt(boolean hasBolt) { this.hasBolt = hasBolt; }

    public boolean isHasMelee() { return hasMelee; }
    public void setHasMelee(boolean hasMelee) { this.hasMelee = hasMelee; }

    public boolean isHasBayonet() { return hasBayonet; }
    public void setHasBayonet(boolean hasBayonet) { this.hasBayonet = hasBayonet; }

    public int getMeleeDamage() { return meleeDamage; }
    public void setMeleeDamage(int meleeDamage) { this.meleeDamage = meleeDamage; }

    public int getBayonetDamage() { return bayonetDamage; }
    public void setBayonetDamage(int bayonetDamage) { this.bayonetDamage = bayonetDamage; }

    public String getMeleeSound() { return meleeSound; }
    public void setMeleeSound(String meleeSound) { this.meleeSound = meleeSound; }

    public String getShootSound() { return shootSound; }
    public void setShootSound(String shootSound) { this.shootSound = shootSound; }

    public String getReloadSound() { return reloadSound; }
    public void setReloadSound(String reloadSound) { this.reloadSound = reloadSound; }

    public String getLuaScript() { return luaScript; }
    public void setLuaScript(String luaScript) { this.luaScript = luaScript; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("displayName", displayName);
        map.put("defaultFireMode", defaultFireMode == null ? null : defaultFireMode.name());
        map.put("damage", damage);
        map.put("damageNear", damageNear);
        map.put("damageFar", damageFar);
        map.put("headshotMultiplier", headshotMultiplier);
        map.put("magazineSize", magazineSize);
        map.put("ammoId", ammoId);
        map.put("reloadTimeTicks", reloadTimeTicks);
        map.put("fireRate", fireRate);
        map.put("bulletsPerShot", bulletsPerShot);
        map.put("spread", spread);
        map.put("recoilUp", recoilUp);
        map.put("recoilHorizontal", recoilHorizontal);
        map.put("recoilDurationTicks", recoilDurationTicks);
        map.put("aimFovMultiplier", aimFovMultiplier);
        List<String> slotStrs = new ArrayList<>();
        if (attachmentSlots != null) {
            for (AttachmentSlotType t : attachmentSlots) slotStrs.add(t.name());
        }
        map.put("attachmentSlots", slotStrs);
        map.put("hasBolt", hasBolt);
        map.put("hasMelee", hasMelee);
        map.put("hasBayonet", hasBayonet);
        map.put("meleeDamage", meleeDamage);
        map.put("bayonetDamage", bayonetDamage);
        map.put("meleeSound", meleeSound);
        map.put("shootSound", shootSound);
        map.put("reloadSound", reloadSound);
        map.put("luaScript", luaScript);
        map.put("level", level);
        map.put("tags", tags);
        return map;
    }

    public static GunData deserialize(Map<String, Object> map) {
        GunData data = new GunData();
        data.id = (String) map.get("id");
        data.displayName = (String) map.get("displayName");
        String mode = (String) map.get("defaultFireMode");
        data.defaultFireMode = mode == null ? FireMode.SEMI : FireMode.valueOf(mode);
        data.damage = asDouble(map.get("damage"), 1.0);
        data.damageNear = asDouble(map.get("damageNear"), data.damage);
        data.damageFar = asDouble(map.get("damageFar"), data.damage);
        data.headshotMultiplier = asDouble(map.get("headshotMultiplier"), 1.5);
        data.magazineSize = asInt(map.get("magazineSize"), 30);
        data.ammoId = (String) map.get("ammoId");
        data.reloadTimeTicks = asDouble(map.get("reloadTimeTicks"), 40.0);
        data.fireRate = asInt(map.get("fireRate"), 600);
        data.bulletsPerShot = asInt(map.get("bulletsPerShot"), 1);
        data.spread = asDouble(map.get("spread"), 0.5);
        data.recoilUp = asDouble(map.get("recoilUp"), 0.0);
        data.recoilHorizontal = asDouble(map.get("recoilHorizontal"), 0.0);
        data.recoilDurationTicks = asDouble(map.get("recoilDurationTicks"), 8.0);
        data.aimFovMultiplier = asDouble(map.get("aimFovMultiplier"), 0.6);
        Object slotObj = map.get("attachmentSlots");
        data.attachmentSlots = new ArrayList<>();
        if (slotObj instanceof List<?> list) {
            for (Object o : list) {
                if (o != null) data.attachmentSlots.add(AttachmentSlotType.valueOf(o.toString()));
            }
        }
        data.hasBolt = asBool(map.get("hasBolt"), false);
        data.hasMelee = asBool(map.get("hasMelee"), true);
        data.hasBayonet = asBool(map.get("hasBayonet"), false);
        data.meleeDamage = asInt(map.get("meleeDamage"), 4);
        data.bayonetDamage = asInt(map.get("bayonetDamage"), 7);
        data.meleeSound = (String) map.get("meleeSound");
        data.shootSound = (String) map.get("shootSound");
        data.reloadSound = (String) map.get("reloadSound");
        data.luaScript = (String) map.get("luaScript");
        data.level = asInt(map.get("level"), 1);
        Object tagsObj = map.get("tags");
        if (tagsObj instanceof List<?> list) {
            List<String> tagList = new ArrayList<>();
            for (Object o : list) if (o != null) tagList.add(o.toString());
            data.tags = tagList.toArray(new String[0]);
        }
        return data;
    }

    private static double asDouble(Object o, double def) {
        if (o instanceof Number n) return n.doubleValue();
        return def;
    }

    private static int asInt(Object o, int def) {
        if (o instanceof Number n) return n.intValue();
        return def;
    }

    private static boolean asBool(Object o, boolean def) {
        if (o instanceof Boolean b) return b;
        return def;
    }
}
