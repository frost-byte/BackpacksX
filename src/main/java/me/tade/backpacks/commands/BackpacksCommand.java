package me.tade.backpacks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.packs.ConfigPack;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("backpack|bp|bpack")
public class BackpacksCommand extends BaseCommand
{
	private final Backpacks plugin;
	public static final String HEADER = "§e[§lBackpack]§e  ";
	private static final String ADDED_EMPTY_LINE = HEADER + "§aAdded empty line!";
	private static final String ADDED_LINE = HEADER + "§aAdded line '%s'!";
	private static final String ERROR_ALREADY_USED = HEADER + "§cThis name is already in use!";
	private static final String ERROR_DOES_NOT_EXIST = HEADER + "§That backpack does not exist!";
	private static final String ERROR_FAILED = HEADER + "§cOperation failed!";
	private static final String ERROR_FILE_CREATION = HEADER + "§cAn error occurred while creating the file!";
	private static final String ERROR_INVALID_NUMBER = HEADER + "§cThis number is not valid! Use 1, 2, 3, ...";
	private static final String ERROR_NO_PERMISSION = HEADER + "§cYou do not have permission to use that backpack!";


	@Inject
	public BackpacksCommand(Backpacks plugin) {
		this.plugin = plugin;
	}

	@HelpCommand
	public void doHelp(CommandSender sender, CommandHelp help)
	{
		sender.sendMessage(
			HEADER + "§7Plugin by §6The_TadeSK§7, version: §6" +
			plugin.getDescription().getVersion()
		);
		sender.sendMessage(HEADER + " §aVersion §c" + plugin.getDescription().getVersion() + " §ahttps://github.com/frost-byte/Backpack/releases");
		help.showHelp();
	}

	@Description("Reload the configuration.")
	@Subcommand("reload|rl")
	@CommandPermission("backpack.reload")
	public void doReload(CommandSender sender)
	{
		sender.sendMessage(HEADER + " §aReloading..");
		plugin.reloadConfig();
		plugin.reloadBackpacks();
		sender.sendMessage(HEADER + " §aReloaded successfully");
	}

	// /bp give <pack_name> <player_name>
	@Description("Give a backpack to a player")
	@Subcommand("give|g")
	@CommandPermission("backpack.give")
	@CommandCompletion("@packs @players")
	public void givePack(
		CommandSender sender,
		@Values("@packs") ConfigPack pack,
		@Optional @Flags("other") Player player
	){
		String playerName = "";
		boolean sendGiftMessage = false;

		if (player == null) {
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			else
			{
				sender.sendMessage("Error! Could not give a backpack to that player!");
				return;
			}
		}
		else
		{
			sendGiftMessage = true;
		}

		ItemStack item = pack.getItemStack();
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(item)) {
			sender.sendMessage(ChatColor.RED + "The player already has one of those backpacks!");
			return;
		}

		HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(pack.getItemStack());

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

		if (sendGiftMessage)
			player.sendMessage("§cYou have received a " + item.getI18NDisplayName());
		else
			sender.sendMessage("§cBackpack given to " + player.getName());
	}

	// List packs
	@Description("View all available backpacks.")
	@Subcommand("list|ls")
	@CommandPermission("backpack.list")
	public void listPacks(CommandSender sender)
	{
		sender.sendMessage("§cAll Loaded Backpacks:");

		plugin.getConfigPacks().values().stream().map(ConfigPack::packInfo).forEach(sender::sendMessage);
	}
}
