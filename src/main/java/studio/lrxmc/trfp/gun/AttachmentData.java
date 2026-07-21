package studio.lrxmc.trfp.gun;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 附件数据：包含修饰倍率（damage / zoom / recoil / spread 等）。
 * 由 {@link studio.lrxmc.trfp.attachment.AttachmentRegistry} 加载。
 */
public class AttachmentData implements ConfigurationSerializable {

    private String id;
    private String displayName;
    private AttachmentSlotType slotType;

    // 修饰倍率，> 1 表示增强，< 1 表示削弱
    private double damageModifier = 1.0;
    private double headshotModifier = 1.0;
    private double zoomModifier = 1.0;     // 用于 HUD 缩放
    private double recoilModifier = 1.0;    // 越小越稳
    private double spreadModifier = 1.0;    // 越小越准
    private double aimFovModifier = 1.0;
    private double reloadTimeModifier = 1.0;
    private int additionalMagazineSize = 0;
    private String[] tags = new String[0];

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public AttachmentSlotType getSlotType() { return slotType; }
    public void setSlotType(AttachmentSlotType slotType) { this.slotType = slotType; }
    public double getDamageModifier() { return damageModifier; }
    public void setDamageModifier(double damageModifier) { this.damageModifier = damageModifier; }
    public double getHeadshotModifier() { return headshotModifier; }
    public void setHeadshotModifier(double headshotModifier) { this.headshotModifier = headshotModifier; }
    public double getZoomModifier() { return zoomModifier; }
    public void setZoomModifier(double zoomModifier) { this.zoomModifier = zoomModifier; }
    public double getRecoilModifier() { return recoilModifier; }
    public void setRecoilModifier(double recoilModifier) { this.recoilModifier = recoilModifier; }
    public double getSpreadModifier() { return spreadModifier; }
    public void setSpreadModifier(double spreadModifier) { this.spreadModifier = spreadModifier; }
    public double getAimFovModifier() { return aimFovModifier; }
    public void setAimFovModifier(double aimFovModifier) { this.aimFovModifier = aimFovModifier; }
    public double getReloadTimeModifier() { return reloadTimeModifier; }
    public void setReloadTimeModifier(double reloadTimeModifier) { this.reloadTimeModifier = reloadTimeModifier; }
    public int getAdditionalMagazineSize() { return additionalMagazineSize; }
    public void setAdditionalMagazineSize(int additionalMagazineSize) { this.additionalMagazineSize = additionalMagazineSize; }
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("displayName", displayName);
        m.put("slotType", slotType == null ? null : slotType.name());
        m.put("damageModifier", damageModifier);
        m.put("headshotModifier", headshotModifier);
        m.put("zoomModifier", zoomModifier);
        m.put("recoilModifier", recoilModifier);
        m.put("spreadModifier", spreadModifier);
        m.put("aimFovModifier", aimFovModifier);
        m.put("reloadTimeModifier", reloadTimeModifier);
        m.put("additionalMagazineSize", additionalMagazineSize);
        m.put("tags", tags);
        return m;
    }

    public static AttachmentData deserialize(Map<String, Object> map) {
        AttachmentData d = new AttachmentData();
        d.id = (String) map.get("id");
        d.displayName = (String) map.get("displayName");
        Object s = map.get("slotType");
        d.slotType = s == null ? null : AttachmentSlotType.valueOf(s.toString());
        d.damageModifier = asD(map.get("damageModifier"), 1.0);
        d.headshotModifier = asD(map.get("headshotModifier"), 1.0);
        d.zoomModifier = asD(map.get("zoomModifier"), 1.0);
        d.recoilModifier = asD(map.get("recoilModifier"), 1.0);
        d.spreadModifier = asD(map.get("spreadModifier"), 1.0);
        d.aimFovModifier = asD(map.get("aimFovModifier"), 1.0);
        d.reloadTimeModifier = asD(map.get("reloadTimeModifier"), 1.0);
        d.additionalMagazineSize = asI(map.get("additionalMagazineSize"), 0);
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
