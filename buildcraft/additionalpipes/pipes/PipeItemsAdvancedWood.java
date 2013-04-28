/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.pipes.logic.PipeLogicAdvancedWood;
import buildcraft.api.core.Position;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import buildcraft.api.transport.IPipedItem;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.EntityPassiveItem;
import buildcraft.core.utils.Utils;
import buildcraft.transport.PipeTransportItems;

public class PipeItemsAdvancedWood extends APPipe implements IPowerReceptor {

	private IPowerProvider powerProvider;

	public PipeItemsAdvancedWood(int itemID) {

		super(new PipeTransportItems(), new PipeLogicAdvancedWood(), itemID);

		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(50, 1, 64, 1, 64);
		powerProvider.configurePowerPerdition(64, 1);
	}

	@Override
	public void setPowerProvider(IPowerProvider provider) {
		provider = powerProvider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public void doWork() {
		if (powerProvider.getEnergyStored() <= 0)
			return;

		World w = worldObj;

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (meta > 5)
			return;

		Position pos = new Position(xCoord, yCoord, zCoord,	ForgeDirection.VALID_DIRECTIONS[meta]);
		pos.moveForwards(1);
		int blockId = w.getBlockId((int) pos.x, (int) pos.y, (int) pos.z);
		TileEntity tile = w.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

		if (tile instanceof IInventory) {
			if (!PipeManager.canExtractItems(this, w, (int) pos.x, (int) pos.y, (int) pos.z))
				return;

			IInventory inventory = (IInventory) tile;

			ItemStack extracted = checkExtract(inventory, true,
					pos.orientation.getOpposite());

			if (extracted == null || extracted.stackSize == 0) {
				powerProvider.useEnergy(1, 1, false);
				return;
			}

			Position entityPos = new Position(pos.x + 0.5, pos.y + Utils.getPipeFloorOf(extracted), pos.z + 0.5,
					pos.orientation.getOpposite());
			entityPos.moveForwards(0.5);
			IPipedItem entity = new EntityPassiveItem(w, entityPos.x, entityPos.y, entityPos.z, extracted);
			((PipeTransportItems) transport).entityEntering(entity, entityPos.orientation);
		}
	}

	/**
	 * Return the itemstack that can be if something can be extracted from this
	 * inventory, null if none. On certain cases, the extractable slot depends
	 * on the position of the pipe.
	 */
	public ItemStack checkExtract(IInventory inventory, boolean doRemove,
			ForgeDirection from) {
		//		if (inventory instanceof ISpecialInventory) {
		//			//At the moment we are going to let special inventorys handle there own. Might change if popular demand
		//			return ((ISpecialInventory) inventory).extractItem(doRemove, from);
		//		}
		IInventory inv = Utils.getInventory(inventory);
		ItemStack result = checkExtractGeneric(inv, doRemove, from, 0, inv.getSizeInventory() - 1);
		return result;
	}

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove, ForgeDirection from, int start, int stop) {
		for (int k = start; k <= stop; ++k)
			if (inventory.getStackInSlot(k) != null && inventory.getStackInSlot(k).stackSize > 0) {

				ItemStack slot = inventory.getStackInSlot(k);

				if (slot != null && slot.stackSize > 0 && canExtract(slot))
					if (doRemove)
						return inventory.decrStackSize(k, (int) powerProvider.useEnergy(1, slot.stackSize, true));
					else
						return slot;
			}
		return null;
	}


	public boolean canExtract(ItemStack item) {
		PipeLogicAdvancedWood logic = (PipeLogicAdvancedWood) this.logic;
		for (int i = 0; i < logic.getSizeInventory(); i++) {
			ItemStack stack = logic.getStackInSlot(i);
			if (stack != null && stack.itemID == item.itemID) {
				if ((Item.itemsList[item.itemID].isDamageable())) {
					return !logic.exclude;
				}
				else if (stack.getItemDamage() == item.getItemDamage()) {
					return !logic.exclude;
				}
			}
		}
		return logic.exclude;
	}

	@Override
	public int powerRequest(ForgeDirection from) {
		return getPowerProvider().getMaxEnergyReceived();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return 6;
		else {
			int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			if (metadata == direction.ordinal())
				return 7;
			else
				return 6;
		}
	}
}
