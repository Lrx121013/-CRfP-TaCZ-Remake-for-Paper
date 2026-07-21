package studio.lrxmc.trfp.ammo;

import studio.lrxmc.trfp.TRfPPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 弹药注册表：从 {@code data/trfp/ammo/} 加载。
 */
public class AmmoRegistry {

    private final TRfPPlugin plugin;
    private final Map<String, AmmoData> ammos = new LinkedHashMap<>();

    public AmmoRegistry(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        ammos.clear();
        File userDir = new File(plugin.getDataFolder(), "ammo");
        if (userDir.isDirectory()) {
            File[] files = userDir.listFiles((d, n) -> n.endsWith(".yml") || n.endsWith(".yaml"));
            if (files != null) {
                for (File f : files) {
                    try {
                        AmmoData d = readFromFile(f);
                        if (d != null && d.getId() != null) {
                            ammos.put(d.getId().toLowerCase(Locale.ROOT), d);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("[TRfP] 读取弹药 " + f.getName() + " 失败：" + e.getMessage());
                    }
                }
            }
        }
        try {
            for (String name : listJar("data/trfp/ammo/")) {
                try (InputStream in = plugin.getResource(name)) {
                    if (in == null) continue;
                    Yaml yaml = new Yaml();
                    Map<String, Object> map = yaml.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                    if (map == null) continue;
                    AmmoData d = AmmoData.deserialize(map);
                    if (d.getId() == null) {
                        String n = name.substring(name.lastIndexOf('/') + 1).replace(".yml", "").replace(".yaml", "");
                        d.setId(n);
                    }
                    ammos.putIfAbsent(d.getId().toLowerCase(Locale.ROOT), d);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] 读取内置弹药失败：" + e.getMessage());
        }
        plugin.getLogger().info("[TRfP] 弹药注册表: " + ammos.size() + " 项。");
    }

    public AmmoData get(String id) {
        if (id == null) return null;
        return ammos.get(id.toLowerCase(Locale.ROOT));
    }

    public Collection<AmmoData> all() {
        return Collections.unmodifiableCollection(ammos.values());
    }

    private AmmoData readFromFile(File f) throws IOException {
        Yaml yaml = new Yaml();
        try (var reader = new InputStreamReader(new java.io.FileInputStream(f), StandardCharsets.UTF_8)) {
            Map<String, Object> map = yaml.load(reader);
            if (map == null) return null;
            AmmoData d = AmmoData.deserialize(map);
            if (d.getId() == null) {
                d.setId(f.getName().replace(".yml", "").replace(".yaml", ""));
            }
            return d;
        }
    }

    private String[] listJar(String prefix) {
        List<String> list = new ArrayList<>();
        try {
            java.net.URL url = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            if (url == null) return new String[0];
            java.io.File jar = new java.io.File(url.toURI());
            if (!jar.isFile()) return new String[0];
            try (java.util.jar.JarFile jf = new java.util.jar.JarFile(jar)) {
                java.util.Enumeration<java.util.jar.JarEntry> en = jf.entries();
                while (en.hasMoreElements()) {
                    java.util.jar.JarEntry e = en.nextElement();
                    if (e.getName().startsWith(prefix) && !e.isDirectory()) list.add(e.getName());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] 列出 jar 资源失败：" + e.getMessage());
        }
        return list.toArray(new String[0]);
    }
}
