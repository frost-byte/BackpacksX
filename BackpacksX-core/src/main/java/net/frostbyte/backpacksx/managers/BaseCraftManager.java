package net.frostbyte.backpacksx.managers;

import net.frostbyte.backpacksx.BackpacksX;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public abstract class BaseCraftManager implements Listener
{
	protected final BackpacksX plugin;

	public BaseCraftManager(BackpacksX plugin) { this.plugin = plugin;}
	public abstract void onPlayerCraftItem(CraftItemEvent event);
}
