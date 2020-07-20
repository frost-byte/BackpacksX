package net.frostbyte.backpacksx.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public interface VersionBridge
{
	boolean registerWithServer();
	boolean serverHasRecipe(JavaPlugin plugin, String packName);
	void registerPackRecipe(JavaPlugin plugin, String packName, Player player);
	void unregisterPackRecipe(JavaPlugin plugin, String packName, Player player);
	void registerServerRecipe(JavaPlugin plugin, String packName);
	void unregisterServerRecipe(JavaPlugin plugin, String packName);
	void updatePackRecipe(JavaPlugin plugin, String packName, ShapedRecipe recipe);
	ShapedRecipe createShapedRecipe(JavaPlugin plugin, String packName, ItemStack itemStack);
	ItemStack createItemStack(Material material, int amount, short data);
	String givePack(JavaPlugin plugin, UUID playerID, ItemStack itemStack);
}
