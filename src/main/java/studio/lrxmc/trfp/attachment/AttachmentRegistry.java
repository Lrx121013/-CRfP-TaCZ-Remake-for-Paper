package studio.lrxmc.trfp.attachment;

import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.AttachmentData;
import studio.lrxmc.trfp.gun.AttachmentSlotType;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 附件注册表：从 {@code data/trfp/attachments/} 加载附件定义。
 * <p>
 * 与原 TACZ attachment index 保持兼容：使用 yaml 简化版（无嵌套 maps）。
 */
public class AttachmentRegistry {

    private final TRfPPlugin plugin;
    private final Map<String, AttachmentData> attachments = new LinkedHashMap<>();

    public AttachmentRegistry(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        attachments.clear();
        File userDir = new File(plugin.getDataFolder(), "attachments");
        if (userDir.isDirectory()) {
            File[] files = userDir.listFiles((d, n) -> n.endsWith(".yml") || n.endsWith(".yaml"));
            if (files != null) {
                for (File f : files) {
                    try {
                        AttachmentData d = readFromFile(f);
                        if (d != null && d.getId() != null) {
                            attachments.put(d.getId().toLowerCase(Locale.ROOT), d);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("[TRfP] 读取附件 " + f.getName() + " 失败：" + e.getMessage());
                    }
                }
            }
        }
        // 内置兜底
        try {
            for (String name : listJar("data/trfp/attachments/")) {
                try (InputStream in = plugin.getResource(name)) {
                    if (in == null) continue;
                    Yaml yaml = new Yaml();
                    Map<String, Object> map = yaml.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                    if (map == null) continue;
                    AttachmentData d = AttachmentData.deserialize(map);
                    if (d.getId() == null) {
                        String base = name.substring(name.lastIndexOf('/') + 1);
                        base = base.replace(".yml", "").replace(".yaml", "");
                        d.setId(base);
                    }
                    attachments.putIfAbsent(d.getId().toLowerCase(Locale.ROOT), d);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] 读取内置附件失败：" + e.getMessage());
        }
        plugin.getLogger().info("[TRfP] 附件注册表: " + attachments.size() + " 项。");
    }

    public AttachmentData get(String id) {
        if (id == null) return null;
        return attachments.get(id.toLowerCase(Locale.ROOT));
    }

    public Collection<AttachmentData> all() {
        return Collections.unmodifiableCollection(attachments.values());
    }

    public Map<AttachmentSlotType, List<AttachmentData>> bySlot() {
        Map<AttachmentSlotType, List<AttachmentData>> map = new EnumMap<>(AttachmentSlotType.class);
        for (AttachmentData d : attachments.values()) {
            if (d.getSlotType() == null) continue;
            map.computeIfAbsent(d.getSlotType(), k -> new ArrayList<>()).add(d);
        }
        return map;
    }

    private AttachmentData readFromFile(File f) throws IOException {
        Yaml yaml = new Yaml();
        try (var reader = new InputStreamReader(new java.io.FileInputStream(f), StandardCharsets.UTF_8)) {
            Map<String, Object> map = yaml.load(reader);
            if (map == null) return null;
            AttachmentData d = AttachmentData.deserialize(map);
            if (d.getId() == null) {
                String n = f.getName().replace(".yml", "").replace(".yaml", "");
                d.setId(n);
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
