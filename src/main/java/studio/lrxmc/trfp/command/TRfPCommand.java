package studio.lrxmc.trfp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemNBT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * /trfp 主命令。
 * <p>
 * 子命令：
 * <ul>
 *   <li>version —— 显示版本</li>
 *   <li>reload —— 重载配置 / Lua / 配方（需要 trfp.admin）</li>
 *   <li>give &lt;player&gt; &lt;gunId&gt; [amount] —— 给予枪械</li>
 *   <li>giveammo &lt;player&gt; &lt;ammoId&gt; &lt;count&gt; —— 给予弹药</li>
 *   <li>attach &lt;slot&gt; &lt;attachmentId&gt; —— 给自己当前手持枪装附件</li>
 * </ul>
 */
public class TRfPCommand implements CommandExecutor, TabCompleter {

    private final TRfPPlugin plugin;

    public TRfPCommand(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "version", "v", "ver" -> sendVersion(sender);
            case "reload" -> doReload(sender);
            case "give" -> doGive(sender, args);
            case "giveammo" -> doGiveAmmo(sender, args);
            case "attach" -> doAttach(sender, args);
            case "help", "?" -> sendHelp(sender);
            default -> {
                sender.sendMessage(Component.text("[TRfP] 未知子命令：" + sub).color(NamedTextColor.RED));
                sendHelp(sender);
            }
        }
        return true;
    }

    private void sendVersion(CommandSender sender) {
        sender.sendMessage(Component.text("[TaCZ] REMAKE For Paper ")
                .color(NamedTextColor.AQUA)
                .append(Component.text("v" + plugin.getDescription().getVersion()).color(NamedTextColor.GRAY))
                .append(Component.text(" | 作者 ").color(NamedTextColor.WHITE))
                .append(Component.text("Lrxmcstudio.").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("工作室的Lrx").color(NamedTextColor.DARK_PURPLE)));
    }

    private void doReload(CommandSender sender) {
        if (!sender.hasPermission("trfp.admin")) {
            sender.sendMessage(Component.text("[TRfP] 你没有权限执行此命令。").color(NamedTextColor.RED));
            return;
        }
        plugin.getConfigManager().reload();
        plugin.getGunRegistry().load();
        plugin.getAttachmentRegistry().load();
        plugin.getAmmoRegistry().load();
        plugin.getRecipeRegistry().registerAll();
        if (plugin.getConfigManager().isEnableLua()) {
            plugin.getLuaEngine().reload();
        }
        sender.sendMessage(Component.text("[TRfP] 配置 / 枪械 / 附件 / 弹药 / 配方 / Lua 已重载。").color(NamedTextColor.GREEN));
    }

    private void doGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("trfp.command.give")) {
            sender.sendMessage(Component.text("[TRfP] 你没有权限执行此命令。").color(NamedTextColor.RED));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text("用法：/trfp give <玩家> <gunId> [数量]").color(NamedTextColor.RED));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("[TRfP] 玩家不在线：" + args[1]).color(NamedTextColor.RED));
            return;
        }
        GunData data = plugin.getGunRegistry().get(args[2]);
        if (data == null) {
            sender.sendMessage(Component.text("[TRfP] 未知枪械 ID：" + args[2]).color(NamedTextColor.RED));
            return;
        }
        int amount = 1;
        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); } catch (NumberFormatException ignored) {}
        }
        amount = Math.max(1, Math.min(amount, 64));
        for (int i = 0; i < amount; i++) {
            var stack = plugin.getGunItemFactory().create(data);
            GunItemNBT.setAmmo(stack, data.getMagazineSize());
            target.getInventory().addItem(stack);
        }
        sender.sendMessage(Component.text("[TRfP] 已给予 " + target.getName() + " " + amount + " 把 " + data.getId()).color(NamedTextColor.GREEN));
    }

    private void doGiveAmmo(CommandSender sender, String[] args) {
        if (!sender.hasPermission("trfp.command.giveammo")) {
            sender.sendMessage(Component.text("[TRfP] 你没有权限执行此命令。").color(NamedTextColor.RED));
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(Component.text("用法：/trfp giveammo <玩家> <ammoId> <数量>").color(NamedTextColor.RED));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("[TRfP] 玩家不在线：" + args[1]).color(NamedTextColor.RED));
            return;
        }
        int count;
        try { count = Integer.parseInt(args[3]); } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("[TRfP] 数量必须为整数。").color(NamedTextColor.RED));
            return;
        }
        var stack = plugin.getItemRegistry().get("ammo_" + args[2].toLowerCase(Locale.ROOT));
        if (stack == null) {
            sender.sendMessage(Component.text("[TRfP] 未知弹药 ID：" + args[2]).color(NamedTextColor.RED));
            return;
        }
        stack.setAmount(Math.max(1, Math.min(count, stack.getMaxStackSize())));
        target.getInventory().addItem(stack);
        sender.sendMessage(Component.text("[TRfP] 已给予 " + target.getName() + " " + count + " 发 " + args[2]).color(NamedTextColor.GREEN));
    }

    private void doAttach(CommandSender sender, String[] args) {
        if (!sender.hasPermission("trfp.command.attach")) {
            sender.sendMessage(Component.text("[TRfP] 你没有权限执行此命令。").color(NamedTextColor.RED));
            return;
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("[TRfP] 该命令只能由玩家执行。").color(NamedTextColor.RED));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text("用法：/trfp attach <slot> <attachmentId>").color(NamedTextColor.RED));
            return;
        }
        var hand = p.getInventory().getItemInMainHand();
        if (!GunItemNBT.isGunItem(hand)) {
            sender.sendMessage(Component.text("[TRfP] 手中未持有 TRfP 枪械。").color(NamedTextColor.RED));
            return;
        }
        String slot = args[1].toUpperCase(Locale.ROOT);
        String attachId = args[2].toLowerCase(Locale.ROOT);
        var att = plugin.getAttachmentRegistry().get(attachId);
        if (att == null) {
            sender.sendMessage(Component.text("[TRfP] 未知附件：" + attachId).color(NamedTextColor.RED));
            return;
        }
        if (!att.getSlotType().name().equals(slot)) {
            sender.sendMessage(Component.text("[TRfP] 附件 " + attachId + " 不属于 " + slot + " 槽位。").color(NamedTextColor.RED));
            return;
        }
        GunItemNBT.setAttachment(hand, slot, attachId);
        sender.sendMessage(Component.text("[TRfP] 已在 " + slot + " 槽位安装 " + attachId).color(NamedTextColor.GREEN));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("==== [TRfP] [TaCZ] REMAKE For Paper 帮助 ====").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/trfp version —— 显示版本与作者").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/trfp reload —— 重载配置 / Lua / 配方").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/trfp give <玩家> <gunId> [数量] —— 给予枪械").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/trfp giveammo <玩家> <ammoId> <数量> —— 给予弹药").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/trfp attach <slot> <attachmentId> —— 给自己枪装附件").color(NamedTextColor.WHITE));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            out.addAll(Arrays.asList("version", "reload", "give", "giveammo", "attach", "help"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "give", "giveammo" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
                }
                case "attach" -> out.addAll(Arrays.asList("SCOPE", "GRIP", "STOCK", "BARREL", "MUZZLE"));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                plugin.getGunRegistry().all().forEach(g -> out.add(g.getId()));
            } else if (args[0].equalsIgnoreCase("giveammo")) {
                out.addAll(Arrays.asList("9mm", "5_56mm", "7_62mm", "12g", "45acp", "50bmg", "308"));
            } else if (args[0].equalsIgnoreCase("attach")) {
                plugin.getAttachmentRegistry().all().forEach(a -> out.add(a.getId()));
            }
        }
        return out;
    }
}
