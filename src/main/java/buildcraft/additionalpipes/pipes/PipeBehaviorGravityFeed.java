package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.APConfiguration;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipe.ConnectedType;
import buildcraft.lib.inventory.filter.StackFilter;
import buildcraft.transport.pipe.flow.PipeFlowItems;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PipeBehaviorGravityFeed extends APPipe
{
			
	private int ticksSincePull = 0;


	
	public PipeBehaviorGravityFeed(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);		
		
		ticksSincePull = nbt.getInteger("ticksSincePull");
	}

	public PipeBehaviorGravityFeed(IPipe pipe)
	{
		super(pipe);
	}

	private boolean shouldTick() 
	{
		return ticksSincePull >= APConfiguration.gravityFeedPipeTicksPerPull;
	}
	
	@Override
	public void onTick()
	{
		if(pipe.getHolder().getPipeWorld().isRemote)
		{
			return;
		}
		
		ticksSincePull++;

		if(shouldTick())
		{
			if(pipe.isConnected(EnumFacing.UP) && pipe.getConnectedType(EnumFacing.UP) == ConnectedType.TILE)
			{
				((PipeFlowItems)pipe.getFlow()).tryExtractItems(1, EnumFacing.UP, null, StackFilter.ALL, false);
			}
			ticksSincePull = 0;
		}
	}

	@Override
	public int getTextureIndex(EnumFacing direction) 
	{
		if(direction == EnumFacing.UP)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbt = super.writeToNbt();

		nbt.setInteger("ticksSincePull", ticksSincePull);
		
		return nbt;
	}
}
