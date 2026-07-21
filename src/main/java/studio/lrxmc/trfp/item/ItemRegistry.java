package studio.lrxmc.trfp.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studio.lrxmc.trfp.TRfPPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 物品注册表：注册 / 缓存 / 查询所有自定义物品（枪械、弹药、附件、原材料）。
 * <p>
 * 每个物品以 {@link NamespacedKey}（"trfp", id）作为唯一键。
 */
public class ItemRegistry {

    public static final String NAMESPACE = "trfp";

    private final TRfPPlugin plugin;
    private final Map<String, ItemStack> items = new LinkedHashMap<>();

    public ItemRegistry(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        items.clear();
        // 弹药占位物品（实际数据由 AmmoRegistry 提供更详细字段）
        registerAmmo("9mm", Material.IRON_NUGGET, "9mm 子弹");
        registerAmmo("5_56mm", Material.IRON_NUGGET, "5.56mm 子弹");
        registerAmmo("7_62mm", Material.IRON_NUGGET, "7.62mm 子弹");
        registerAmmo("12g", Material.IRON_NUGGET, "12 号霰弹");
        registerAmmo("45acp", Material.IRON_NUGGET, ".45 ACP 子弹");
        registerAmmo("50bmg", Material.IRON_NUGGET, ".50 BMG 子弹");
        registerAmmo("308", Material.IRON_NUGGET, ".308 子弹");

        // 附件占位物品
        registerAttachment("scope_holographic", "全息瞄具");
        registerAttachment("scope_acog", "ACOG 瞄具");
        registerAttachment("scope_sniper", "狙击镜");
        registerAttachment("grip_vertical", "垂直握把");
        registerAttachment("grip_angled", "斜握把");
        registerAttachment("stock_tactical", "战术枪托");
        registerAttachment("stock_sniper", "狙击枪托");
        registerAttachment("barrel_suppressor", "消音器");
        registerAttachment("barrel_compensator", "制退器");
        registerAttachment("muzzle_extended", "延伸枪管");

        // 原材料
        registerItem("steel_ingot", Material.IRON_INGOT, "钢板");
        registerItem("gun_part_trigger", Material.FLINT, "扳机组件");
        registerItem("gun_part_barrel", Material.STICK, "枪管");
        registerItem("gun_part_stock", Material.STICK, "枪托");
        registerItem("spring", Material.IRON_NUGGET, "弹簧");
        registerItem("gunpowder", Material.GUNPOWDER, "火药");

        plugin.getLogger().info("[TRfP] 物品注册表加载完成，共 " + items.size() + " 项。");
    }

    private void registerItem(String id, Material mat, String name) {
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(net.kyori.adventure.text.Component.text(name)
                .color(net.kyori.adventure.text.format.NamedTextColor.WHITE)
                .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(keyOf(id), PersistentDataType.STRING, id);
        stack.setItemMeta(meta);
        items.put(id.toLowerCase(Locale.ROOT), stack);
    }

    private void registerAmmo(String id, Material mat, String name) {
        registerItem("ammo_" + id, mat, name);
    }

    private void registerAttachment(String id, String name) {
        registerItem("attach_" + id, Material.ECHO_SHARD, name);
    }

    public ItemStack get(String id) {
        if (id == null) return null;
        return items.get(id.toLowerCase(Locale.ROOT)).clone();
    }

    public boolean isCustomItem(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return false;
        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (NamespacedKey k : pdc.getKeys()) {
            if (NAMESPACE.equals(k.getNamespace())) return true;
        }
        return false;
    }

    public String getItemId(ItemStack stack) {
        if (!isCustomItem(stack)) return null;
        for (NamespacedKey k : stack.getItemMeta().getPersistentDataContainer().getKeys()) {
            if (NAMESPACE.equals(k.getNamespace())) {
                return stack.getItemMeta().getPersistentDataContainer().get(k, PersistentDataType.STRING);
            }
        }
        return null;
    }

    public Collection<ItemStack> allItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public static NamespacedKey keyOf(String key) {
        return new NamespacedKey(NAMESPACE, key);
    }
}
