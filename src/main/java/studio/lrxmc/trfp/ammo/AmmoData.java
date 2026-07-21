package studio.lrxmc.trfp.ammo;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 弹药数据。
 * <p>
 * 字段名沿用原 TACZ。
 */
public class AmmoData implements ConfigurationSerializable {

    private String id;
    private String displayName;
    private int maxStackSize = 64;
    private String caliber;  // 弹种（与枪械的 ammoId 对应）
    private double damageBonus = 1.0;
    private String[] tags = new String[0];

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public int getMaxStackSize() { return maxStackSize; }
    public void setMaxStackSize(int maxStackSize) { this.maxStackSize = maxStackSize; }
    public String getCaliber() { return caliber; }
    public void setCaliber(String caliber) { this.caliber = caliber; }
    public double getDamageBonus() { return damageBonus; }
    public void setDamageBonus(double damageBonus) { this.damageBonus = damageBonus; }
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("displayName", displayName);
        m.put("maxStackSize", maxStackSize);
        m.put("caliber", caliber);
        m.put("damageBonus", damageBonus);
        m.put("tags", tags);
        return m;
    }

    public static AmmoData deserialize(Map<String, Object> map) {
        AmmoData d = new AmmoData();
        d.id = (String) map.get("id");
        d.displayName = (String) map.get("displayName");
        d.maxStackSize = asI(map.get("maxStackSize"), 64);
        d.caliber = (String) map.get("caliber");
        d.damageBonus = asD(map.get("damageBonus"), 1.0);
        Object t = map.get("tags");
        if (t instanceof java.util.List<?> list) {
            java.util.List<String> tagList = new java.util.ArrayList<>();
            for (Object o : list) if (o != null) tagList.add(o.toString());
            d.tags = tagList.toArray(new String[0]);
        }
        return d;
    }

    private static double asD(Object o, double def) {
        return o instanceof Number n ? n.doubleValue() : def;
    }

    private static int asI(Object o, int def) {
        return o instanceof Number n ? n.intValue() : def;
    }
}
