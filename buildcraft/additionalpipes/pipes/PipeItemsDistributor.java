/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.pipes.logic.PipeLogicDistributor;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;

public class PipeItemsDistributor extends APPipe implements IPipeTransportItemsHook {

	public final PipeLogicDistributor logic;

	public PipeItemsDistributor(int itemID) {
		super(new PipeTransportItems(), new PipeLogicDistributor(), itemID);
		logic = (PipeLogicDistributor) super.logic;
	}

	@Override
	public int getIconIndex(ForgeDirection connection) {
		switch (connection) {
		case DOWN:    //-y
			return 10;
		case UP:      //+y
			return 11;
		case NORTH:   //-z
			return 12;
		case SOUTH:   //+z
			return 13;
		case WEST:    //-x
			return 14;
		case EAST:    //+x
		default:
			return 9;
		}
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(LinkedList<ForgeDirection> possibleOrientations, Position pos, IPipedItem item) {
		LinkedList<ForgeDirection> result = new LinkedList<ForgeDirection>();

		if (logic.curTick >= logic.distData[logic.distSide]) {
			toNextOpenSide();
		}

		result.add(ForgeDirection.VALID_DIRECTIONS[logic.distSide]);
		logic.curTick += item.getItemStack().stackSize;
		return result;
	}

	private void toNextOpenSide() {
		logic.curTick = 0;
		for (int o = 0; o < logic.distData.length; ++o) {
			logic.distSide = (logic.distSide + 1) % logic.distData.length;
			if (logic.distData[logic.distSide] > 0 &&
					container.isPipeConnected(ForgeDirection.VALID_DIRECTIONS[logic.distSide])) {
				break;
			}
		}
		//no valid inventories found, do nothing
	}

	@Override
	public void entityEntered(IPipedItem item, ForgeDirection orientation) {
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
	}

}
