package me.tade.backpacks.v1_14_R1.commands;

import co.aikar.commands.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.commands.CommandSetup;
import me.tade.backpacks.packs.ConfigPack;

import java.util.logging.Logger;


@Singleton
public class ACFSetup extends CommandSetup
{
	@Inject
	public ACFSetup(
		BukkitCommandManager commandManager,
		Backpacks plugin,
		@Named("Backpacks") Logger logger
	) {
		super(commandManager, plugin, logger);
	}

	/**
	 * Defines Completions which can be referenced by any Command registered with ACF,
	 * which allows the user to use the tab key to provide suggestions or complete
	 * partially typed input
	 */
	@Override
	public ACFSetup registerCommandCompletions()
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
	@Override
	public ACFSetup registerCommandContexts()
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
	 * is to use the "%player" replacement instead of having to change "backpack.player" everywhere. This way you change it
	 * in one spot.
	 */
	@Override
	public ACFSetup registerCommandReplacements()
	{
		// logger.info("ACF Setup: Registering command replacements");

		// CommandReplacements rep = commandManager.getCommandReplacements();

		// Permissions Replacements
		//		rep.addReplacement("%player","backpack.player");
		return this;
	}
}
