package me.tade.backpacks.managers;

import me.tade.backpacks.Backpacks;
import me.tade.backpacks.packs.ConfigPack;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class BaseLoadingManager implements Listener
{
	protected final Backpacks plugin;

	public BaseLoadingManager(Backpacks plugin) { this.plugin = plugin;}
	public abstract void registerPack(ConfigPack pack, Player player);
	public abstract void onJoinServer(PlayerJoinEvent event);
	public abstract void onQuitServer(PlayerQuitEvent event);

}
