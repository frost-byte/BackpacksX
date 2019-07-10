package me.tade.backpacks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.HelpCommand;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.packs.ConfigPack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BackpacksBaseCommand extends BaseCommand
{
	protected final Backpacks plugin;

	public BackpacksBaseCommand(Backpacks plugin) { this.plugin = plugin; }

	@HelpCommand
	public abstract void doHelp(CommandSender sender, CommandHelp help);
	public abstract void doReload(CommandSender sender);
	public abstract void givePack(CommandSender sender, ConfigPack pack, Player player);
	public abstract void listPacks(CommandSender sender);
}
