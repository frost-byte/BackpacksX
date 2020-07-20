## Preventing Nesting of Backpacks

### Clicking
Stacks can be grabbed by pressing left-click on the stack and split by pressing right-click.
If the stack is an odd quantity, then it leaves the smaller half (for example, a stack of 7 leaves 3 items
in the slot and grabs 4).

Conversely, while holding a stack of items in the inventory, left-clicking places the full stack in the
slot, and right-clicking place just one item.

#### Shift-Click
**⇧ Shift Clicking** on the item sends the item from the player's hotbar to the main 27 slots, or
vice versa. Doing so, while a block's inventory (chest, dispenser, etc.) is open, sends the item stack
immediately from the player's inventory into the block that they have open, or vice versa if the player held
shift while clicking on an item in the block's inventory.


#### Double Clicking
**Double-clicking** an item grabs up to a full stack from all the items within the inventory.

#### Shift Double-Clicking
**⇧ Shift double-clicking** a stack moves all items of that kind between the player's inventory and
the block, or their inventory and their hotbar. However, the player's cursor must already be holding an item
for this to work. The item held is irrelevant.

### HotBar/QuickAction
Pressing 1–9 switches the item hovered over with the cursor with the item in that slot of the hotbar, or
moves the item from one to the other if either slot is empty.

### Dragging
While an item stack is grabbed, dragging items around with the left mouse button divide the stack equally
over the dragged-over slots, while dragging item stacks around with the right mouse button places one item of
that stack into each dragged-over slot — in both cases, only if the slot is empty or contains the same item
being dragged.

### Creative Mode
In Creative mode, middle-clicking an item grabs a full stack of the item while leaving the item in the slot,
and dragging items with the middle mouse button places a full stack of the item in each dragged-over slot.

Pressing C+1-9 in Creative mode saves the current toolbar in the Saved Toolbars tab.
It can be loaded with X+1-9.

### Dropping Items
Pressing drop while the cursor is hovering over an item in the inventory or a container drops one item.
Holding Ctrl and pressing drop while hovering over a slot in the inventory or container that has one or more
items drops the entire stack of items. While venturing without the full inventory open, the same can be
applied for the selected item(s) from the hotbar.

Items can also be dropped into the world by clicking outside the inventory window while holding an item with
the cursor.

## Preventing Nesting of Backpacks

### Events
#### InventoryClickEvent
#### InventoryDragEvent

### Scenarios to Prevent
- [ ] Shift-Clicking/Shift-Double-Clicking on a Backpack inside the players inventory, causing it to be
    placed it into the inventory of a Backpack.
- [ ] Shift-Clicking/Shift-Double-Clicking on a Backpack in a Hot Bar slot to place it into the inventory of a Backpack.
- [ ] Left/Right clicking on the inventory of a backpack, while another backpack is on the cursor
- [ ] Pressing 1-9 while hovering over a slot in the backpack inventory, if the corresponding Hot Bar slot
    contains a backpack.
- [ ] Dragging while holding the left or right mouse button, while the cursor contains a backpack, and the
    player is dragging over the inventory of a backpack.

### Slot Types
    Represents the type of inventory slot that was clicked or moused-over with the cursor.

#### CONTAINER
    A regular slot in the container or the player's inventory; anything not covered by the other enum values.
####QUICKBAR

### ClickTypes
####DOUBLE_CLICK
    Pressing the left mouse button twice in quick succession.
####LEFT
    The left (or primary) mouse button.
####MIDDLE
    The middle mouse button, or a "scrollwheel click".
####NUMBER_KEY
    One of the number keys 1-9, correspond to slots on the hotbar.
####RIGHT
    The right mouse button.
####SHIFT_LEFT
    Holding shift while pressing the left mouse button.
####SHIFT_RIGHT
    Holding shift while pressing the right mouse button.

###PREVENTED ACTIONS
- Shift Click on backpack moving it into another backpack
- Drag and Drop of backpack item into backpack inventory
- Transferring Backpack from hotbar to backpack inventory
- Move backpack from player inventory to a backpack inventory

####HOTBAR_MOVE_AND_READD
    The clicked item is moved to the hotbar, and the item currently there is
    re-added to the player's inventory.
####HOTBAR_SWAP
    The clicked slot and the picked hotbar slot are swapped.
####MOVE_TO_OTHER_INVENTORY
    The item is moved to the opposite inventory if a space is found.

## Item Selection
### Clicked
    A Backpack has been clicked
### On the Cursor
    A Backpack is attached to the cursor/pointer or the cursor is hovering over a Backpack 
#### Cursor Actions
#####PLACE_ALL
    All of the items on the cursor are moved to the clicked slot.

#####PLACE_ONE
    A single item from the cursor is moved to the clicked slot.

#####PLACE_SOME
    Some of the items from the cursor are moved to the clicked slot
    (usually up to the max stack size).

#####SWAP_WITH_CURSOR
    The clicked item and the cursor are exchanged.
