package me.tade.backpacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import co.aikar.commands.BukkitCommandManager;
import me.tade.backpacks.Metrics.SingleLineChart;

import me.tade.backpacks.commands.BackpacksBaseCommand;
import me.tade.backpacks.commands.CommandSetup;
import me.tade.backpacks.managers.BaseCraftManager;
import me.tade.backpacks.managers.BaseInventoryManager;
import me.tade.backpacks.managers.BaseLoadingManager;
import me.tade.backpacks.packs.Backpack;
import me.tade.backpacks.packs.ConfigPack;
import me.tade.backpacks.util.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static me.tade.backpacks.util.StringConstants.HEADER;

@SuppressWarnings("FieldCanBeLocal")
public class Backpacks extends JavaPlugin {
	/**
	 * Map of Config Names to their Backpack Configuration
	 */
	private HashMap<String, ConfigPack> configPacks = new HashMap<>();

	/**
	 * Map of Player Names to a List of their Backpacks
	 */
	private HashMap<String, List<Backpack>> playerBackpacks = new HashMap<>();

	/**
	 * Map of Backpacks and Inventories
	 */
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

	public void onEnable()
	{
		findVersion();
		getLogger().info("-----------------------------");
		getLogger().info("          Backpacks          ");
		getLogger().info("                             ");
		getLogger().info("     MC Revision: " + VersionManager.getMinecraftRevision());

		if (!registerDependencies())
			return;

		registerListeners();
		registerCommands();
		registerConfig();
		reloadConfig();
		reloadBackpacks();
		registerMetrics();
		createPlayerPacks();
	}

	private boolean registerDependencies() {
		getLogger().info("Registering Dependencies");
		metrics = new Metrics(this);
		pluginUpdater = new PluginUpdater(this);

		// Initialize the ACF Command Manger
		commandManager = new BukkitCommandManager(this);

		try {
			getLogger().info("Binding Version Bridge");
			VersionManager.loadBridge();
		} catch (Exception e) {
			getLogger().warning("Error! Could not load Version Bridge!");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}

		try
		{
			getLogger().info("Binding Module Bridge");
			VersionManager.loadBinderModule(this, getLogger(), commandManager);
		}
		catch (Exception e)
		{
			getLogger().warning("Error! Could not load Binder Bridge!");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}

		VersionManager.injectMembers(this);
		return true;
	}

	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();

		getLogger().info("Registering listeners");
		pm.registerEvents(
			VersionManager.getInstance(BaseInventoryManager.class),
			this
		);
		pm.registerEvents(
			VersionManager.getInstance(BaseCraftManager.class),
			this
		);
		pm.registerEvents(
			VersionManager.getInstance(BaseLoadingManager.class),
			this
		);
	}

	private void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		//noinspection ResultOfMethodCallIgnored
		(new File(getDataFolder().getAbsolutePath() + "/saves")).mkdir();
	}

	private void registerCommands() {
		getLogger().info("Registering Commands");
		//noinspection deprecation
		commandManager.enableUnstableAPI("help");

		CommandSetup setup = VersionManager.getInstance(CommandSetup.class);

		setup
			.registerCommandContexts()
			.registerCommandReplacements()
			.registerCommandCompletions();

		commandManager.registerCommand(VersionManager.getInstance(BackpacksBaseCommand.class));
	}

	private void registerMetrics() {
		getLogger().info("Loading bStats... https://bstats.org/plugin/bukkit/Backpack");
		metrics.addCustomChart(new SingleLineChart("backpacks") {
			public int getValue() {
				return getConfigPacks().size();
			}
		});
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
					List<Backpack> backpacks = getPlayerBackpacks().getOrDefault(playerName, null);

					if (backpacks != null)
					{
						if(!backpacks.contains(backpack))
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

	public void createPlayerPacks(Player player)
	{
		if (player == null || !player.isOnline())
			return;

		String playerName = player.getName();
		BaseLoadingManager loadingManager = VersionManager
			.getInstance(BaseLoadingManager.class);

		for (String configName : getConfigPacks().keySet())
		{
			ConfigPack configPack = getConfigPacks().get(configName);

			if (player.hasPermission("backpack.craft." + configName)) {
				loadingManager.registerPack(configPack, player);

				Backpack backpack = loadBackpack(player, configName);

				if (backpack != null)
				{
					List<Backpack> backpacks = getPlayerBackpacks().getOrDefault(
						playerName,
						null
					);

					if (backpacks == null)
						backpacks = new ArrayList<>(Collections.singletonList(backpack));
					else
					{
						if (!backpacks.contains(backpack))
							backpacks.add(backpack);
					}

					getPlayerBackpacks().put(playerName, backpacks);
				}
			}
		}

		if (getPluginUpdater().needUpdate()) {
			if (player.isOp() || player.hasPermission("backpack.update.info")) {
				sendUpdateMessage();
			}
		}
	}

	public void savePlayerPacks(Player player)
	{
		String playerName = player.getName();
		List<Backpack> backpacks = getPlayerBackpacks().get(playerName);

		if (backpacks != null)
			backpacks.forEach(backpack -> saveBackpack(player, backpack));
	}

	@SuppressWarnings("unused")
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

			if (backpacks == null)
				continue;

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
				material = Material.valueOf(matName);
			}
			catch (Exception e)
			{
				material = Material.matchMaterial(matName);

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
			byte data = (byte)packSection.getInt("item.data");
			item = VersionManager.createItemStack(material, amount, (short) data);

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

	private void addBackpackInventory(
		Player player,
		Backpack backpack,
		ConfigPack configPack
	){
		getBackpackInventories().put(
			backpack,
			Bukkit.createInventory(
				player,
				configPack.getSize(),
				configPack.getItemStack().getItemMeta().getDisplayName()
			)
		);
	}

	private void addPlayerBackpacks(Player player, List<Backpack> backpacks)
	{
		getPlayerBackpacks().put(player.getName(), backpacks);
	}

	public Backpack getBackpack(Player player, String configName) {
		if (player == null || configName.isEmpty())
			return null;
		String playerName = player.getName();
		List<Backpack> packs = getPlayerBackpacks().getOrDefault(playerName, null);

		Backpack backpack;

		if (packs != null && !packs.isEmpty())
		{
			backpack = packs.stream()
				.filter(p -> p.getConfigName().equalsIgnoreCase(configName))
				.findFirst()
				.orElse(null);

			if (backpack != null)
				return backpack;
		}

		ConfigPack configPack = getConfigPacks().getOrDefault(configName, null);
		if (configPack == null)
			return null;

		backpack = new Backpack(
			playerName,
			configPack.getSize(),
			configName,
			new ArrayList<>()
		);

		if (packs == null)
			packs = new ArrayList<>();

		packs.add(backpack);
		addPlayerBackpacks(player, packs);
		addBackpackInventory(player, backpack, configPack);

		return backpack;
	}

	private Backpack loadBackpack(Player player, String configName) {
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
		int i = 0;
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
			if (i < itemStackList.length)
				itemStackList[i++] = itemStack;
		}

		return itemStackList;
	}

	void sendUpdateMessage() {
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

	private PluginUpdater getPluginUpdater() {
		return pluginUpdater;
	}
}
