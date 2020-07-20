package net.frostbyte.backpacksx.v1_8_R3.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.frostbyte.backpacksx.BackpacksX;
import net.frostbyte.backpacksx.managers.BaseInventoryManager;
import net.frostbyte.backpacksx.packs.Backpack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static org.bukkit.event.inventory.ClickType.NUMBER_KEY;
import static org.bukkit.event.inventory.InventoryAction.HOTBAR_MOVE_AND_READD;
import static org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY;

@SuppressWarnings("unused")
@Singleton
public class InventoryManager extends BaseInventoryManager implements Listener {

	@Inject
	public InventoryManager(BackpacksX plugin) {
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
	public void onInventoryClick(InventoryClickEvent event)
	{
		Inventory destination;
		HumanEntity entity = event.getWhoClicked();

		if (!(entity instanceof Player))
		{
			return;
		}

		Player p = (Player) entity;
		ItemStack checkedItem;
		InventoryView view = event.getView();
		InventoryAction action = event.getAction();

		if (action == MOVE_TO_OTHER_INVENTORY)
		{
			destination = event.getInventory();
		}
		else
		{
			destination = event.getClickedInventory();
		}

		boolean isBackpackInventory = plugin.isBackpackInventory(destination);
		ClickType clickType = event.getClick();

		/* Let the Event Fire If:
			The Destination Inventory isn't a backpack
			The ClickEvent won't add an item to the Destination Inventory
			The Action won't add an item to the Destination Inventory
		 */
		if (
			!isBackpackInventory ||
				!checkedClickTypes.contains(clickType) ||
				!checkedActions.contains(action) ||
				!checkedSlotTypes.contains(event.getSlotType())
		) {
			return;
		}

		// Prevent HotBar Backpack item from being swapped into
		// another backpack
		if (
			clickType == NUMBER_KEY &&
				action == HOTBAR_MOVE_AND_READD
		) {
			int hotBarSlot = event.getHotbarButton();
			checkedItem = p.getInventory().getItem(hotBarSlot);
		}
		else if (cursorActions.contains(action))
		{
			checkedItem = event.getCursor();
		}
		else
		{
			checkedItem = event.getCurrentItem();
		}

		// Prevent Event if the checkedItem is a backpack
		if (plugin.isBackpack(checkedItem))
		{
			event.setCancelled(true);
			sendNestingMessage(p);
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event)
	{
		HumanEntity entity = event.getWhoClicked();

		if (
			entity instanceof Player &&
				draggedBackpack(event)
		) {
			//plugin.getLogger().info("onInventoryDrag.cancellingEvent");
			event.setCancelled(true);
			sendNestingMessage((Player)entity);
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
