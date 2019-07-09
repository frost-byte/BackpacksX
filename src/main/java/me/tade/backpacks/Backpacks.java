package me.tade.backpacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Injector;
import me.tade.backpacks.Metrics.SingleLineChart;

import me.tade.backpacks.packs.Backpack;
import me.tade.backpacks.packs.ConfigPack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import static me.tade.backpacks.commands.BackpacksCommand.HEADER;

@SuppressWarnings("FieldCanBeLocal")
public class Backpacks extends JavaPlugin {
	private HashMap<String, ConfigPack> configPacks = new HashMap<>();
	private HashMap<String, List<Backpack>> playerBackpacks = new HashMap<>();
	private HashMap<Backpack, Inventory> backpackInventories = new HashMap<>();
	private PluginUpdater pluginUpdater;
	private static boolean v1_12;
	private static boolean v1_13;
	private static boolean v1_14;

	private Metrics metrics;

	/**
	 * ACF CommandManager for Bukkit used for registering in game and console commands, contexts, completions, and
	 * replacements
	 *
	 * @see co.aikar.commands.BukkitCommandManager
	 * @see co.aikar.commands.CommandManager
	 */
	private BukkitCommandManager commandManager;

	/**
	 * Guice Injection
	 */
	private Injector injector;

	/**
	 * The reflections for the plugin.
	 */
	private Reflections pluginReflections;

	public void onEnable()
	{
		findVersion();
		getLogger().info("-----------------------------");
		getLogger().info("          Backpacks          ");
		registerDependencies();
		generateReflections();
		registerListeners();
		registerCommands();
		registerConfig();
		reloadBackpacks();
		registerMetrics();
		createPlayerPacks();
	}

	private void registerDependencies() {
		getLogger().info("Registering Dependencies");
		metrics = new Metrics(this);
		pluginUpdater = new PluginUpdater(this);

		// Initialize the ACF Command Manger
		commandManager = new BukkitCommandManager(this);

		// Initialize Dependency Injection
		PluginBinderModule module = new PluginBinderModule(
			this,
			commandManager,
			getLogger(),
			metrics
		);

		injector = module.createInjector();
		injector.injectMembers(this);
	}

	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();

		getLogger().info("Registering listeners");

		Set<Class<? extends Listener>> listeners = pluginReflections.getSubTypesOf(Listener.class);

		listeners.forEach(l -> pm.registerEvents(injector.getInstance(l), this));
	}

	private void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		//noinspection ResultOfMethodCallIgnored
		(new File(getDataFolder().getAbsolutePath() + "/saves")).mkdir();
	}

	private void registerCommands() {
		getLogger().info("Registering Commands");
		//noinspection deprecation
		commandManager.enableUnstableAPI("help");

		ACFSetup acfSetup = injector.getInstance(ACFSetup.class);
		acfSetup.registerCommandContexts();
		acfSetup.registerCommandReplacements();
		acfSetup.registerCommandCompletions();

		Set<Class<? extends BaseCommand>> commands = pluginReflections.getSubTypesOf(BaseCommand.class);
		commands.forEach(c -> commandManager.registerCommand(injector.getInstance(c)));
	}

	private void registerMetrics() {
		getLogger().info("Loading bStats... https://bstats.org/plugin/bukkit/Backpack");
		metrics.addCustomChart(new SingleLineChart("backpacks") {
			public int getValue() {
				return getConfigPacks().size();
			}
		});
	}

	private void generateReflections() {
		// Create Reflections
		getLogger().info("Performing Reflections...");
		pluginReflections = new Reflections("me.tade.backpacks");
	}

	private void createPlayerPacks()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			String playerName = player.getName();
			for (String configName : getConfigPacks().keySet())
			{
				Backpack backpack = loadBackpack(player, configName);
				if (backpack != null)
				{
					List<Backpack> backpacks;
					if (getPlayerBackpacks().containsKey(playerName))
					{
						backpacks = getPlayerBackpacks().get(playerName);
						backpacks.add(backpack);
					}
					else
					{
						backpacks = new ArrayList<>(Collections.singletonList(backpack));
					}
					getPlayerBackpacks().put(playerName, backpacks);
				}
			}
		}
	}

	public static boolean isV1_12() { return v1_12; }

	@SuppressWarnings("unused")
	public static boolean isV1_13()
	{
		return v1_13;
	}

	@SuppressWarnings("unused")
	public static boolean isV1_14()
	{
		return v1_14;
	}

	private void findVersion() {
		String version = Bukkit.getBukkitVersion(); // Returns in format like: 1.12.2-R0.1-SNAPSHOT
		v1_14 = version.startsWith("1.14");
		v1_13 = version.startsWith("1.13");
		v1_12 = version.startsWith("1.12") || (!v1_14 && !v1_13);
	}

	public void onDisable() {

		for (Player player : Bukkit.getOnlinePlayers())
		{
			String playerName = player.getName();
			List<Backpack> backpacks = getPlayerBackpacks().getOrDefault(playerName, null);
			for (Backpack pack : backpacks) {
				saveBackpack(player, pack);
			}
		}
	}

	public void reloadBackpacks() {
		configPacks.clear();
		ConfigurationSection section = getConfig().getConfigurationSection("backpacks");

		if (section == null)
			return;
		Set<String> packNames = section.getKeys(false);
		getLogger().info("Found " + packNames.size() + " packs.");

		for (String name : packNames) {
			getLogger().info("Loading Config for Backpack named " + name);
			ConfigurationSection packSection = section.getConfigurationSection(name);

			if (packSection == null)
				continue;

			int size = packSection.getInt("size");
			List<String> recipe = packSection.getStringList("recipe");

			String matName = packSection.getString("item.material");

			if (matName == null || matName.isEmpty())
				continue;

			Material material;
			try
			{
				org.bukkit.Material.matchMaterial(matName, false);
				material = Material.valueOf(matName);
			}
			catch (Exception e)
			{
				material = Material.matchMaterial(matName, false);

				if (material == null) {
					e.printStackTrace();
					continue;
				}
			}

			int amount = packSection.getInt("item.amount");
			String itemName = packSection.getString("item.name");
			List<String> configLore = packSection.getStringList("item.lore");
			List<String> lore = new ArrayList<>();

			for (String line : configLore)
			{
				lore.add(ChatColor.translateAlternateColorCodes('&', line));
			}

			ItemStack item;
			if (v1_12)
			{
				byte data = (byte)packSection.getInt("item.data");
				//noinspection deprecation
				item = new ItemStack(material, amount, (short) data);
			}
			else {
				item = new ItemStack(material, amount);
			}

			ItemMeta meta = item.getItemMeta();

			if (itemName != null && !itemName.isEmpty())
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));

			meta.setLore(lore);
			item.setItemMeta(meta);
			configPacks.put(name, new ConfigPack(this, name, size, recipe, item));
		}
	}

	public HashMap<String, ConfigPack> getConfigPacks() {
		return configPacks;
	}

	public HashMap<String, List<Backpack>> getPlayerBackpacks() {
		return playerBackpacks;
	}

	public HashMap<Backpack, Inventory> getBackpackInventories() {
		return backpackInventories;
	}

	public boolean isBackpack(ItemStack itemStack) {
		for (ConfigPack pack : getConfigPacks().values()) {
			if(pack.getItemStack().equals(itemStack))
				return true;
		}

		return false;
	}

	public String getConfigBackpackName(ItemStack itemStack) {
		for(ConfigPack pack : getConfigPacks().values()) {
			if (pack.getItemStack().equals(itemStack))
				return pack.getName();
		}

		return null;
	}

	public Backpack getBackpack(Player player, String configName) {
		if (player == null || configName.isEmpty())
			return null;
		String playerName = player.getName();
		List<Backpack> packs = getPlayerBackpacks().getOrDefault(playerName, null);

		if (packs != null && !packs.isEmpty()) {
			for (Backpack backpack : packs) {
				if (backpack.getConfigName().equalsIgnoreCase(configName)) {
					return backpack;
				}
			}
		}

		ConfigPack configPack = getConfigPacks().getOrDefault(configName, null);
		if (configPack == null)
			return null;

		Backpack backpack = new Backpack(playerName, configPack.getSize(), configName, new ArrayList<>());

		if (packs == null)
			packs = new ArrayList<>();

		packs.add(backpack);
		getPlayerBackpacks().put(playerName, packs);

		getBackpackInventories().put(
			backpack,
			Bukkit.createInventory(
				player,
				configPack.getSize(),
				configPack.getItemStack().getItemMeta().getDisplayName()
			)
		);

		return backpack;
	}

	public Backpack loadBackpack(Player player, String configName) {
		File dir = new File(getDataFolder(), "/saves/" + player.getUniqueId().toString() + "=" + configName + ".backpack");

		if (!dir.exists())
		{
			return null;
		}
		else
		{
			FileInputStream fis = null;

			try
			{
				fis = new FileInputStream(dir.getAbsolutePath());
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

			if (fis == null)
				return null;

			GZIPInputStream gs = null;
			ObjectInputStream ois = null;

			try {
				gs = new GZIPInputStream(fis);
				ois = new ObjectInputStream(gs);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (ois == null)
				return null;

			Backpack backpack = null;

			try
			{
				backpack = (Backpack)ois.readObject();
			}
			catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			try {
				ois.close();
				gs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (backpack == null)
				return null;

			ConfigPack configPack = getConfigPacks().getOrDefault(backpack.getConfigName(), null);
			if (configPack == null)
				return null;

			Inventory inventory = Bukkit.createInventory(
				player,
				configPack.getSize(),
				configPack.getItemStack().getItemMeta().getDisplayName()
			);

			inventory.setContents(deserializeItemStackList(backpack.getItems()));
			getBackpackInventories().put(backpack, inventory);

			return backpack;
		}
	}

	public void saveBackpack(Player player, Backpack backpack)
	{
		File dir = new File(getDataFolder(), "/saves/" + player.getUniqueId().toString() + "=" + backpack.getConfigName() + ".backpack");

		if (!dir.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
				dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		backpack.setItems(serializeItemStackList(getBackpackInventories().get(backpack).getContents()));
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(dir.getAbsoluteFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (fos == null)
			return;

		GZIPOutputStream gz;
		ObjectOutputStream oos = null;

		try
		{
			gz = new GZIPOutputStream(fos);
			oos = new ObjectOutputStream(gz);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (oos == null)
			return;

		try
		{
			oos.writeObject(backpack);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			oos.flush();
			oos.close();
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private List<HashMap<Map<String, Object>, Map<String, Object>>> serializeItemStackList(ItemStack[] itemStackList)
	{
		List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList = new ArrayList<>();

		for (ItemStack original : itemStackList)
		{
			ItemStack newStack = original == null ? new ItemStack(Material.AIR) : original.clone();
			HashMap<Map<String, Object>, Map<String, Object>> serializedMap = new HashMap<>();
			Map<String, Object> serializedItemMeta = newStack.hasItemMeta() ? newStack.getItemMeta().serialize() : null;
			newStack.setItemMeta(null);
			Map<String, Object> serializedItemStack = newStack.serialize();
			serializedMap.put(serializedItemStack, serializedItemMeta);
			serializedItemStackList.add(serializedMap);
		}

		return serializedItemStackList;
	}

	private ItemStack[] deserializeItemStackList(List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList) {
		ItemStack[] itemStackList = new ItemStack[serializedItemStackList.size()];

		ItemStack itemStack;
		for (HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackMap : serializedItemStackList) {
			Entry<Map<String, Object>, Map<String, Object>> serializedItemStack = serializedItemStackMap.entrySet().iterator().next();
			itemStack = ItemStack.deserialize(serializedItemStack.getKey());

			if (serializedItemStack.getValue() != null) {
				ItemMeta itemMeta = (ItemMeta)ConfigurationSerialization.deserializeObject(
					serializedItemStack.getValue(),
					Objects.requireNonNull(ConfigurationSerialization.getClassByAlias("ItemMeta"))
				);
				itemStack.setItemMeta(itemMeta);
			}
		}

		return itemStackList;
	}

	public void sendUpdateMessage() {
		(new BukkitRunnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers())
				{
					if (player.isOp() || player.hasPermission("backpack.update"))
					{
						player.sendMessage(" ");
						player.sendMessage(
							HEADER + " §aA new update has come! Released on §e" +
							pluginUpdater.getUpdateInfo()[1]
						);
						player.sendMessage(
							HEADER + " §aNew version number/your current version §e" +
							pluginUpdater.getUpdateInfo()[0] + "§7/§c" +
							getDescription().getVersion()
						);
						player.sendMessage(
							HEADER + " §aDownload update here: §ehttps://github.com/frost-byte/Backpack/releases"
						);
					}
				}
			}
		}).runTaskLater(this, 20L);
	}

	public PluginUpdater getPluginUpdater() {
		return pluginUpdater;
	}
}
