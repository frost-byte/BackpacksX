package me.tade.backpacks.managers;

import me.tade.backpacks.Backpacks;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("unused")
public abstract class BaseLoadingManager implements Listener
{
	protected final Backpacks plugin;

	public BaseLoadingManager(Backpacks plugin) { this.plugin = plugin;}
	public abstract void onJoinServer(PlayerJoinEvent event);
	public abstract void onQuitServer(PlayerQuitEvent event);

}
