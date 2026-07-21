package studio.lrxmc.trfp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import studio.lrxmc.trfp.TRfPPlugin;

/**
 * 玩家连接监听：处理资源包下发与玩家数据快照。
 */
public class PlayerConnectionListener implements Listener {

    private final TRfPPlugin plugin;

    public PlayerConnectionListener(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().onJoin(player);
        // 下发资源包
        String url = plugin.getConfigManager().getResourcePackUrl();
        String sha1 = plugin.getConfigManager().getResourcePackSha1();
        if (url != null && !url.isBlank()) {
            try {
                player.setResourcePack(url, sha1.isBlank() ? null : sha1, false, Component.text("§6[TRfP] §f客户端资源已下发 —— 作者 Lrxmcstudio.工作室的Lrx"));
            } catch (Exception e) {
                plugin.getLogger().warning("[TRfP] 下发资源包失败：" + e.getMessage());
            }
        }
        player.sendMessage(Component.text("[TRfP] 欢迎使用 ").color(NamedTextColor.GOLD)
                .append(Component.text("[TaCZ] REMAKE For Paper").color(NamedTextColor.AQUA))
                .append(Component.text(" v" + plugin.getDescription().getVersion()).color(NamedTextColor.GRAY))
                .append(Component.text(" —— 作者 Lrxmcstudio.工作室的Lrx").color(NamedTextColor.LIGHT_PURPLE)));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().onQuit(event.getPlayer());
    }
}
