package me.tade.backpacks.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public interface VersionBridge
{
	ShapedRecipe createShapedRecipe(JavaPlugin plugin, String name, ItemStack itemStack);
	ItemStack createItemStack(Material material, int amount, short data);
}
