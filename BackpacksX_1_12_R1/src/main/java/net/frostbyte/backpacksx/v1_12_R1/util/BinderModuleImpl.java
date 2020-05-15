package net.frostbyte.backpacksx.v1_12_R1.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import net.frostbyte.backpacksx.BackpacksX;
import net.frostbyte.backpacksx.managers.BaseCraftManager;
import net.frostbyte.backpacksx.managers.BaseInventoryManager;
import net.frostbyte.backpacksx.managers.BaseLoadingManager;
import net.frostbyte.backpacksx.util.BinderBridge;
import net.frostbyte.backpacksx.v1_12_R1.managers.CraftManager;
import net.frostbyte.backpacksx.v1_12_R1.managers.InventoryManager;
import net.frostbyte.backpacksx.v1_12_R1.managers.LoadingManager;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public class BinderModuleImpl implements BinderBridge
{
	private Injector injector;

	public BinderModuleImpl() {}

	@Override
	public void createBridge(BackpacksX plugin)
	{
		checkNotNull(plugin, "The plugin instance cannot be null.");

		injector = new AbstractModule()
		{
			@Override
			protected void configure()
			{
				bind(BackpacksX.class).toInstance(plugin);
				bind(BaseCraftManager.class).to(CraftManager.class);
				bind(BaseLoadingManager.class).to(LoadingManager.class);
				bind(BaseInventoryManager.class).to(InventoryManager.class);
				bind(Logger.class)
					.annotatedWith(Names.named("Backpacks"))
					.toInstance(plugin.getLogger());
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
