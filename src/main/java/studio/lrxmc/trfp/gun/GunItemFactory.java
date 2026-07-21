package studio.lrxmc.trfp.gun;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import studio.lrxmc.trfp.TRfPPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 枪械物品工厂：基于 {@link GunData} 构造一个完整的 {@link ItemStack}。
 * <p>
 * 物品材质默认为 {@link Material#IRON_HORSE_ARMOR}（Paper 上无法自定义模型；
 * 实际视觉由 Resource Pack 中的 {@code assets/trfp/models/item/<gunId>.json} 决定），
 * 玩家在背包中看到的图标由资源包覆盖。
 */
public class GunItemFactory {

    private final TRfPPlugin plugin;

    public GunItemFactory(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public ItemStack create(@NotNull GunData data) {
        ItemStack stack = new ItemStack(Material.IRON_HORSE_ARMOR);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(displayName(data));
        meta.setCustomModelData(computeModelId(data));
        meta.addItemFlags(ItemFlag.values());
        // 隐藏附魔光效，但保留耐久可视化
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("类型: " + data.getId()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        if (data.getDefaultFireMode() != null) {
            lore.add(Component.text("默认模式: " + data.getDefaultFireMode().name()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        }
        if (data.getMagazineSize() > 0) {
            lore.add(Component.text("弹匣: " + data.getMagazineSize()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.text("伤害: " + data.getDamage()).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("射速: " + data.getFireRate() + " RPM").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("TRfP · Lrxmcstudio.工作室的Lrx").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, true));
        meta.lore(lore);
        stack.setItemMeta(meta);

        // 写入 NBT
        GunItemNBT.setGunId(stack, data.getId());
        GunItemNBT.setFireMode(stack, data.getDefaultFireMode() == null ? FireMode.SEMI : data.getDefaultFireMode());
        GunItemNBT.setAmmo(stack, 0);
        GunItemNBT.setAmmoDummy(stack, 0);
        GunItemNBT.setLevel(stack, data.getLevel() <= 0 ? 1 : data.getLevel());

        return stack;
    }

    private Component displayName(GunData data) {
        String name = data.getDisplayName() == null ? data.getId() : data.getDisplayName();
        return Component.text("[" + name + "]").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * 自定义模型数据：使用 gunId 的稳定哈希作为数值，
     * 让 Resource Pack 可以将模型 key 映射到该数值。
     */
    private int computeModelId(GunData data) {
        return Math.floorMod(data.getId().hashCode(), 1_000_000);
    }
}
