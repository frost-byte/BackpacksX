package net.frostbyte.backpacksx.v1_8_R3.util;

import net.frostbyte.backpacksx.BackpacksX;
import net.frostbyte.backpacksx.packs.ConfigPack;
import net.frostbyte.backpacksx.util.VersionBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.md_5.bungee.api.ChatColor.RED;

@SuppressWarnings("unused")
public class BridgeImpl implements VersionBridge
{
	public BridgeImpl() {}

	@Override
	public boolean registerWithServer()
	{
		return false;
	}

	@Override
	public boolean serverHasRecipe(JavaPlugin plugin, String packName)
	{
		List<Recipe> results = new ArrayList<>();
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		ConfigPack configPack = ((BackpacksX) plugin).getConfigPacks().getOrDefault(packName, null);

		if (configPack == null)
		{
			return false;
		}

		ItemStack backpack = configPack.getItemStack();
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			ItemStack currentResult = recipe.getResult();
			if (currentResult.getType() != backpack.getType()) {
				continue;
			}
			if (currentResult.hasItemMeta() && currentResult.getItemMeta().hasDisplayName())
			{
				String name = currentResult.getItemMeta().getDisplayName();
				if (name.contains(packName)) {
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public void registerPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {}

	@Override
	public void registerServerRecipe(JavaPlugin plugin, String packName)
	{
		if (!serverHasRecipe(plugin, packName))
		{
			ConfigPack configPack = ((BackpacksX) plugin).getConfigPacks().getOrDefault(packName, null);

			if (configPack != null)
			{
				Bukkit.getServer().addRecipe(configPack.getShapedRecipe());
			}
		}
	}

	@Override
	public void unregisterPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {
		if (serverHasRecipe(plugin, packName))
		{
			Bukkit.resetRecipes();
		}
	}

	@Override
	public void unregisterServerRecipe(JavaPlugin plugin, String packName)
	{
		if (serverHasRecipe(plugin, packName))
		{
			Bukkit.resetRecipes();
		}
	}

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

	@Override
	public String givePack(
		JavaPlugin plugin,
		UUID playerID,
		ItemStack itemStack
	) {
		Player player = Bukkit.getPlayer(playerID);

		if (player == null) {
			return RED + "Could not find that player!";
		}

		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(itemStack)) {
			return RED + "The player already has one of those backpacks!";
		}

		HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(itemStack);

		if (!overflow.isEmpty())
		{
			Location loc = player.getLocation();
			World world = loc.getWorld();

			if ( world != null)
			{
				for (Map.Entry<Integer, ItemStack> e : overflow.entrySet()) {
					ItemStack i = e.getValue();
					world.dropItem(loc, i);
				}
			}
		}

		return "";
	}
}
