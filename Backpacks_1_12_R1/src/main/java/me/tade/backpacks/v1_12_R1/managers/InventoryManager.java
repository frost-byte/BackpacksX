package me.tade.backpacks.v1_12_R1.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.managers.BaseInventoryManager;
import me.tade.backpacks.packs.Backpack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

@SuppressWarnings("unused")
@Singleton
public class InventoryManager extends BaseInventoryManager implements Listener {

	@Inject
	public InventoryManager(Backpacks plugin) {
		super(plugin);
	}

	@EventHandler
	public void onInteractBackpack(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();

		if (event.getItem() != null)
		{
			if (plugin.isBackpack(event.getItem()))
			{
				Backpack backpack = plugin.getBackpack(
					player,
					plugin.getConfigBackpackName(event.getItem())
				);

				if (backpack != null)
				{
					event.setCancelled(true);
					player.openInventory(plugin.getBackpackInventories().get(backpack));
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		Player player = (Player)event.getPlayer();
		List<Backpack> backpacks = plugin.getPlayerBackpacks().get(player.getName());

		if (backpacks != null)
		{
			for (Backpack backpack : backpacks)
			{
				plugin.saveBackpack(player, backpack);
			}
		}
	}
}
