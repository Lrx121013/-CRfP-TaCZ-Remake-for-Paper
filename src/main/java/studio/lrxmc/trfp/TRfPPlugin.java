package studio.lrxmc.trfp;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import studio.lrxmc.trfp.ammo.AmmoRegistry;
import studio.lrxmc.trfp.attachment.AttachmentRegistry;
import studio.lrxmc.trfp.command.TRfPCommand;
import studio.lrxmc.trfp.config.ConfigManager;
import studio.lrxmc.trfp.data.PlayerDataManager;
import studio.lrxmc.trfp.gun.GunItemFactory;
import studio.lrxmc.trfp.gun.GunRegistry;
import studio.lrxmc.trfp.item.ItemRegistry;
import studio.lrxmc.trfp.listener.AimListener;
import studio.lrxmc.trfp.listener.CombatListener;
import studio.lrxmc.trfp.listener.PlayerConnectionListener;
import studio.lrxmc.trfp.listener.ReloadListener;
import studio.lrxmc.trfp.lua.LuaEngine;
import studio.lrxmc.trfp.recipe.RecipeRegistry;
import studio.lrxmc.trfp.resourcepack.ResourcePackManager;

/**
 * [TaCZ] REMAKE For Paper
 * 入口插件类。
 *
 * @author Lrxmcstudio.工作室的Lrx
 */
public final class TRfPPlugin extends JavaPlugin {

    private static TRfPPlugin instance;

    private ConfigManager configManager;
    private GunRegistry gunRegistry;
    private AttachmentRegistry attachmentRegistry;
    private AmmoRegistry ammoRegistry;
    private ItemRegistry itemRegistry;
    private GunItemFactory gunItemFactory;
    private PlayerDataManager playerDataManager;
    private LuaEngine luaEngine;
    private ResourcePackManager resourcePackManager;
    private RecipeRegistry recipeRegistry;

    @Override
    public void onEnable() {
        instance = this;

        long start = System.currentTimeMillis();
        getLogger().info("==================================================");
        getLogger().info(" [TaCZ] REMAKE For Paper (TRfP) v" + getDescription().getVersion());
        getLogger().info(" 作者: Lrxmcstudio.工作室的Lrx");
        getLogger().info(" 基于 TACZ (MCModderAnchor) 移植");
        getLogger().info("==================================================");

        saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        this.gunRegistry = new GunRegistry(this);
        this.gunRegistry.load();

        this.attachmentRegistry = new AttachmentRegistry(this);
        this.attachmentRegistry.load();

        this.ammoRegistry = new AmmoRegistry(this);
        this.ammoRegistry.load();

        this.itemRegistry = new ItemRegistry(this);
        this.itemRegistry.register();

        this.gunItemFactory = new GunItemFactory(this);

        this.playerDataManager = new PlayerDataManager(this);
        this.playerDataManager.loadAll();

        this.luaEngine = new LuaEngine(this);
        if (configManager.isEnableLua()) {
            this.luaEngine.loadAll();
        }

        this.resourcePackManager = new ResourcePackManager(this);

        this.recipeRegistry = new RecipeRegistry(this);
        this.recipeRegistry.registerAll();

        registerCommands();
        registerListeners();

        long cost = System.currentTimeMillis() - start;
        getLogger().info("[TRfP] 启动完成，耗时 " + cost + " ms。");
    }

    @Override
    public void onDisable() {
        getLogger().info("[TRfP] [TaCZ] REMAKE For Paper 已停用 —— 再见 Lrxmcstudio.工作室的Lrx");
        if (playerDataManager != null) playerDataManager.saveAll();
        if (luaEngine != null) luaEngine.close();
        instance = null;
    }

    private void registerCommands() {
        PluginCommand cmd = getCommand("trfp");
        if (cmd == null) {
            getLogger().warning("[TRfP] 找不到 /trfp 命令，请检查 plugin.yml。");
            return;
        }
        TRfPCommand executor = new TRfPCommand(this);
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerConnectionListener(this), this);
        pm.registerEvents(new CombatListener(this), this);
        pm.registerEvents(new ReloadListener(this), this);
        pm.registerEvents(new AimListener(this), this);
    }

    public static TRfPPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public GunRegistry getGunRegistry() { return gunRegistry; }
    public AttachmentRegistry getAttachmentRegistry() { return attachmentRegistry; }
    public AmmoRegistry getAmmoRegistry() { return ammoRegistry; }
    public ItemRegistry getItemRegistry() { return itemRegistry; }
    public GunItemFactory getGunItemFactory() { return gunItemFactory; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public LuaEngine getLuaEngine() { return luaEngine; }
    public ResourcePackManager getResourcePackManager() { return resourcePackManager; }
    public RecipeRegistry getRecipeRegistry() { return recipeRegistry; }
}
