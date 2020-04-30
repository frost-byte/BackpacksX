package net.frostbyte.backpacksx.v1_8_R3.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.frostbyte.backpacksx.Backpacks;
import net.frostbyte.backpacksx.managers.BaseCraftManager;
import net.frostbyte.backpacksx.packs.ConfigPack;
import net.frostbyte.backpacksx.util.StringConstants;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
					player.sendMessage(ChatColor.RED + "You can only have one of those backpacks in your inventory!");
					event.setCancelled(true);
					event.setResult(Result.DENY);
					return;
				}

				if (!player.hasPermission("backpacksx.craft." + pack.getName()))
				{
					event.setCancelled(true);
					event.setResult(Result.DENY);
					player.sendMessage(StringConstants.HEADER + " §cYou do not have permission to create that pack!");
				}
			}
		}
	}
}
