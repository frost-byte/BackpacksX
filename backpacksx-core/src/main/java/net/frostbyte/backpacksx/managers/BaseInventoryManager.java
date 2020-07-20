package net.frostbyte.backpacksx.managers;

import net.frostbyte.backpacksx.BackpacksX;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.bukkit.event.inventory.ClickType.*;
import static org.bukkit.event.inventory.InventoryAction.*;
import static org.bukkit.event.inventory.InventoryType.SlotType.CONTAINER;
import static org.bukkit.event.inventory.InventoryType.SlotType.QUICKBAR;

@SuppressWarnings("unused")
public abstract class BaseInventoryManager implements Listener
{
	protected final BackpacksX plugin;
	protected final List<ClickType> checkedClickTypes = new ArrayList<>(
		Arrays.asList(
			LEFT,
			RIGHT,
			SHIFT_LEFT,
			SHIFT_RIGHT,
			MIDDLE,
			NUMBER_KEY,
			DOUBLE_CLICK
		)
	);

	protected final List<InventoryAction> checkedActions = new ArrayList<>(
		Arrays.asList(
			MOVE_TO_OTHER_INVENTORY,
			PLACE_ALL,
			PLACE_ONE,
			PLACE_SOME,
			SWAP_WITH_CURSOR,
			HOTBAR_MOVE_AND_READD
		)
	);

	protected final List<InventoryAction> cursorActions = new ArrayList<>(
		Arrays.asList(
			PLACE_ALL,
			PLACE_ONE,
			PLACE_SOME
		)
	);

	protected final List<InventoryType.SlotType> checkedSlotTypes = new ArrayList<>(
		Arrays.asList(
			QUICKBAR,
			CONTAINER
		)
	);

	public BaseInventoryManager(BackpacksX plugin) { this.plugin = plugin;}

	public abstract void onInteractBackpack(PlayerInteractEvent event);
	public abstract void onInventoryClick(InventoryClickEvent event);
	public abstract void onInventoryDrag(InventoryDragEvent event);
	public abstract void onInventoryClose(InventoryCloseEvent event);

	protected boolean draggedBackpack(InventoryDragEvent event) {
		int topSize = event.getView().getTopInventory().getSize();
		boolean hasBackpackSlot = false;

		// If any dragged over slot is in the Top Inventory (the backpack's), then a backpack slot was dragged over
		// and the event should be cancelled, if the item being dragged is a backpack.
		for (int slot : event.getRawSlots())
		{
			if (slot < topSize)
			{
				hasBackpackSlot = true;
				break;
			}
		}

		/* Let the Event Fire if the Destination Inventory isn't a backpack	 */
		return (
			hasBackpackSlot &&
			plugin.isBackpackInventory(event.getInventory()) &&
			plugin.isBackpack(event.getOldCursor())
		);
	}

	protected void sendNestingMessage(Player p)
	{
		p.sendMessage(ChatColor.RED + "You cannot place one backpack inside of another.!");
	}
}
