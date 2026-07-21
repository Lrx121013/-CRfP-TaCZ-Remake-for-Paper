package studio.lrxmc.trfp.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import studio.lrxmc.trfp.TRfPPlugin;
import studio.lrxmc.trfp.gun.GunData;
import studio.lrxmc.trfp.gun.GunItemFactory;
import studio.lrxmc.trfp.gun.GunItemNBT;
import studio.lrxmc.trfp.item.ItemRegistry;

import java.util.Locale;

/**
 * 配方注册：注册原 TACZ 模组的核心工作台配方（简化版）。
 * <p>
 * 完整 80+ 把枪的配方可在运行时由用户放置 yml/json 自定义。
 */
public class RecipeRegistry {

    private final TRfPPlugin plugin;

    public RecipeRegistry(TRfPPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        for (GunData gun : plugin.getGunRegistry().all()) {
            registerGunRecipe(gun);
        }
        registerAmmoRecipes();
        registerAttachmentRecipes();
        plugin.getLogger().info("[TRfP] 配方注册完成。");
    }

    private void registerGunRecipe(GunData gun) {
        NamespacedKey key = new NamespacedKey(plugin, "gun_" + gun.getId().toLowerCase(Locale.ROOT));
        ShapedRecipe recipe = new ShapedRecipe(key, plugin.getGunItemFactory().create(gun));
        recipe.shape("ISI", "IBI", "STS");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(plugin.getItemRegistry().get("gun_part_trigger")));
        recipe.setIngredient('B', new RecipeChoice.ExactChoice(plugin.getItemRegistry().get("gun_part_barrel")));
        Bukkit.addRecipe(recipe);
    }

    private void registerAmmoRecipes() {
        registerAmmoShapeless("9mm", "steel_ingot", "gunpowder");
        registerAmmoShapeless("5_56mm", "steel_ingot", "gunpowder");
        registerAmmoShapeless("7_62mm", "steel_ingot", "gunpowder");
        registerAmmoShapeless("12g", "steel_ingot", "gunpowder");
        registerAmmoShapeless("45acp", "steel_ingot", "gunpowder");
        registerAmmoShapeless("50bmg", "steel_ingot", "gunpowder");
        registerAmmoShapeless("308", "steel_ingot", "gunpowder");
    }

    private void registerAmmoShapeless(String ammoId, String... matIds) {
        NamespacedKey key = new NamespacedKey(plugin, "ammo_" + ammoId);
        ItemStack out = plugin.getItemRegistry().get("ammo_" + ammoId);
        if (out == null) return;
        out.setAmount(8);
        ShapelessRecipe recipe = new ShapelessRecipe(key, out);
        for (String m : matIds) {
            recipe.addIngredient(new RecipeChoice.ExactChoice(plugin.getItemRegistry().get(m)));
        }
        Bukkit.addRecipe(recipe);
    }

    private void registerAttachmentRecipes() {
        // 简易示范：瞄具配方
        registerAttachmentShaped("scope_holographic", "SI", "IS");
        registerAttachmentShaped("scope_acog", "SIS", "IGI");
        registerAttachmentShaped("scope_sniper", "SGS", "SGS");
        registerAttachmentShaped("grip_vertical", " S ", " S ", "III");
        registerAttachmentShaped("grip_angled", " S ", "IS ", "I  ");
        registerAttachmentShaped("stock_tactical", "SSS", " I ", " I ");
        registerAttachmentShaped("barrel_suppressor", "III", " T ", " I ");
        registerAttachmentShaped("barrel_compensator", "III", "ITI", "III");
    }

    private void registerAttachmentShaped(String attachId, String... shapeRows) {
        ItemStack out = plugin.getItemRegistry().get("attach_" + attachId);
        if (out == null) return;
        NamespacedKey key = new NamespacedKey(plugin, "attach_" + attachId);
        ShapedRecipe recipe = new ShapedRecipe(key, out);
        recipe.shape(shapeRows);
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('G', Material.GLASS_PANE);
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(plugin.getItemRegistry().get("gun_part_trigger")));
        Bukkit.addRecipe(recipe);
    }
}
