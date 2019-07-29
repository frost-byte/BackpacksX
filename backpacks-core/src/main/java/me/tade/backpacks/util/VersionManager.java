package me.tade.backpacks.util;

import co.aikar.commands.BukkitCommandManager;
import me.tade.backpacks.Backpacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class VersionManager
{
	private static VersionBridge BRIDGE;
	private static String MINECRAFT_REVISION;
	private static BinderBridge BINDER;
	private VersionManager() {

	}

	public static void loadBridge() throws Exception {

		String className = "me.tade.backpacks.v" + getMinecraftRevision() + ".util.BridgeImpl";
		BRIDGE = (VersionBridge) Class.forName(className).getConstructor().newInstance();
	}

	public static void loadBinderModule(
		Backpacks plugin,
		Logger logger,
		BukkitCommandManager commandManager
	) throws Exception
	{
		String className = "me.tade.backpacks.v" + getMinecraftRevision() + ".util.BinderModuleImpl";
		BINDER = (BinderBridge) Class.forName(className).getConstructor().newInstance();
		BINDER.createBridge(plugin, logger, commandManager);
	}

	public static <T> T getInstance(Class<T> var1)
	{
		return BINDER.getInstance(var1);
	}

	public static ShapedRecipe createShapedRecipe(JavaPlugin plugin, String name, ItemStack itemStack)
	{
		return BRIDGE.createShapedRecipe(plugin, name, itemStack);
	}

	public static ItemStack createItemStack(Material material, int amount, short data)
	{
		return BRIDGE.createItemStack(material, amount, data);
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
