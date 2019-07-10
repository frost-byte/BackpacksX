package me.tade.backpacks.util;

import co.aikar.commands.BukkitCommandManager;
import me.tade.backpacks.Backpacks;

import java.util.logging.Logger;

public interface BinderBridge
{
	void createBridge(Backpacks plugin, Logger logger, BukkitCommandManager commandManager);
	void injectMembers(Object o);
	<T> T getInstance(Class<T> var1);
}
