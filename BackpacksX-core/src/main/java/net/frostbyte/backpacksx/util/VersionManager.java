package net.frostbyte.backpacksx.util;

import net.frostbyte.backpacksx.Backpacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class VersionManager
{
	private static VersionBridge BRIDGE;
	private static String MINECRAFT_REVISION;
	private static BinderBridge BINDER;
	private static final String PACKAGE_HEADER = "net.frostbyte.backpacksx.v";

	private VersionManager() {}

	public static void loadBridge() throws Exception {

		String className = PACKAGE_HEADER + getMinecraftRevision() + ".util.BridgeImpl";
		BRIDGE = (VersionBridge) Class.forName(className).getConstructor().newInstance();
	}

	public static void loadBinderModule(Backpacks plugin) throws Exception
	{
		String className = PACKAGE_HEADER + getMinecraftRevision() + ".util.BinderModuleImpl";
		BINDER = (BinderBridge) Class.forName(className).getConstructor().newInstance();
		BINDER.createBridge(plugin);
	}

	public static <T> T getInstance(Class<T> var1)
	{
		return BINDER.getInstance(var1);
	}

	public static ShapedRecipe createShapedRecipe(JavaPlugin plugin, String name, ItemStack itemStack)
	{
		return BRIDGE.createShapedRecipe(plugin, name, itemStack);
	}

	public static void registerPackRecipe(JavaPlugin plugin, String packName, Player player)
	{
		BRIDGE.registerPackRecipe(plugin, packName, player);
	}

	public static void unregisterPackRecipe(JavaPlugin plugin, String packName, Player player)
	{
		BRIDGE.unregisterPackRecipe(plugin, packName, player);
	}

	public static void updatePackRecipe(JavaPlugin plugin, String packName, ShapedRecipe recipe)
	{
		BRIDGE.updatePackRecipe(plugin, packName, recipe);
	}

	public static ItemStack createItemStack(Material material, int amount, short data)
	{
		return BRIDGE.createItemStack(material, amount, data);
	}

	public static String givePack(JavaPlugin plugin, UUID playerID, ItemStack itemStack)
	{
		return BRIDGE.givePack(plugin, playerID, itemStack);
	}

	public static void injectMembers(Object o)
	{
		BINDER.injectMembers(o);
	}

	public static String getMinecraftRevision() {
		if (MINECRAFT_REVISION == null) {
			MINECRAFT_REVISION = Bukkit.getServer().getClass().getPackage().getName();
		}
		return MINECRAFT_REVISION.substring(MINECRAFT_REVISION.lastIndexOf('.') + 2);
	}
}
