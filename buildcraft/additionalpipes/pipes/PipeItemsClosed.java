package buildcraft.additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.pipes.logic.PipeLogicClosed;
import buildcraft.core.inventory.Transactor;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.core.utils.Utils;
import buildcraft.transport.EntityData;
import buildcraft.transport.IItemTravelingHook;
import buildcraft.transport.PipeTransportItems;

public class PipeItemsClosed extends APPipe implements IInventory, IItemTravelingHook {

	private ItemStack[] inventory = new ItemStack[10];

	public PipeItemsClosed(int itemID) {
		super(new PipeTransportItems(), new PipeLogicClosed(), itemID);
		((PipeTransportItems) transport).travelHook = this;
	}

	@Override
	public void dropContents() {
		super.dropContents();
		Utils.dropItems(worldObj, this, xCoord, yCoord, zCoord);
	}

	@Override
	public void centerReached(PipeTransportItems pipe, EntityData data) {
	}

	@Override
	public void endReached(PipeTransportItems pipe, EntityData data,
			TileEntity tile) {
	}

	@Override
	public void drop(PipeTransportItems pipe, EntityData data) {
		Transactor transactor = new TransactorSimple(this);
		transactor.add(data.item.getItemStack().copy(), ForgeDirection.UNKNOWN, true);
		if(inventory[inventory.length - 1] != null) {
			for(int i = 1; i < inventory.length; i++) {
				inventory[i - 1] = inventory[i];
			}
		}
		inventory[inventory.length - 1] = null;
		data.item.getItemStack().stackSize = 0;
		container.scheduleRenderUpdate();
	}


	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList list = new NBTTagList();

		for (ItemStack stack : inventory) {
			if(stack != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}

		nbttagcompound.setTag("closedInventory", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList list = nbttagcompound.getTagList("closedInventory");
		for (int i = 0; i < list.tagCount() && i < inventory.length; i++) {
			NBTTagCompound stackTag = (NBTTagCompound) list.tagAt(i);
			inventory[i] = ItemStack.loadItemStackFromNBT(stackTag);
		}

	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return 18 + (getStackInSlot(0) == null ? 0 : 1);
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int amt) {
		ItemStack stack = inventory[i].splitStack(amt);
		if (inventory[i].stackSize == 0) {
			inventory[i] = null;
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = inventory[i];
		inventory[i] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inventory[i] = stack;
	}

	@Override
	public String getInvName() {
		return "pipeItemsClosed";
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
		return false;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

}
