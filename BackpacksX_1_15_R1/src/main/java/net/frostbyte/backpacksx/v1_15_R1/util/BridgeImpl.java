package net.frostbyte.backpacksx.v1_15_R1.util;

import net.frostbyte.backpacksx.util.VersionBridge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

	private final Map<NamespacedKey, ShapedRecipe> recipes = new HashMap<>();

	@Override
	public boolean registerWithServer()
	{
		return false;
	}

	@Override
	public void registerPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);

		if (!serverHasRecipe(plugin, packName)) {
			registerServerRecipe(plugin, packName);
		}

		if (player.discoverRecipe(lookupKey)) {
			player.sendMessage(
				ChatColor.AQUA + "" +  ChatColor.BOLD + "Discovered " + packName +
					" recipe, log back in use it!"
			);
		}
	}

	public boolean isServerPackRegistered(JavaPlugin plugin, String packName)
	{
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);
		ShapedRecipe recipe = recipes.getOrDefault(lookupKey, null);

		if (recipe != null)
		{
			return !Bukkit.getRecipesFor(recipe.getResult()).isEmpty();
		}

		return false;
	}

	@Override
	public boolean serverHasRecipe(JavaPlugin plugin, String packName)
	{
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);
		List<Recipe> results = new ArrayList<>();
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		ShapedRecipe packRecipe = recipes.getOrDefault(lookupKey, null);

		if (packRecipe == null)
		{
			return false;
		}

		ItemStack backpack = packRecipe.getResult();
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
	public void unregisterPackRecipe(
		JavaPlugin plugin,
		String packName,
		Player player
	) {
	}

	@Override
	public void registerServerRecipe(JavaPlugin plugin, String packName)
	{
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);

		if (!recipes.isEmpty())
		{
			ShapedRecipe recipe = recipes.getOrDefault(lookupKey, null);
			if (recipe != null)
			{
				recipes.put(lookupKey, recipe);

				try
				{
					Bukkit.getServer().addRecipe(recipe);
					plugin.getLogger().info(
						ChatColor.AQUA + "" + ChatColor.BOLD + "Registered Recipe " + packName + " with the Server."
					);
				}
				catch (IllegalStateException ignored)
				{
				}
			}
		}
	}

	@Override
	public void unregisterServerRecipe(JavaPlugin plugin, String packName)
	{
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);
		try
		{
			Bukkit.getServer().removeRecipe(lookupKey);
			plugin.getLogger().info(
				ChatColor.AQUA + "" + ChatColor.BOLD + "Removed Recipe " + packName + " from the Server."
			);
		}
		catch (IllegalStateException ignored) {}
	}

	@Override
	public void updatePackRecipe(
		JavaPlugin plugin,
		String packName,
		ShapedRecipe recipe
	)
	{
		NamespacedKey lookupKey = new NamespacedKey(plugin, packName);

		if (recipe != null)
		{
			recipes.put(lookupKey, recipe);
		}
	}

	@Override
	public ShapedRecipe createShapedRecipe(
		JavaPlugin plugin,
		String packName,
		ItemStack itemStack
	){
		NamespacedKey key = new NamespacedKey(plugin, packName);
		ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
		recipe.setGroup("backpacksx");
		recipes.putIfAbsent(key, recipe);
		//plugin.getLogger().info("Bridge: Creating Shaped Recipe for " + key);
		return recipe;
	}

	@Override
	public ItemStack createItemStack(Material material, int amount, short data)
	{
		return new ItemStack(material, amount);
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
