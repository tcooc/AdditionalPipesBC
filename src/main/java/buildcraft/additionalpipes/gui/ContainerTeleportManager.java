/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.ArrayUtils;

import buildcraft.additionalpipes.access.ContainerRestrictedTile;
import buildcraft.additionalpipes.access.Property;
import buildcraft.additionalpipes.access.PropertyBoolean;
import buildcraft.additionalpipes.access.PropertyIntArray;
import buildcraft.additionalpipes.access.PropertyInteger;
import buildcraft.additionalpipes.access.PropertyMap;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.tileentity.TileTeleportManager;
import buildcraft.additionalpipes.utils.WaseiUtils;
import buildcraft.api.core.Position;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ContainerTeleportManager extends ContainerRestrictedTile {

	public final PropertyBoolean propIsPublic;
	public final PropertyInteger propLP;
	public final PropertyIntArray propMaps;
	public final PropertyMap propPipes;
	public final PropertyIntArray propViewClick;

	public final TileTeleportManager manager;

	private Map<PropertyInteger, PropertyIntArray> pipes;
	private boolean needToRebuild = false;

	public ContainerTeleportManager(IInventory playerInventory, TileTeleportManager manager) {
		super(manager, 0);
		this.manager = manager;

		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			rebuildPipeProps();
			MinecraftForge.EVENT_BUS.register(this);
		}

		propIsPublic = addPropertyToContainer(new PropertyBoolean());
		propLP = addPropertyToContainer(new PropertyInteger());
		propMaps = addPropertyToContainer(new PropertyIntArray());
		propPipes = addPropertyToContainer(new PropertyMap());
		propViewClick = addPropertyToContainer(new PropertyIntArray());

		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 9; k1++) {
				addSlotToContainer(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 142 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 200));
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return manager.getWorldObj().getTileEntity(manager.xCoord, manager.yCoord, manager.zCoord) == manager;
	}

	@Override
	public void detectAndSendChanges() {
		if (needToRebuild) {
			rebuildPipeProps();
			needToRebuild = false;
		}

		super.detectAndSendChanges();

		for (Object crafter : crafters) {
			// crafter is ICrafting
			if (crafter instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) crafter;
				ItemStack backup = player.inventory.mainInventory[0];
				try {
					for (int map : manager.maps) {
						ItemStack mapStack = new ItemStack(Items.map, 1, map);
						player.inventory.mainInventory[0] = mapStack;
						MapData mapData = WaseiUtils.getMapData(mapStack, manager.getWorldObj());
						if (mapData != null) {
							mapData.updateVisiblePlayers(player, mapStack);
							Packet packet = Items.map.createMapDataPacket(mapStack, manager.getWorldObj(), player);

							if (packet != null) {
								player.playerNetServerHandler.sendPacketToPlayer(packet);
							}
						}
						
					}
				} finally {
					player.inventory.mainInventory[0] = backup;
				}
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		Slot slot = (Slot) inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			if (!WaseiUtils.isValidMap(slotStack, manager.getWorldObj()) ||
					ArrayUtils.indexOf(propMaps.value, slotStack.getItemDamage()) != -1 ||
					!canEdit(player.getCommandSenderName())) {
				return super.transferStackInSlot(player, slotId);
			}

			manager.maps.add(Integer.valueOf(slotStack.getItemDamage()));
			slotStack.stackSize--;
			if (slotStack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
			rebuildPipeProps();
			return super.transferStackInSlot(player, slotId);
		}

		return null;
	}

	private void transferStackInView(EntityPlayer player, int index) {
		ItemStack stackInView = WaseiUtils.createMapStack(manager.maps.get(index), manager.getWorldObj());
		if (mergeItemStack(stackInView, 0, inventorySlots.size(), true)) {
			manager.maps.remove(index);
		}
	}

	private void viewClick(int index, int mouseButton, int data, EntityPlayer player)
	{
		InventoryPlayer invPlayer = player.inventory;

		if ((data == 0 || data == 1) && (mouseButton == 0 || mouseButton == 1)) {
			if (data == 1) {
				// shift
				if (index < manager.maps.size() && manager.tryEdit(player)) {
					transferStackInView(player, index);
				}
			} else {
				ItemStack stackClicked = index < manager.maps.size() ? WaseiUtils.createMapStack(manager.maps.get(index), manager.getWorldObj()) : null;
				ItemStack stackInHand = invPlayer.getItemStack();

				if (stackClicked == null) {
					if (WaseiUtils.isValidMap(stackInHand, manager.getWorldObj()) && !manager.maps.contains(stackInHand.getItemDamage()) &&
							manager.tryEdit(player)) {
						// put item to view
						manager.maps.add(Integer.valueOf(stackInHand.getItemDamage()));
						stackInHand.stackSize--;
						if (stackInHand.stackSize == 0) {
							invPlayer.setItemStack(null);
						}
					}
				} else {
					if (stackInHand == null) {
						if (manager.tryEdit(player)) {
							manager.maps.remove(index);
							invPlayer.setItemStack(stackClicked);
						}
					} else if (stackInHand.getUnlocalizedName().equals(Items.map.getUnlocalizedName()) && stackInHand.getItemDamage() == stackClicked.getItemDamage() &&
							ItemStack.areItemStackTagsEqual(stackInHand, stackClicked)) {
						// Ordinarily, the Container merges the item in hand into the item in slot
						if (stackInHand.stackSize + 1 <= stackInHand.getMaxStackSize() && manager.tryEdit(player)) {
							manager.maps.remove(index);
							stackInHand.stackSize++;
						}
					} else if (WaseiUtils.isValidMap(stackInHand, manager.getWorldObj()) && stackInHand.stackSize == 1) {
						// replace
						if (manager.tryEdit(player)) {
							manager.maps.set(index, stackInHand.getItemDamage());
							invPlayer.setItemStack(stackClicked);
						}
					} else if (stackInHand.getItem() == Items.map) //empty map
					{
						// copy
						ItemStack copyStack = stackClicked.copy();
						copyStack.stackSize = stackInHand.stackSize;
						invPlayer.setItemStack(copyStack);
					}
				}
			}
		} else if (data == 2 && mouseButton >= 0 && mouseButton < 9) {
			// hotbar, unused
		} else if (data == 3 && player.capabilities.isCreativeMode && invPlayer.getItemStack() == null && index < manager.maps.size()) {
			// pick
			ItemStack stackToPick = WaseiUtils.createMapStack(manager.maps.get(index), manager.getWorldObj());
			stackToPick.stackSize = stackToPick.getMaxStackSize();
			invPlayer.setItemStack(stackToPick);
		} else if (data == 3 || mouseButton == 2) {
			// sort
			if (manager.tryEdit(player)) {
				Collections.sort(manager.maps);
			}
		} else if (data == 4 && invPlayer.getItemStack() == null && index < manager.maps.size()) {
			// drop
			if (manager.tryEdit(player)) {
				ItemStack stackToDrop = WaseiUtils.createMapStack(manager.maps.get(index), manager.getWorldObj());
				manager.maps.remove(index);
				player.dropPlayerItemWithRandomChoice(stackToDrop, false);
			}
		}
	}

	@SubscribeEvent
	public void onTeleportPipeChange(TeleportPipeEvent event) {
		needToRebuild = true;
	}

	@SubscribeEvent
	public void onContainerModify(ContainerModifyEvent event) {
		if (event.source != this && event.obj == manager) {
			needToRebuild = true;
		}
	}

	private void rebuildPipeProps() {
		ListMultimap<PropertyInteger, ITeleportPipe> pipeMultimap = ArrayListMultimap.create();
		for (ITeleportPipe pipe : TeleportManager.getPipes()) {
			for (int map : manager.getMapsLinkTo(pipe)) {
				pipeMultimap.put(PropertyInteger.create(map), pipe);
			}
		}
		pipes = Maps.newHashMap(Maps.transformEntries(pipeMultimap.asMap(), new EntryTransformer<PropertyInteger, Collection<ITeleportPipe>, PropertyIntArray>() {
			@Override
			public PropertyIntArray transformEntry(PropertyInteger key, Collection<ITeleportPipe> value) {
				int[] result = new int[value.size() * 3];
				int i = 0;
				for (ITeleportPipe pipe : value) {
					result[i++] = pipe.getType().ordinal();
					Position pos = pipe.getPosition();
					MapData mapData = WaseiUtils.getMapData(WaseiUtils.createMapStack(key.value, manager.getWorldObj()), manager.getWorldObj());
					int size = 1 << mapData.scale;
					double var11 = (pos.x - mapData.xCenter) / size;
					double var12 = (pos.z - mapData.zCenter) / size;
					result[i++] = (int) (var11 * 2.0F + 0.5D);
					result[i++] = (int) (var12 * 2.0F + 0.5D);
				}
				return PropertyIntArray.create(result);
			}
		}));

		if (!needToRebuild) {
			MinecraftForge.EVENT_BUS.post(new ContainerModifyEvent(this, manager));
		}
	}

	@Override
	protected boolean onChangeProperty(int index, Property prop, EntityPlayer player) {
		if (index == propIsPublic.index) {
			if (manager.tryEdit(player)) {
				manager.isPublic = ((PropertyBoolean) prop).value;
				rebuildPipeProps();
			}
		}
		else if (index == propViewClick.index) {
			int[] value = ((PropertyIntArray) prop).value;
			viewClick(value[0], value[1] >>> 16, value[1] & 0xFFFF, player);
			((EntityPlayerMP) player).updateHeldItem();
			rebuildPipeProps();
			return false;// Not to resends property
		} else {
			return super.onChangeProperty(index, prop, player);
		}
		return true;
	}

	@Override
	protected Object getPropertyValue(int index) {
		if (index == propIsPublic.index) {
			return manager.isPublic;
		} else if (index == propLP.index) {
			return 0;
		} else if (index == propMaps.index) {
			return manager.maps;
		} else if (index == propPipes.index) {
			return pipes;
		} else if (index == propViewClick.index) {
			return ArrayUtils.EMPTY_INT_ARRAY;
		} else {
			return super.getPropertyValue(index);
		}
	}

}
