package buildcraft.additionalpipes.pipes;

import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Class for all 3 types of switch pipe
 * @author jamie
 *
 */
public class PipeBehaviorSwitch extends APPipe
{
	
	private boolean canConnect;

	public PipeBehaviorSwitch(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
	}

	public PipeBehaviorSwitch(IPipe pipe)
	{
		super(pipe);
	}

	@Override
	public int getTextureIndex(EnumFacing direction)
	{		
		return (canConnect ? 0 : 1);
	}
	
	public void onTick()
	{
		// run only on the server
		if(pipe.getHolder().getPipeWorld().isRemote)
		{
			return;
		}
		
		boolean newCanConnect = !pipe.getHolder().getPipeWorld().isBlockPowered(getPos());
		
		if(canConnect != newCanConnect)
		{
			canConnect = newCanConnect;
			pipe.markForUpdate();
		}
	}
	
	
	
	/*
	@Override
	public boolean canConnectRedstone() {
		return true;
	}
	*/
	

	@Override
	public void writePayload(PacketBuffer buffer, Side side)
	{
		buffer.writeBoolean(canConnect);
	}

	@Override
	public void readPayload(PacketBuffer buffer, Side side, MessageContext ctx)
	{
		canConnect = buffer.readBoolean();
	}

	@Override
	public boolean canConnect(EnumFacing face, PipeBehaviour other)
	{
		return canConnect;
	}

	@Override
	public boolean canConnect(EnumFacing face, TileEntity oTile)
	{
		return canConnect;
	}

}
