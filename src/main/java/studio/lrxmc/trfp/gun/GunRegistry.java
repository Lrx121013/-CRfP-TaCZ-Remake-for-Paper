package studio.lrxmc.trfp.gun;

import studio.lrxmc.trfp.TRfPPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 枪械注册表：从 {@code plugins/TRfP/guns/} 或 jar 内置资源中读取每把枪的 yml/json 定义。
 * <p>
 * 数据放置在 {@code src/main/resources/data/trfp/guns/<gunId>.yml}，方便服务部署时直接覆盖。
 *
 * @author Lrxmcstudio.工作室的Lrx
 */
public class GunRegistry {

    private final TRfPPlugin plugin;
    private final Map<String, GunData> guns = new LinkedHashMap<>();

    public GunRegistry(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 加载所有枪械数据。优先从 {@code plugins/TRfP/guns/} 读取，
     * 然后从 jar 内的 {@code data/trfp/guns/} 读取。
     */
    public void load() {
        guns.clear();

        // 1. 读取用户目录（可覆盖 jar 内置）
        File userDir = new File(plugin.getDataFolder(), "guns");
        if (userDir.isDirectory()) {
            File[] files = userDir.listFiles((d, name) -> name.endsWith(".yml") || name.endsWith(".json"));
            if (files != null) {
                for (File f : files) {
                    try {
                        GunData data = readFromFile(f);
                        if (data != null && data.getId() != null) {
                            guns.put(data.getId().toLowerCase(Locale.ROOT), data);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("[TRfP] 读取枪械文件 " + f.getName() + " 失败：" + e.getMessage());
                    }
                }
            }
        }

        // 2. 读取 jar 内置资源（兜底）
        try {
            Path dir = new File(plugin.getDataFolder(), "tmp_gun_index").toPath();
            Files.createDirectories(dir);
            int loaded = 0;
            for (String name : listJarResources("data/trfp/guns/")) {
                try (InputStream in = plugin.getResource(name)) {
                    if (in == null) continue;
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                    GunData data = GunData.deserialize(cfg.getValues(false));
                    if (data.getId() == null) {
                        String base = name.substring(name.lastIndexOf('/') + 1);
                        base = base.replace(".yml", "").replace(".json", "");
                        data.setId(base);
                    }
                    String key = data.getId().toLowerCase(Locale.ROOT);
                    guns.putIfAbsent(key, data);
                    loaded++;
                }
            }
            plugin.getLogger().info("[TRfP] 已加载内置枪械定义 " + loaded + " 把；当前注册表共 " + guns.size() + " 把。");
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] 读取 jar 内置枪械数据失败：" + e.getMessage());
        }
    }

    public GunData get(String id) {
        if (id == null) return null;
        return guns.get(id.toLowerCase(Locale.ROOT));
    }

    public Collection<GunData> all() {
        return Collections.unmodifiableCollection(guns.values());
    }

    private GunData readFromFile(File file) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        GunData data = GunData.deserialize(cfg.getValues(false));
        if (data.getId() == null) {
            String name = file.getName();
            name = name.replace(".yml", "").replace(".json", "");
            data.setId(name);
        }
        return data;
    }

    private String[] listJarResources(String prefix) throws IOException {
        java.util.List<String> list = new java.util.ArrayList<>();
        java.util.jar.JarFile jar = null;
        try {
            java.net.URL url = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            if (url == null) return new String[0];
            java.io.File jarFile = new java.io.File(url.toURI());
            if (!jarFile.isFile()) return new String[0];
            jar = new java.util.jar.JarFile(jarFile);
            java.util.Enumeration<java.util.jar.JarEntry> en = jar.entries();
            while (en.hasMoreElements()) {
                java.util.jar.JarEntry e = en.nextElement();
                if (e.getName().startsWith(prefix) && !e.isDirectory()) {
                    list.add(e.getName());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] 列出 jar 资源失败：" + e.getMessage());
        } finally {
            if (jar != null) try { jar.close(); } catch (Exception ignored) {}
        }
        return list.toArray(new String[0]);
    }
}
