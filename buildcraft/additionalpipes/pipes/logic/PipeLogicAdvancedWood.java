/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;
import buildcraft.transport.pipes.PipeLogicWood;

public class PipeLogicAdvancedWood extends PipeLogic implements IInventory {

	public ItemStack [] items = new ItemStack [9];

	public boolean exclude = false;

	public void switchSource () {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		int newMeta = 6;

		for (int i = meta + 1; i <= meta + 6; ++i) {
			ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i % 6];
			TileEntity tile = container.getTile(o);
			if (isInput(tile))
				if (PipeManager.canExtractItems(container.getPipe(), tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord)) {
					newMeta = o.ordinal();
					break;
				}
		}

		if (newMeta != meta) {
			worldObj.setBlock(xCoord, yCoord, zCoord, newMeta);
			container.scheduleRenderUpdate();
			//worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean isInput(TileEntity tile) {
		return !(tile instanceof TileGenericPipe) && tile instanceof IInventory
				&& Utils.checkPipesConnections(container, tile);
	}


	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench
				&& ((IToolWrench) equipped).canWrench(entityplayer, xCoord, yCoord, zCoord)) {
			switchSource();
			((IToolWrench) equipped).wrenchUsed(entityplayer, xCoord, yCoord, zCoord);
			return true;
		}
		if(AdditionalPipes.isPipe(equipped)) {
			return false;
		}

		entityplayer.openGui(AdditionalPipes.instance, GuiHandler.PIPE_WOODEN_ADV,
				container.worldObj, container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		Pipe pipe2 = null;

		if (tile instanceof TileGenericPipe) {
			pipe2 = ((TileGenericPipe) tile).pipe;
		}

		if (BuildCraftTransport.alwaysConnectPipes) {
			return super.isPipeConnected(tile);
		}
		else {
			return (pipe2 == null || (!(pipe2.logic instanceof PipeLogicWood) && !(pipe2.logic instanceof PipeLogicAdvancedWood))) && super.isPipeConnected(tile);
		}
	}

	@Override
	public void initialize () {
		super.initialize();
		switchSourceIfNeeded();
	}

	private void switchSourceIfNeeded () {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (meta > 5)
			switchSource();
		else {
			TileEntity tile = container.getTile(ForgeDirection.VALID_DIRECTIONS[meta]);
			if (!isInput(tile))
				switchSource();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		exclude = nbttagcompound.getBoolean("exclude");

		NBTTagList nbttaglist = nbttagcompound.getTagList("items");

		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist.tagAt(j);
			int index = nbttagcompound2.getInteger("index");
			items [index] = ItemStack.loadItemStackFromNBT(nbttagcompound2);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("exclude", exclude);

		NBTTagList nbttaglist = new NBTTagList();

		for (int j = 0; j < items.length; ++j) {
			if (items [j] != null && items [j].stackSize > 0) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound ();
				nbttaglist.appendTag(nbttagcompound2);
				nbttagcompound2.setInteger("index", j);
				items [j].writeToNBT(nbttagcompound2);
			}
		}

		nbttagcompound.setTag("items", nbttaglist);
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return items[var1];
	}

	@Override
	public ItemStack decrStackSize(int i, int amt) {
		ItemStack stack = getStackInSlot(i);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(i, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(i, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);
		if (stack != null) {
			setInventorySlotContents(i, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		items[var1] = var2;
	}

	@Override
	public String getInvName() {
		return "item.PipeItemsAdvancedWood";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean doDrop() {
		Utils.dropItems(worldObj, this, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		// TODO look around
		return true;
	}

}
