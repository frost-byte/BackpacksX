package me.tade.backpacks.commands;

import co.aikar.commands.BukkitCommandManager;
import me.tade.backpacks.Backpacks;

import java.util.logging.Logger;

public abstract class CommandSetup
{
	protected final BukkitCommandManager commandManager;
	protected final Logger logger;

	@SuppressWarnings( { "FieldCanBeLocal", "unused" })
	protected final Backpacks plugin;

	public CommandSetup(
		BukkitCommandManager commandManager,
		Backpacks plugin,
		Logger logger
	)
	{
		this.commandManager = commandManager;
		this.plugin = plugin;
		this.logger = logger;
	}

	public abstract CommandSetup registerCommandCompletions();
	public abstract CommandSetup registerCommandContexts();
	public abstract CommandSetup registerCommandReplacements();
}
