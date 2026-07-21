package studio.lrxmc.trfp.resourcepack;

import studio.lrxmc.trfp.TRfPPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * 资源包管理：首次启动时将 jar 内的资源包模板解压到 {@code plugins/TRfP/resourcepack/}。
 * 管理员可上传至自己的 CDN 后填入 config.yml 的 {@code resource-pack-url}。
 */
public class ResourcePackManager {

    private final TRfPPlugin plugin;

    public ResourcePackManager(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public File extractPackTemplate() {
        File dir = new File(plugin.getDataFolder(), "resourcepack");
        if (!dir.exists()) dir.mkdirs();
        // pack.mcmeta
        File meta = new File(dir, "pack.mcmeta");
        if (!meta.exists()) {
            try (InputStream in = plugin.getResource("resourcepack/pack.mcmeta")) {
                if (in != null) Files.copy(in, meta.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                plugin.getLogger().warning("[TRfP] 写出 pack.mcmeta 失败：" + e.getMessage());
            }
        }
        // 其他资源
        copyDir("resourcepack/assets/", new File(dir, "assets"));
        return dir;
    }

    private void copyDir(String jarPrefix, File destDir) {
        try {
            if (!destDir.exists()) destDir.mkdirs();
            java.net.URL url = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            if (url == null) return;
            java.io.File jarFile = new java.io.File(url.toURI());
            if (!jarFile.isFile()) return;
            try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
                java.util.Enumeration<java.util.jar.JarEntry> en = jar.entries();
                while (en.hasMoreElements()) {
                    java.util.jar.JarEntry e = en.nextElement();
                    if (!e.getName().startsWith(jarPrefix) || e.isDirectory()) continue;
                    File out = new File(plugin.getDataFolder(), e.getName());
                    if (!out.exists()) {
                        out.getParentFile().mkdirs();
                        try (InputStream in = jar.getInputStream(e)) {
                            Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] 复制资源目录失败：" + e.getMessage());
        }
    }
}
