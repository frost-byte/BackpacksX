package me.tade.backpacks.v1_14_R1.util;

import me.tade.backpacks.util.VersionBridge;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
	){
		NamespacedKey namespacedKey = new NamespacedKey(plugin, name);
		return new ShapedRecipe(namespacedKey, itemStack);
	}

	@Override
	public ItemStack createItemStack(Material material, int amount, short data)
	{
		return new ItemStack(material, amount);
	}
}
