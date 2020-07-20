package net.frostbyte.backpacksx.commands;

import co.aikar.commands.BaseCommand;

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
import net.frostbyte.backpacksx.BackpacksX;
import net.frostbyte.backpacksx.packs.ConfigPack;
import net.frostbyte.backpacksx.util.StringConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.RED;

@CommandAlias("backpacksx|bpx|bpax")
public class BackpacksCommand extends BaseCommand
{
	protected final BackpacksX plugin;

	public BackpacksCommand(BackpacksX plugin) { this.plugin = plugin; }

	@HelpCommand
	public void doHelp(CommandSender sender, CommandHelp help)
	{
		sender.sendMessage(
			StringConstants.VERSION_DESCRIPTION +
				plugin.getDescription().getVersion()
		);
		sender.sendMessage(StringConstants.HEADER + " §aVersion §c" + plugin.getDescription().getVersion() + " §ahttps://github.com/frost-byte/BackpacksX/releases");
		help.showHelp();
	}

	@Description("Reload the configuration.")
	@Subcommand("reload|rl")
	@CommandPermission("backpacksx.reload")
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
	@CommandPermission("backpacksx.give")
	@CommandCompletion("@packs @players")
	public void givePack(
		CommandSender sender,
		@Values("@packs") ConfigPack pack,
		@Optional @Flags("other") Player player
	){
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

		String giftMessage = plugin.givePack(player.getUniqueId(), pack.getItemStack());

		if (!giftMessage.isEmpty()) {
			sender.sendMessage(giftMessage);
			return;
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
		String packPermission = "backpacksx.craft." + pack.getName();

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
	@CommandPermission("backpacksx.list")
	public void listPacks(CommandSender sender)
	{
		sender.sendMessage("§cAll Loaded BackpacksX:");

		plugin.getConfigPacks().values().stream().map(ConfigPack::packInfo).forEach(sender::sendMessage);
	}
}
