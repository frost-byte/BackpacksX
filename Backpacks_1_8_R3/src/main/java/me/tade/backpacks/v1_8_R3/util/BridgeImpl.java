package me.tade.backpacks.v1_8_R3.util;

import me.tade.backpacks.util.VersionBridge;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class BridgeImpl implements VersionBridge
{
	public BridgeImpl() {}

	@Override
	public void registerPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {}

	@Override
	public void unregisterPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {}

	@Override
	public void updatePackRecipe(
		JavaPlugin plugin,
		String packName,
		ShapedRecipe recipe
	) {
		if (recipe != null)
		{
			try
			{
				plugin.getServer().addRecipe(recipe);
			}
			catch (IllegalStateException ignored) {}
		}
	}

	@Override
	public ShapedRecipe createShapedRecipe(
		JavaPlugin plugin,
		String packName,
		ItemStack itemStack
	) {
		return new ShapedRecipe(itemStack);
	}

	@Override
	public ItemStack createItemStack(Material material, int amount, short data)
	{
		return new ItemStack(material, amount, data);
	}
}
