package me.tade.backpacks.managers;

import me.tade.backpacks.Backpacks;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class BaseInventoryManager implements Listener
{
	protected final Backpacks plugin;

	public BaseInventoryManager(Backpacks plugin) { this.plugin = plugin;}

	public abstract void onInteractBackpack(PlayerInteractEvent event);
	public abstract void onInventoryClose(InventoryCloseEvent event);
}
