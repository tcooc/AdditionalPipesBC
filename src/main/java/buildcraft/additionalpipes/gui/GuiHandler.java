package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.additionalpipes.pipes.PipeBehaviorDistribution;
import buildcraft.additionalpipes.pipes.PipeBehaviorJeweled;
import buildcraft.additionalpipes.pipes.PipeBehaviorPriorityInsertion;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	// Gui IDs
	public static final int PIPE_TP = 1;
	public static final int PIPE_DIST = 2;
	public static final int PIPE_WOODEN_ADV = 3;
	public static final int PIPE_CLOSED = 4;
	public static final int PIPE_PRIORITY = 5;
	public static final int PIPE_JEWELED = 6;


	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile == null) {
			return null;
		}
		
		Log.debug("Opening Container " + ID + " on the server");
		
		switch(ID) {
		case PIPE_TP:
			return new ContainerTeleportPipe(player, (PipeBehaviorTeleport) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_DIST:
			return new ContainerDistributionPipe((PipeBehaviorDistribution) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_WOODEN_ADV:
			return new ContainerAdvancedWoodPipe(player, (PipeBehaviorAdvWood) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_CLOSED:
			return new ContainerPipeClosed(player, (PipeBehaviorClosed) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_PRIORITY:
			return new ContainerPriorityInsertionPipe((PipeBehaviorPriorityInsertion) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_JEWELED:
			return new ContainerJeweledPipe(player.inventory, ((PipeBehaviorJeweled)((TilePipeHolder) tile).getPipe().getBehaviour()));
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile == null) {
			return null;
		}
		
		Log.debug("Opening GUI " + ID + " on the client");
		
		switch(ID)
		{
		case PIPE_TP:
			return new GuiTeleportPipe(player, ((PipeBehaviorTeleport)((TilePipeHolder) tile).getPipe().getBehaviour()));
		case PIPE_DIST:
			return new GuiDistributionPipe((PipeBehaviorDistribution) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_WOODEN_ADV:
			return new GuiAdvancedWoodPipe(player, (PipeBehaviorAdvWood) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_CLOSED:
			return new GuiPipeClosed(player, (PipeBehaviorClosed) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_PRIORITY:
			return new GuiPriorityInsertionPipe((PipeBehaviorPriorityInsertion) ((TilePipeHolder) tile).getPipe().getBehaviour());
		case PIPE_JEWELED:
			return new GuiJeweledPipe(player.inventory, ((PipeBehaviorJeweled)((TilePipeHolder) tile).getPipe().getBehaviour()));
		default:
			return null;
		}
	}
}
