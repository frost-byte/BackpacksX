package me.tade.backpacks.v1_14_R1.util;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.commands.BackpacksBaseCommand;
import me.tade.backpacks.commands.CommandSetup;
import me.tade.backpacks.managers.BaseCraftManager;
import me.tade.backpacks.managers.BaseInventoryManager;
import me.tade.backpacks.managers.BaseLoadingManager;
import me.tade.backpacks.util.BinderBridge;
import me.tade.backpacks.v1_14_R1.commands.ACFSetup;
import me.tade.backpacks.v1_14_R1.commands.BackpacksCommand;
import me.tade.backpacks.v1_14_R1.managers.CraftManager;
import me.tade.backpacks.v1_14_R1.managers.InventoryManager;
import me.tade.backpacks.v1_14_R1.managers.LoadingManager;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public class BinderModuleImpl implements BinderBridge
{
	private Injector injector;

	public BinderModuleImpl() {}

	@Override
	public void createBridge(
		Backpacks plugin,
		Logger logger,
		BukkitCommandManager commandManager
	)
	{
		checkNotNull(plugin, "The plugin instance cannot be null.");
		checkNotNull(commandManager, "The command manager cannot be null.");
		checkNotNull(logger, "The logger cannot be null.");

		injector = new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(Backpacks.class).toInstance(plugin);
				bind(BaseCraftManager.class).to(CraftManager.class).in(Singleton.class);
				bind(BaseLoadingManager.class).to(LoadingManager.class).in(Singleton.class);
				bind(BaseInventoryManager.class).to(InventoryManager.class).in(Singleton.class);
				bind(BackpacksBaseCommand.class).to(BackpacksCommand.class).in(Singleton.class);
				bind(CommandSetup.class).to(ACFSetup.class).in(Singleton.class);
				bind(BukkitCommandManager.class).toInstance(commandManager);
				bind(Logger.class)
					.annotatedWith(Names.named("Backpacks"))
					.toInstance(logger);
			}

			/**
			 * Generate the Guice Injector for this Module
			 *
			 * @return The guice injector used to retrieve bound instances and create new instances based upon the
			 * implementations bound to their specified contract class
			 */
			Injector createInjector()
			{
				injector = Guice.createInjector(this);
				return injector;
			}

		}.createInjector();
		injector.injectMembers(plugin);
	}

	@Override
	public void injectMembers(Object o)
	{
		injector.injectMembers(o);
	}

	@Override
	public <T> T getInstance(Class<T> var1)
	{
		return injector.getInstance(var1);
	}
}
