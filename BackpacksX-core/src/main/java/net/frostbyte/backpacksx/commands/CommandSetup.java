package net.frostbyte.backpacksx.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.InvalidCommandArgument;
import net.frostbyte.backpacksx.BackpacksX;
import net.frostbyte.backpacksx.packs.ConfigPack;

import java.util.logging.Logger;

public class CommandSetup
{
	private final Logger logger;
	private final BukkitCommandManager commandManager;

	@SuppressWarnings( { "FieldCanBeLocal", "unused" })
	protected final BackpacksX plugin;

	public CommandSetup(BackpacksX plugin)
	{
		this.plugin = plugin;
		this.commandManager = new BukkitCommandManager(plugin);
		this.logger = plugin.getLogger();

		//noinspection deprecation
		commandManager.enableUnstableAPI("help");
	}

	@SuppressWarnings({"UnusedReturnValue", "DanglingJavadoc"})
	/**
	 * Defines Completions which can be referenced by any Command registered with ACF,
	 * 	 * which allows the user to use the tab key to provide suggestions or complete
	 * 	 * partially typed input
	 * @return This instance
	 */
	public CommandSetup registerCommandCompletions()
	{
		logger.info("ACF Setup: Registering command completions");

		CommandCompletions<BukkitCommandCompletionContext> comp = commandManager.getCommandCompletions();
		comp.registerCompletion("packs", c -> plugin.getConfigPacks().keySet());

		return this;
	}

	/**
	 * Provides ACF with contexts for converting string input to commands into
	 * object instances, based upon various annotations, Flags, for example.
	 * Compares the parameters defined for a command and converts the input strings
	 * provided by the command issuer into a relevant object instance.
	 * <p>
	 * Specifying an IssuerAware context allows the CommandSender or initiator of the
	 * command to be treated as an instance of a specific object.
	 */
	public CommandSetup registerCommandContexts()
	{
		logger.info("ACF Setup: Registering command contexts");

		CommandContexts<BukkitCommandExecutionContext> con = commandManager.getCommandContexts();

		// Converts the input string to an integer
		con.registerContext(int.class, c -> {
			int number;

			try
			{
				number = Integer.parseInt(c.popFirstArg());
			}
			catch (NumberFormatException e)
			{
				throw new InvalidCommandArgument("Invalid number format.");
			}

			return number;
		});

		con.registerContext(ConfigPack.class, c -> {
			ConfigPack configPack = null;
			try
			{
				configPack = plugin.getConfigPacks().getOrDefault(c.popFirstArg(), null);

				if (configPack == null)
					throw new InvalidCommandArgument("Invalid Backpack.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return configPack;
		});
		return this;
	}

	/**
	 * Register Replacements with ACF
	 * Defines an alias for a widely used permission or string, that can be referenced when
	 * defining the permissions for a command/subcommands registered with ACF.
	 * The obvious benefit of using this, instead of just referring to them literally in all places,
	 * is to use the "%player" replacement instead of having to change "backpacksx.player" everywhere. This way you change it
	 * in one spot.
	 */
	public CommandSetup registerCommandReplacements()
	{
		// logger.info("Command Setup: Registering command replacements");

		// CommandReplacements rep = commandManager.getCommandReplacements();

		// Permissions Replacements
		//		rep.addReplacement("%player","backpacksx.player");
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public CommandSetup registerCommand(BaseCommand baseCommand)
	{
		commandManager.registerCommand(baseCommand);
		return this;
	}
}
