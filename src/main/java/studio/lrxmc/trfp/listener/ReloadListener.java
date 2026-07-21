package studio.lrxmc.trfp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemNBT;
import studio.lrxmc.trfp.item.ItemRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 换弹监听：监听玩家使用物品事件 + 切副手。
 * 简化实现：玩家将"匹配口径的弹药"放在副手并按 F 时触发换弹。
 */
public class ReloadListener implements Listener {

    private final TRfPPlugin plugin;
    private final Map<UUID, Long> reloading = new HashMap<>();

    public ReloadListener(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        ItemStack main = p.getInventory().getItemInMainHand();
        if (!GunItemNBT.isGunItem(main)) return;
        GunData data = plugin.getGunRegistry().get(GunItemNBT.getGunId(main));
        if (data == null) return;
        // 副手找弹药
        ItemStack off = event.getOffHandItem();
        if (off == null || !off.hasItemMeta()) {
            p.sendActionBar(Component.text("副手没有匹配的弹药。").color(NamedTextColor.RED));
            return;
        }
        String offId = plugin.getItemRegistry().getItemId(off);
        if (offId == null || !offId.equals("ammo_" + data.getAmmoId())) {
            p.sendActionBar(Component.text("弹药口径不匹配（需要 " + data.getAmmoId() + "）。").color(NamedTextColor.RED));
            return;
        }
        event.setCancelled(true);
        startReload(p, main, data, off);
    }

    private void startReload(Player p, ItemStack gun, GunData data, ItemStack ammo) {
        if (reloading.containsKey(p.getUniqueId())) {
            p.sendActionBar(Component.text("正在换弹中…").color(NamedTextColor.YELLOW));
            return;
        }
        int need = data.getMagazineSize() - GunItemNBT.getAmmo(gun);
        if (need <= 0) {
            p.sendActionBar(Component.text("弹匣已满。").color(NamedTextColor.GREEN));
            return;
        }
        reloading.put(p.getUniqueId(), System.currentTimeMillis() + (long) (data.getReloadTimeTicks() * 50));
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 0.6f, 1.2f);
        p.sendActionBar(Component.text("换弹中…").color(NamedTextColor.GOLD));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            reloading.remove(p.getUniqueId());
            int available = ammo.getAmount();
            int take = Math.min(need, available);
            ammo.setAmount(available - take);
            GunItemNBT.setAmmo(gun, GunItemNBT.getAmmo(gun) + take);
            p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 0.5f, 1.5f);
            p.sendActionBar(Component.text("换弹完成 +" + take).color(NamedTextColor.GREEN));
        }, (long) data.getReloadTimeTicks());
    }

    public boolean isReloading(UUID id) {
        Long until = reloading.get(id);
        if (until == null) return false;
        if (System.currentTimeMillis() >= until) {
            reloading.remove(id);
            return false;
        }
        return true;
    }
}
