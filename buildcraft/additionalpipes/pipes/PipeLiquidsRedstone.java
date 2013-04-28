/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.core.network.TileNetworkData;
import buildcraft.core.utils.Utils;
import buildcraft.transport.PipeTransportLiquids;
import buildcraft.transport.pipes.PipeLogicGold;

public class PipeLiquidsRedstone extends APPipe {
	public @TileNetworkData boolean isPowering = false;

	public PipeLiquidsRedstone(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogicGold(), itemID);

		((PipeTransportLiquids) transport).flowRate = 80;
		((PipeTransportLiquids) transport).travelDelay = 2;
	}

	private void UpdateTiles(int i, int j, int k) {
		worldObj.notifyBlocksOfNeighborChange(i, j, k, BuildCraftTransport.genericPipeBlock.blockID);
	}

	@Override
	public int isPoweringTo(int l) {
		//System.out.println("RedStoneIsPoweringTo");
		LiquidStack liquid = ((PipeTransportLiquids) transport)
				.getTanks(ForgeDirection.UNKNOWN)[ForgeDirection.UNKNOWN.ordinal()].getLiquid();
		if (liquid == null || liquid.amount == 0) {
			isPowering = false;
			return 0;
		}

		isPowering = true;
		int i1 = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if(i1 == 5 && l == 1) {
			return 0;
		}

		if(i1 == 3 && l == 3) {
			return 0;
		}

		if(i1 == 4 && l == 2) {
			return 0;
		}

		if(i1 == 1 && l == 5) {
			return 0;
		}

		return (i1 != 2 || l != 4)?15:0;
	}

	@Override
	public int isIndirectlyPoweringTo(int l) {
		return isPoweringTo(l);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		//System.out.println("Quantity: " + (((PipeTransportLiquids)this.transport).getLiquidQuantity()) + " - Wanted: " + computeMaxLiquid() + " - Qua2: " + computeEnds()[1]);
		//System.out.println("Quantity: " + ((PipeTransportLiquids)this.transport).getCenter());
		LiquidStack liquid = ((PipeTransportLiquids) transport)
				.getTanks(ForgeDirection.UNKNOWN)[ForgeDirection.UNKNOWN.ordinal()].getLiquid();
		if (liquid == null || liquid.amount == 0 && isPowering) {
			isPowering = false;
			UpdateTiles(container.xCoord, container.yCoord, container.zCoord);
		}
		else if (!isPowering) {
			isPowering = true;
			UpdateTiles(container.xCoord, container.yCoord, container.zCoord);
		}
	}

	public boolean canRec(Position p) {
		TileEntity entity = worldObj.getBlockTileEntity((int) p.x, (int) p.y,
				(int) p.z);

		if (!Utils.checkPipesConnections(entity, container)) {
			return false;
		}

		if (entity instanceof IPipeEntry || entity instanceof ITankContainer) {
			return true;
		}

		return false;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return isPowering ? 15 : 1;
	}
}
