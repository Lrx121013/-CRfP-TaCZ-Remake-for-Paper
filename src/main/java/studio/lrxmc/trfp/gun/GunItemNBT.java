package studio.lrxmc.trfp.gun;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 枪械 NBT 工具：在 ItemStack 的 PersistentDataContainer 上读写枪械状态。
 * <p>
 * 字段：
 * <ul>
 *   <li>{@code trfp:gun_id}（STRING）—— 枪械 ID</li>
 *   <li>{@code trfp:fire_mode}（STRING）—— 当前 FireMode</li>
 *   <li>{@code trfp:ammo}（INTEGER）—— 弹匣内弹药</li>
 *   <li>{@code trfp:ammo_dummy}（INTEGER）—— 额外弹药（弹匣外）</li>
 *   <li>{@code trfp:level}（INTEGER）—— 等级/经验</li>
 *   <li>{@code trfp:skin}（STRING）—— 皮肤变体</li>
 *   <li>{@code trfp:attachments}（STRING）—— JSON 编码的 attachment 槽位</li>
 * </ul>
 */
public final class GunItemNBT {

    public static final NamespacedKey KEY_GUN_ID = new NamespacedKey("trfp", "gun_id");
    public static final NamespacedKey KEY_FIRE_MODE = new NamespacedKey("trfp", "fire_mode");
    public static final NamespacedKey KEY_AMMO = new NamespacedKey("trfp", "ammo");
    public static final NamespacedKey KEY_AMMO_DUMMY = new NamespacedKey("trfp", "ammo_dummy");
    public static final NamespacedKey KEY_LEVEL = new NamespacedKey("trfp", "level");
    public static final NamespacedKey KEY_SKIN = new NamespacedKey("trfp", "skin");
    public static final NamespacedKey KEY_ATTACHMENTS = new NamespacedKey("trfp", "attachments");

    private GunItemNBT() {}

    public static boolean isGunItem(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return false;
        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(KEY_GUN_ID, PersistentDataType.STRING);
    }

    @Nullable
    public static String getGunId(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return null;
        return stack.getItemMeta().getPersistentDataContainer().get(KEY_GUN_ID, PersistentDataType.STRING);
    }

    public static void setGunId(@NotNull ItemStack stack, @NotNull String gunId) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_GUN_ID, PersistentDataType.STRING, gunId.toLowerCase(Locale.ROOT));
        stack.setItemMeta(meta);
    }

    public static FireMode getFireMode(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return FireMode.SEMI;
        String s = stack.getItemMeta().getPersistentDataContainer().get(KEY_FIRE_MODE, PersistentDataType.STRING);
        if (s == null) return FireMode.SEMI;
        try { return FireMode.valueOf(s); } catch (Exception e) { return FireMode.SEMI; }
    }

    public static void setFireMode(@NotNull ItemStack stack, @NotNull FireMode mode) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_FIRE_MODE, PersistentDataType.STRING, mode.name());
        stack.setItemMeta(meta);
    }

    public static int getAmmo(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return 0;
        Integer v = stack.getItemMeta().getPersistentDataContainer().get(KEY_AMMO, PersistentDataType.INTEGER);
        return v == null ? 0 : v;
    }

    public static void setAmmo(@NotNull ItemStack stack, int amount) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_AMMO, PersistentDataType.INTEGER, Math.max(0, amount));
        stack.setItemMeta(meta);
    }

    public static int getAmmoDummy(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return 0;
        Integer v = stack.getItemMeta().getPersistentDataContainer().get(KEY_AMMO_DUMMY, PersistentDataType.INTEGER);
        return v == null ? 0 : v;
    }

    public static void setAmmoDummy(@NotNull ItemStack stack, int amount) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_AMMO_DUMMY, PersistentDataType.INTEGER, Math.max(0, amount));
        stack.setItemMeta(meta);
    }

    public static int getLevel(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return 1;
        Integer v = stack.getItemMeta().getPersistentDataContainer().get(KEY_LEVEL, PersistentDataType.INTEGER);
        return v == null ? 1 : v;
    }

    public static void setLevel(@NotNull ItemStack stack, int level) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_LEVEL, PersistentDataType.INTEGER, Math.max(0, level));
        stack.setItemMeta(meta);
    }

    @Nullable
    public static String getSkin(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return null;
        return stack.getItemMeta().getPersistentDataContainer().get(KEY_SKIN, PersistentDataType.STRING);
    }

    public static void setSkin(@NotNull ItemStack stack, @Nullable String skin) {
        ItemMeta meta = stack.getItemMeta();
        if (skin == null) {
            meta.getPersistentDataContainer().remove(KEY_SKIN);
        } else {
            meta.getPersistentDataContainer().set(KEY_SKIN, PersistentDataType.STRING, skin);
        }
        stack.setItemMeta(meta);
    }

    /**
     * 读取所有附件槽（attachment slot id -> attachment id）。
     * 简易 JSON 解析（避免引第三方库）。
     */
    @NotNull
    public static Map<String, String> getAttachments(@NotNull ItemStack stack) {
        if (!stack.hasItemMeta()) return new HashMap<>();
        String raw = stack.getItemMeta().getPersistentDataContainer().get(KEY_ATTACHMENTS, PersistentDataType.STRING);
        if (raw == null || raw.isEmpty()) return new HashMap<>();
        Map<String, String> map = new HashMap<>();
        // 格式：SCOPE:scope_id;GRIP:grip_id
        for (String pair : raw.split(";")) {
            if (pair.isBlank()) continue;
            int idx = pair.indexOf(':');
            if (idx <= 0) continue;
            map.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return map;
    }

    public static void setAttachment(@NotNull ItemStack stack, @NotNull String slot, @Nullable String attachmentId) {
        Map<String, String> map = getAttachments(stack);
        if (attachmentId == null) {
            map.remove(slot);
        } else {
            map.put(slot, attachmentId);
        }
        saveAttachments(stack, map);
    }

    public static void saveAttachments(@NotNull ItemStack stack, @NotNull Map<String, String> map) {
        ItemMeta meta = stack.getItemMeta();
        if (map.isEmpty()) {
            meta.getPersistentDataContainer().remove(KEY_ATTACHMENTS);
        } else {
            StringBuilder sb = new StringBuilder();
            for (var e : map.entrySet()) {
                if (sb.length() > 0) sb.append(';');
                sb.append(e.getKey()).append(':').append(e.getValue());
            }
            meta.getPersistentDataContainer().set(KEY_ATTACHMENTS, PersistentDataType.STRING, sb.toString());
        }
        stack.setItemMeta(meta);
    }
}
