package studio.lrxmc.trfp.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemNBT;
import studio.lrxmc.trfp.gun.GunItemFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家数据管理：使用 {@code plugins/TRfP/data/<uuid>.yml} 存储玩家所有枪械 NBT。
 * <p>
 * 玩家背包里的枪械 ItemStack 其 NBT 本身就足够表达状态（持久化 PDC），
 * 这里的数据文件主要用途是：在玩家意外丢枪时能通过 UUID 找回 / 用于跨服同步。
 */
public class PlayerDataManager {

    private final TRfPPlugin plugin;
    private final Map<UUID, YamlConfiguration> cache = new HashMap<>();

    public PlayerDataManager(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        File dir = dataDir();
        if (!dir.exists()) dir.mkdirs();
    }

    public void saveAll() {
        for (UUID id : cache.keySet()) save(id);
    }

    public YamlConfiguration get(UUID id) {
        return cache.computeIfAbsent(id, this::load);
    }

    public YamlConfiguration load(UUID id) {
        File f = new File(dataDir(), id + ".yml");
        if (!f.exists()) {
            try { f.createNewFile(); } catch (IOException e) {
                plugin.getLogger().warning("[TRfP] 无法创建玩家数据文件 " + f.getName() + "：" + e.getMessage());
            }
        }
        return YamlConfiguration.loadConfiguration(f);
    }

    public void save(UUID id) {
        YamlConfiguration cfg = cache.get(id);
        if (cfg == null) return;
        File f = new File(dataDir(), id + ".yml");
        try {
            cfg.save(f);
        } catch (IOException e) {
            plugin.getLogger().warning("[TRfP] 写入玩家数据文件 " + f.getName() + " 失败：" + e.getMessage());
        }
    }

    public void onQuit(Player player) {
        UUID id = player.getUniqueId();
        YamlConfiguration cfg = get(id);
        cfg.set("lastName", player.getName());
        cfg.set("lastSeen", System.currentTimeMillis());
        // 记录当前主手枪械 NBT 快照
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (GunItemNBT.isGunItem(hand)) {
            cfg.set("mainhand.gunId", GunItemNBT.getGunId(hand));
            cfg.set("mainhand.ammo", GunItemNBT.getAmmo(hand));
            cfg.set("mainhand.fireMode", GunItemNBT.getFireMode(hand).name());
            cfg.set("mainhand.skin", GunItemNBT.getSkin(hand));
        }
        save(id);
    }

    public void onJoin(Player player) {
        YamlConfiguration cfg = get(player.getUniqueId());
        cfg.set("lastName", player.getName());
        cfg.set("lastSeen", System.currentTimeMillis());
    }

    /**
     * 玩家首次进入时由 CommandTrigger 调用此方法初始化"默认枪械包"（可选）。
     * 当前默认不发放任何东西，避免误刷物品。
     */
    public void grantStarterKit(Player player) {
        GunData data = plugin.getGunRegistry().get("ak47");
        if (data == null) data = plugin.getGunRegistry().all().stream().findFirst().orElse(null);
        if (data == null) return;
        ItemStack gun = plugin.getGunItemFactory().create(data);
        GunItemNBT.setAmmo(gun, data.getMagazineSize());
        GunItemNBT.setAmmoDummy(gun, data.getMagazineSize() * 3);
        player.getInventory().addItem(gun);
    }

    private File dataDir() {
        return new File(plugin.getDataFolder(), "data");
    }
}
