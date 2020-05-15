package net.frostbyte.backpacksx.util;

import net.frostbyte.backpacksx.BackpacksX;

public interface BinderBridge
{
	void createBridge(BackpacksX plugin);
	void injectMembers(Object o);
	<T> T getInstance(Class<T> var1);
}
