package studio.lrxmc.trfp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemNBT;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 瞄准监听：根据玩家潜行状态切换"瞄准"模式。
 * <p>
 * 由于 Paper 不允许动态修改 FOV，这里使用 {@code player.sendActionBar} 提示玩家。
 * Resource Pack 配合 vanilla Perspective 设置可以实现客户端侧视觉缩放。
 */
public class AimListener implements Listener {

    private final TRfPPlugin plugin;
    private final Map<UUID, Boolean> aiming = new HashMap<>();

    public AimListener(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (!GunItemNBT.isGunItem(hand)) return;
        GunData data = plugin.getGunRegistry().get(GunItemNBT.getGunId(hand));
        if (data == null) return;
        boolean isAim = event.isSneaking();
        aiming.put(p.getUniqueId(), isAim);
        if (isAim) {
            p.sendActionBar(Component.text("瞄准中…").color(NamedTextColor.GREEN));
        } else {
            p.sendActionBar(Component.text("取消瞄准。").color(NamedTextColor.GRAY));
        }
    }

    public boolean isAiming(Player p) {
        return aiming.getOrDefault(p.getUniqueId(), false);
    }
}
