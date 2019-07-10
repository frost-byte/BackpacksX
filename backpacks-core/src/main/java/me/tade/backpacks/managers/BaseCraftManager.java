package me.tade.backpacks.managers;

import me.tade.backpacks.Backpacks;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public abstract class BaseCraftManager implements Listener
{
	protected final Backpacks plugin;

	public BaseCraftManager(Backpacks plugin) { this.plugin = plugin;}
	public abstract void onPlayerCraftItem(CraftItemEvent event);
}
