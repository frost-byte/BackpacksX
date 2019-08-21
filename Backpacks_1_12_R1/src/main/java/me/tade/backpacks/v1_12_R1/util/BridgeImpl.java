package me.tade.backpacks.v1_12_R1.util;

import me.tade.backpacks.util.VersionBridge;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class BridgeImpl implements VersionBridge
{
	public BridgeImpl() {}

	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private Map<NamespacedKey, ShapedRecipe> recipes = new HashMap<>();

	@Override
	public void registerPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);
		Logger logger = plugin.getLogger();

		if (!recipes.isEmpty())
		{
			ShapedRecipe recipe = recipes.getOrDefault(lookupKey, null);
			if (recipe != null)
			{
				if (recipes != null)
					recipes.put(lookupKey, recipe);

				try
				{
					if (plugin.getServer().addRecipe(recipe)) {
						player.sendMessage(
							ChatColor.AQUA + "" +  ChatColor.BOLD + "Discovered " + packName +
								" recipe, log back in use it!"
						);
					}
				}
				catch (IllegalStateException ignored) {}
			}
		}
	}

	@Override
	public void unregisterPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {
	}

	@Override
	public void updatePackRecipe(
		JavaPlugin plugin,
		String packName,
		ShapedRecipe recipe
	)
	{
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);

		if (recipe != null && recipes != null)
			recipes.put(lookupKey, recipe);
	}

	@Override
	public ShapedRecipe createShapedRecipe(
		JavaPlugin plugin,
		String packName,
		ItemStack itemStack
	){
		NamespacedKey key = new NamespacedKey(plugin, packName);
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipes.putIfAbsent(key, recipe);
		//plugin.getLogger().info("Bridge: Creating Shaped Recipe for " + key);
		return recipe;
	}

	@Override
	public ItemStack createItemStack(Material material, int amount, short data)
	{
		return new ItemStack(material, amount, data);
	}
}
