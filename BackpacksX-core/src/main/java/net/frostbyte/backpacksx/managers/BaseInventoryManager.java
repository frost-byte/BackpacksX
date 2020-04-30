package net.frostbyte.backpacksx.managers;

import net.frostbyte.backpacksx.Backpacks;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@SuppressWarnings("unused")
public abstract class BaseInventoryManager implements Listener
{
	protected final Backpacks plugin;

	public BaseInventoryManager(Backpacks plugin) { this.plugin = plugin;}

	public abstract void onInteractBackpack(PlayerInteractEvent event);
	public abstract void onInventoryClose(InventoryCloseEvent event);
}
