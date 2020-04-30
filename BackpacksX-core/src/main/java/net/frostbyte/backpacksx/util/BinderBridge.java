package net.frostbyte.backpacksx.util;

import net.frostbyte.backpacksx.Backpacks;

public interface BinderBridge
{
	void createBridge(Backpacks plugin);
	void injectMembers(Object o);
	<T> T getInstance(Class<T> var1);
}
