package buildcraft.additionalpipes.gui;

import java.util.List;

import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageTelePipeData;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.lib.gui.ContainerBC_Neptune;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ContainerTeleportPipe extends ContainerBC_Neptune {

	public int connectedPipes = 0;

	private int ticks = 0;
	public PipeBehaviorTeleport pipe;
	private int freq;
	private byte state;
	private boolean isPublic;
	
	//true if the provided pipe is sending items to other pipes
	//and output locations should be shown on the ledger
	private boolean isSendingPipe;
	
	// only set on the server side
	private int originalfreq;

	public ContainerTeleportPipe(EntityPlayer player, PipeBehaviorTeleport pipe)
	{
		super(player);
		this.pipe = pipe;

		//set these variables to invalid values so that they will be updated
		state = -1;
		isPublic = !pipe.isPublic();
		freq = -1;
		
		isSendingPipe = pipe.canSend();
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{			
			List<ITeleportPipe> connectedPipes = TeleportManager.instance.getConnectedPipes(pipe, !isSendingPipe, isSendingPipe);
			int[] locations = new int[connectedPipes.size() * 3];
			for(int i = 0; i < connectedPipes.size() && i < 9; i++) {
				ITeleportPipe connectedPipe = connectedPipes.get(i);
				locations[3 * i] = connectedPipe.getContainer().getPos().getX();
				locations[3 * i + 1] = connectedPipe.getContainer().getPos().getY();
				locations[3 * i + 2] = connectedPipe.getContainer().getPos().getZ();
			}
			
			MessageTelePipeData message = new MessageTelePipeData(pipe.getPos(), locations, pipe.getOwnerUUID(), pipe.getOwnerName());
			PacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP) player);
			
			//save the pipe's old frequency so it can be removed later
			originalfreq = pipe.getFrequency();
			
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		TilePipeHolder tile = pipe.getContainer();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int connectedPipesNew = connectedPipes;
		if(ticks % 20 == 0) { // reduce lag
			ticks = 0;
			Log.debug("Old connected:" + connectedPipesNew);
			connectedPipesNew = TeleportManager.instance.getConnectedPipes(pipe, !isSendingPipe, isSendingPipe).size();
			Log.debug("New connected:" + connectedPipesNew);
		}
		ticks++;
		for(IContainerListener crafter : listeners) {
			if(freq != pipe.getFrequency()) {
				crafter.sendWindowProperty(this, 0, pipe.getFrequency());
			}
			if(state != pipe.getState()) {
				crafter.sendWindowProperty(this, 1, pipe.getState());
			}
			if(connectedPipesNew != connectedPipes) {
				crafter.sendWindowProperty(this, 2, connectedPipesNew);
			}
			if(isPublic != pipe.isPublic()) {
				crafter.sendWindowProperty(this, 3, pipe.isPublic() ? 1 : 0);
			}
		}
		state = pipe.getState();
		freq = pipe.getFrequency();
		isPublic = pipe.isPublic();
		connectedPipes = connectedPipesNew;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			pipe.setFrequency(j);
			break;
		case 1:
			pipe.setState((byte) j);
			break;
		case 2:
			connectedPipes = j;
			break;
		case 3:
			pipe.setPublic((j == 1));
			break;
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			//remove the pipe from the old frequency
			TeleportManager.instance.remove(pipe, originalfreq);
			//re-add the pipe to the new frequency
			TeleportManager.instance.add(pipe, freq);

		}
	}

}
