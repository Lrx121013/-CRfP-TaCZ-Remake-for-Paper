package studio.lrxmc.trfp.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import studio.lrxmc.trfp.TRfPPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 配置管理：处理 {@code plugins/TRfP/config.yml} 的加载与重载。
 * 暴露给其它子系统的所有可调参数都集中在这里。
 */
public final class ConfigManager {

    private final TRfPPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(TRfPPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        // 从 jar 中读取默认配置以保证新字段有值
        try (var in = plugin.getResource("config.yml")) {
            if (in != null) {
                try (var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    YamlConfiguration def = YamlConfiguration.loadConfiguration(reader);
                    config.setDefaults(def);
                    config.options().copyDefaults(true);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("[TRfP] 读取默认配置失败：" + e.getMessage());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("[TRfP] 保存配置失败：" + e.getMessage());
        }
    }

    public FileConfiguration getRaw() {
        return config;
    }

    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }

    public String getResourcePackUrl() {
        return config.getString("resource-pack-url", "");
    }

    public String getResourcePackSha1() {
        return config.getString("resource-pack-sha1", "");
    }

    public boolean isEnableLua() {
        return config.getBoolean("enable-lua", true);
    }

    public int getMaxAttachDistance() {
        return config.getInt("max-attach-distance", 32);
    }

    public double getDamageMultiplier() {
        return config.getDouble("damage-multiplier", 1.0);
    }

    public double getHeadshotMultiplier() {
        return config.getDouble("headshot-multiplier", 1.5);
    }

    public String getDefaultFireMode() {
        return config.getString("default-fire-mode", "SEMI");
    }

    public String getLang() {
        return config.getString("lang", "zh_cn");
    }
}
