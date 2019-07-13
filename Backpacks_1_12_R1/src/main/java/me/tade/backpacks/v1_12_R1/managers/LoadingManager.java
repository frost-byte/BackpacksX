package me.tade.backpacks.v1_12_R1.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.managers.BaseLoadingManager;
import me.tade.backpacks.packs.Backpack;
import me.tade.backpacks.packs.ConfigPack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@Singleton
public class LoadingManager extends BaseLoadingManager implements Listener {

	@Inject
	public LoadingManager(Backpacks plugin)
	{
		super(plugin);
	}

	@EventHandler
	public void onJoinServer(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();

		for (String configName : plugin.getConfigPacks().keySet())
		{
			ConfigPack configPack = plugin.getConfigPacks().get(configName);

			Backpack backpack = plugin.loadBackpack(player, configName);

			if (backpack != null)
			{
				List<Backpack> backpacks = plugin.getPlayerBackpacks().getOrDefault(
					playerName,
					null
				);

				if (backpacks == null)
					backpacks = new ArrayList<>(Collections.singletonList(backpack));
				else
					backpacks.add(backpack);

				plugin.getPlayerBackpacks().put(playerName, backpacks);
			}
		}

		if (plugin.getPluginUpdater().needUpdate()) {
			if (player.isOp() || player.hasPermission("backpack.update.info")) {
				plugin.sendUpdateMessage();
			}
		}
	}

	@EventHandler
	public void onQuitServer(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		List<Backpack> backpacks = plugin.getPlayerBackpacks().get(playerName);

		if (backpacks != null)
		{
			for (Backpack backpack : backpacks)
			{
				plugin.saveBackpack(player, backpack);
			}
		}
	}
}
