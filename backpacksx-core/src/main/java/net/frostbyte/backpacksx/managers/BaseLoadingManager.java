package net.frostbyte.backpacksx.managers;

import net.frostbyte.backpacksx.BackpacksX;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("unused")
public abstract class BaseLoadingManager implements Listener
{
	protected final BackpacksX plugin;

	public BaseLoadingManager(BackpacksX plugin) { this.plugin = plugin;}
	public abstract void onJoinServer(PlayerJoinEvent event);
	public abstract void onQuitServer(PlayerQuitEvent event);

}
