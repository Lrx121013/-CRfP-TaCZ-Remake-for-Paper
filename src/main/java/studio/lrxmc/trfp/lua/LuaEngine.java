package studio.lrxmc.trfp.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import studio.lrxmc.trfp.TRfPPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Lua 引擎集成：基于 luaj，迁移自原 TACZ 的脚本系统。
 * <p>
 * 文件位置：{@code src/main/resources/trfp-lua/gun/<gunId>.lua}。
 * 提供钩子：{@code on_shoot}、{@code on_reload}、{@code on_hit}、{@code on_attach}。
 */
public class LuaEngine {

    private final TRfPPlugin plugin;
    private final Map<String, LuaValue> gunScripts = new HashMap<>();
    private final AtomicReference<LuaValue> global = new AtomicReference<>();

    public LuaEngine(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        gunScripts.clear();
        // 初始化全局环境
        LuaValue g = JsePlatform.standardGlobals();
        g.set("trfp", LuaValue.tableOf());
        g.get("trfp").set("version", LuaValue.valueOf("1.0.0"));
        g.get("trfp").set("author", LuaValue.valueOf("Lrxmcstudio.工作室的Lrx"));
        global.set(g);

        // 列举所有脚本
        try {
            int loaded = 0;
            for (String name : listJar("trfp-lua/gun/")) {
                if (!name.endsWith(".lua")) continue;
                try (InputStream in = plugin.getResource(name)) {
                    if (in == null) continue;
                    LuaValue chunk = g.load(new InputStreamReader(in, StandardCharsets.UTF_8), name);
                    chunk.call();
                    String gunId = name.substring(name.lastIndexOf('/') + 1, name.length() - 4);
                    gunScripts.put(gunId.toLowerCase(Locale.ROOT), g);
                    loaded++;
                } catch (Exception e) {
                    plugin.getLogger().warning("[TRfP] 加载 Lua 脚本 " + name + " 失败：" + e.getMessage());
                }
            }
            plugin.getLogger().info("[TRfP] Lua 引擎：已加载 " + loaded + " 个枪械脚本。");
        } catch (Exception e) {
            plugin.getLogger().warning("[TRfP] Lua 初始化失败：" + e.getMessage());
        }
    }

    public void reload() {
        close();
        loadAll();
    }

    public void close() {
        gunScripts.clear();
        global.set(null);
    }

    public void call(String gunId, String hook, Object... args) {
        if (!plugin.getConfigManager().isEnableLua()) return;
        LuaValue g = global.get();
        if (g == null) return;
        String key = gunId == null ? "" : gunId.toLowerCase(Locale.ROOT);
        LuaValue env = gunScripts.get(key);
        if (env == null) return;
        LuaValue fn = env.get(hook);
        if (fn.isnil()) return;
        try {
            LuaValue[] luaArgs = new LuaValue[args.length];
            for (int i = 0; i < args.length; i++) {
                luaArgs[i] = toLua(args[i]);
            }
            fn.invoke(luaArgs);
        } catch (Exception e) {
            plugin.getLogger().fine("[TRfP] Lua 钩子 " + hook + " 异常：" + e.getMessage());
        }
    }

    private LuaValue toLua(Object o) {
        if (o == null) return LuaValue.NIL;
        if (o instanceof String s) return LuaValue.valueOf(s);
        if (o instanceof Number n) return LuaValue.valueOf(n.doubleValue());
        if (o instanceof Boolean b) return LuaValue.valueOf(b);
        return LuaValue.valueOf(o.toString());
    }

    private String[] listJar(String prefix) {
        java.util.List<String> list = new java.util.ArrayList<>();
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
