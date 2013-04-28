package buildcraft.additionalpipes.pipes;

import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicWaterPump;
import buildcraft.transport.PipeTransportLiquids;

public class PipeLiquidsWaterPump extends APPipe {
	private static final Block water = Block.waterStill;

	private PipeTransportLiquids transport;

	public PipeLiquidsWaterPump(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogicWaterPump(), itemID);
		transport = (PipeTransportLiquids) super.transport;
		transport.flowRate = 80;
		transport.travelDelay = 4;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(AdditionalPipes.proxy.isServer(worldObj) &&
				worldObj.getBlockId(xCoord, yCoord - 1, zCoord) == water.blockID) {
			transport.fill(ForgeDirection.UNKNOWN, new LiquidStack(water, 100), true);
		}
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return 24;
	}

}
