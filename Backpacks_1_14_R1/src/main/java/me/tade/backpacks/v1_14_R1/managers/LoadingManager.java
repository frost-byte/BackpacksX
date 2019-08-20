package me.tade.backpacks.v1_14_R1.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.managers.BaseLoadingManager;
import me.tade.backpacks.packs.ConfigPack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("unused")
@Singleton
public class LoadingManager extends BaseLoadingManager implements Listener {

	@Inject
	public LoadingManager(Backpacks plugin)
	{
		super(plugin);
	}

	@Override
	public void registerPack(ConfigPack pack, Player player)
	{
		player.discoverRecipe(new NamespacedKey(plugin, pack.getName()));
		try
		{
			Bukkit.getServer().addRecipe(pack.getShapedRecipe());
		}
		catch (IllegalStateException ex)
		{
			plugin.getLogger().warning("Backpack " + pack.getName() + " already registered, ignoring.");
		}
	}

	@EventHandler
	public void onJoinServer(PlayerJoinEvent event)	{
		plugin.createPlayerPacks(event.getPlayer());
	}

	@EventHandler
	public void onQuitServer(PlayerQuitEvent event) {
		plugin.savePlayerPacks(event.getPlayer());
	}
}
