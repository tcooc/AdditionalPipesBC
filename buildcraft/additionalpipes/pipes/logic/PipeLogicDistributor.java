/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes.logic;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicDistributor extends PipeLogic {

	public int distData[] = {1, 1, 1, 1, 1, 1};
	public int distSide = 0;
	public int curTick = 0;

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if(player.isSneaking()) {
			return false;
		}

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if (equipped != null) {
			if (AdditionalPipes.isPipe(equipped)) {
				return false;
			}
		}

		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_DIST,
				container.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	private void sanityCheck() {
		for (int d : distData) {
			if (d > 0) {
				return;
			}
		}
		for (int i = 0; i < distData.length; i++) {
			Arrays.fill(distData, 1);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("curTick", curTick);
		nbt.setInteger("distSide", distSide);
		for (int i = 0; i < distData.length; i++) {
			nbt.setInteger("distData" + i, distData[i]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		curTick = nbt.getInteger("curTick");
		distSide = nbt.getInteger("distSide");
		for (int i = 0; i < distData.length; i++) {
			distData[i] = nbt.getInteger("distData" + i);
		}
		sanityCheck();
	}

}
