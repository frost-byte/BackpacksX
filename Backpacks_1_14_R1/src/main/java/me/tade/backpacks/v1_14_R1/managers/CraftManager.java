package me.tade.backpacks.v1_14_R1.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.tade.backpacks.Backpacks;
import me.tade.backpacks.managers.BaseCraftManager;
import me.tade.backpacks.packs.ConfigPack;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static me.tade.backpacks.util.StringConstants.HEADER;

@SuppressWarnings("unused")
@Singleton
public class CraftManager extends BaseCraftManager implements Listener {

	@Inject
	public CraftManager(Backpacks plugin)
	{
		super(plugin);
	}

	@EventHandler
	public void onPlayerCraftItem(CraftItemEvent event)
	{
		if (event.getWhoClicked() instanceof Player)
		{
			Player player = (Player)event.getWhoClicked();
			ItemStack result = event.getInventory().getResult();

			if (result == null)
				return;

			PlayerInventory inventory = player.getInventory();

			ConfigPack pack = plugin.getConfigPacks().values()
				.stream()
				.filter(p -> p.getItemStack().equals(result))
				.findFirst().orElse(null);

			if (pack == null)
				return;

			if (pack.getItemStack().equals(result))
			{
				if (inventory.contains(result)) {
					event.setCancelled(true);
					event.setResult(Result.DENY);
					player.sendMessage(ChatColor.RED + "You can only have one of those backpacks in your inventory!");
					return;
				}

				if (!player.hasPermission("backpack.craft." + pack.getName()))
				{
					event.setCancelled(true);
					event.setResult(Result.DENY);
					player.sendMessage(HEADER + " §cYou do not have permission to create that pack!");
				}
			}
		}
	}
}
