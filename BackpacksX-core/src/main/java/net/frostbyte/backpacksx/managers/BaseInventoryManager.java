package net.frostbyte.backpacksx.managers;

import net.frostbyte.backpacksx.BackpacksX;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@SuppressWarnings("unused")
public abstract class BaseInventoryManager implements Listener
{
	protected final BackpacksX plugin;

	public BaseInventoryManager(BackpacksX plugin) { this.plugin = plugin;}

	public abstract void onInteractBackpack(PlayerInteractEvent event);
	public abstract void onInventoryClose(InventoryCloseEvent event);
}
