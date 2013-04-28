/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import buildcraft.additionalpipes.pipes.logic.PipeLogicAdvancedWood;
import buildcraft.core.gui.BuildCraftContainer;

public class ContainerAdvancedWoodPipe extends BuildCraftContainer {

	private PipeLogicAdvancedWood logic;
	private IInventory playerIInventory;
	private IInventory filterIInventory;

	private boolean exclude;

	public ContainerAdvancedWoodPipe (IInventory playerInventory, PipeLogicAdvancedWood filterInventory) {
		super (filterInventory.getSizeInventory());
		logic = filterInventory;
		exclude = !logic.exclude;
		playerIInventory = playerInventory;
		filterIInventory = filterInventory;

		int k = 0;

		for(int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(filterInventory, j1 + k * 9, 8 + j1 * 18, 18 + k * 18));
		}


		for(int l = 0; l < 3; l++) {
			for(int k1 = 0; k1 < 9; k1++) {
				addSlotToContainer(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 76 + l * 18));
			}

		}

		for(int i1 = 0; i1 < 9; i1++) {
			addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 134));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (Object crafter : crafters) {
			if(exclude != logic.exclude) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 0, logic.exclude ? 1 : 0);
			}
		}
		exclude = logic.exclude;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			logic.exclude = (j == 1);
			break;
		}
	}

}