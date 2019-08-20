package me.tade.backpacks.v1_14_R1.commands;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Values;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.commands.BackpacksBaseCommand;
import me.tade.backpacks.packs.ConfigPack;
import me.tade.backpacks.util.StringConstants;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.RED;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("backpack|bp|bpack")
public class BackpacksCommand extends BackpacksBaseCommand
{
	@Inject
	public BackpacksCommand(Backpacks plugin)
	{
		super(plugin);
	}

	@HelpCommand
	public void doHelp(CommandSender sender, CommandHelp help)
	{
		sender.sendMessage(
			StringConstants.HEADER + "§7Plugin by §6The_TadeSK§7, version: §6" +
			plugin.getDescription().getVersion()
		);
		sender.sendMessage(StringConstants.HEADER + " §aVersion §c" + plugin.getDescription().getVersion() + " §ahttps://github.com/frost-byte/Backpack/releases");
		help.showHelp();
	}

	@Description("Reload the configuration.")
	@Subcommand("reload|rl")
	@CommandPermission("backpack.reload")
	public void doReload(CommandSender sender)
	{
		sender.sendMessage(StringConstants.HEADER + " §aReloading..");
		plugin.reloadConfig();
		plugin.reloadBackpacks();
		sender.sendMessage(StringConstants.HEADER + " §aReloaded successfully");
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
			sender.sendMessage(RED + "The player already has one of those backpacks!");
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
			player.sendMessage("§cYou have received a " + pack.getName());
		else
			sender.sendMessage("§cBackpack given to " + player.getName());
	}

	@Description("View the details for a backpack")
	@Subcommand("info|i")
	@Syntax("<pack>")
	@CommandCompletion("@packs")
	public void packInfo(
		CommandSender sender,
		@Values("@packs") ConfigPack pack
	){
		String hasPermission = "";
		String packPermission = "backpack.craft." + pack.getName();

		if (sender instanceof Player) {
			Player player = (Player) sender;
			ConfigPack config = plugin.getConfigPacks().getOrDefault(pack.getName(), null);

			hasPermission = "Permission: ";

			if (config != null)
			{
				hasPermission += (player.hasPermission(packPermission)) ? GREEN + "Yes" : RED + "No";
			}
			else
			{
				hasPermission += RED + "No";
			}
		}

		sender.sendMessage(pack.packInfo() +"\n" + packPermission + ": " + hasPermission);
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
