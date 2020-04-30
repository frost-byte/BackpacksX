package net.frostbyte.backpacksx.commands;

import co.aikar.commands.BaseCommand;
import net.frostbyte.backpacksx.Backpacks;
import net.frostbyte.backpacksx.packs.ConfigPack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public abstract class BackpacksBaseCommand extends BaseCommand
{
	protected final Backpacks plugin;

	public BackpacksBaseCommand(Backpacks plugin) { this.plugin = plugin; }

	public abstract void doReload(CommandSender sender);
	public abstract void givePack(CommandSender sender, ConfigPack pack, Player player);
	public abstract void listPacks(CommandSender sender);
}
