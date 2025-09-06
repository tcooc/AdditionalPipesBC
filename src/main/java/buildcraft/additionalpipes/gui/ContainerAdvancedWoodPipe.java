/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.lib.gui.ContainerBC_Neptune;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAdvancedWoodPipe extends ContainerBC_Neptune {

	private boolean exclude;
	private PipeBehaviorAdvWood pipe;

	public ContainerAdvancedWoodPipe(EntityPlayer player, PipeBehaviorAdvWood pipe) {
		super(player);
		this.pipe = pipe;
		exclude = !pipe.getExclude(); // force a network update
		int k = 0;

		for(int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new SlotItemHandler(pipe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), j1 + k * 9, 8 + j1 * 18, 18 + k * 18));
		}

		for(int l = 0; l < 3; l++) {
			for(int k1 = 0; k1 < 9; k1++) {
				addSlotToContainer(new Slot(player.inventory, k1 + l * 9 + 9, 8 + k1 * 18, 73 + l * 18));
			}

		}

		for(int i1 = 0; i1 < 9; i1++) {
			addSlotToContainer(new Slot(player.inventory, i1, 8 + i1 * 18, 131));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		TilePipeHolder tile = (TilePipeHolder) pipe.pipe.getHolder();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}

	@Override
	/**
	 * Called on the server, checks if the exclusion state has changed vs our cache
	 */
	public void detectAndSendChanges() 
	{
		super.detectAndSendChanges();
		for(IContainerListener crafter : listeners) {
			if(exclude != pipe.getExclude()) {
				crafter.sendWindowProperty(this, 0, pipe.getExclude() ? 1 : 0);
			}
		}
		exclude = pipe.getExclude();
	}

	/**
	 * Called on the client to update the exclusion state.  Information is transmitted in the other direction via MessageAdvWoodenPipe
	 */
	@Override
	public void updateProgressBar(int i, int j) 
	{
		switch(i) {
		case 0:
			pipe.setExclude(j == 1);
			break;
		}
	}

}