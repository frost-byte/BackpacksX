package me.tade.backpacks.v1_12_R1.util;

import me.tade.backpacks.util.VersionBridge;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class BridgeImpl implements VersionBridge
{
	public BridgeImpl() {}

	@Override
	public ShapedRecipe createShapedRecipe(
		JavaPlugin plugin,
		String name,
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
