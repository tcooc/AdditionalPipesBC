package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicSwitch extends PipeLogic {

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void onNeighborBlockChange(int blockId) {
		super.onNeighborBlockChange(blockId);
		container.scheduleNeighborChange();
		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = container.getTile(direction);
			if(tile instanceof TileGenericPipe) {
				((TileGenericPipe) tile).scheduleNeighborChange();
			}
		}
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		return worldObj != null && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

}
