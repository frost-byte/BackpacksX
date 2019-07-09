package me.tade.backpacks;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;


import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public class PluginBinderModule extends AbstractModule
{
	private Backpacks plugin;
	private final BukkitCommandManager commandManager;
	private final Metrics metrics;

	/**
	 * The Plugin's Logger
	 */
	private final Logger logger;

	PluginBinderModule(
		Backpacks plugin,
		BukkitCommandManager commandManager,
		Logger logger,
		Metrics metrics
	) {
		this.plugin = checkNotNull(plugin, "The plugin instance cannot be null.");
		this.commandManager = checkNotNull(commandManager, "The command manager cannot be null.");
		this.logger = checkNotNull(logger, "The logger cannot be null.");
		this.metrics = checkNotNull(metrics, "The metrics cannot be null.");
	}

	/**
	 * Generate the Guice Injector for this Module
	 *
	 * @return The guice injector used to retrieve bound instances and create new instances
	 * based upon the implementations bound to their specified contract class
	 */
	Injector createInjector()
	{
		return Guice.createInjector(this);
	}

	/**
	 * Configure the Injections and Bindings for our Guice Module
	 * Binds classes to specific instances.
	 * Creates the Configuration Factory and associated implementations
	 */
	@Override
	protected void configure() {
		bind(Backpacks.class).toInstance(plugin);
		bind(BukkitCommandManager.class).toInstance(commandManager);
		bind(Metrics.class)
			.annotatedWith(Names.named("BP_Metrics"))
			.toInstance(metrics);
		bind(Logger.class)
			.annotatedWith(Names.named("Backpacks"))
			.toInstance(logger);
	}
}
