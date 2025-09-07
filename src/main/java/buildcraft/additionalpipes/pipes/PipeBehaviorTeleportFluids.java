/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;
import java.util.Iterator;

import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeEventFluid;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.transport.pipe.flow.PipeFlowFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class PipeBehaviorTeleportFluids extends PipeBehaviorTeleport 
{

	public PipeBehaviorTeleportFluids(IPipe pipe, NBTTagCompound tagCompound)
	{
		super(pipe, tagCompound, TeleportPipeType.FLUIDS);
	}

	public PipeBehaviorTeleportFluids(IPipe pipe)
	{
		super(pipe, TeleportPipeType.FLUIDS);
	}
	

	/**
	 * Thsi event handler only allows as much fluid to move to the center as can move to other teleport pipes
	 * @param event
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PipeEventHandler
	public void preMoveCenter(PipeEventFluid.PreMoveToCentre event)
	{
		if(canSend())
		{
			ArrayList<PipeBehaviorTeleportFluids> connectedPipes = (ArrayList)TeleportManager.instance.getConnectedPipes(this, false, true);
			
			// make our request based off the total number of MB that other pipes have space for
			int totalMBNeeded = 0;
			for(PipeBehaviorTeleportFluids pipe : connectedPipes)
			{
				totalMBNeeded += pipe.getMaxAcceptableMB(event.fluid.getFluid());
			}
			
			Log.debug("[FluidTeleportPipe] Requested " + totalMBNeeded + " MB of fluid");
			
			for(EnumFacing side : EnumFacing.VALUES)
			{
				int fluidFromThisSide = Math.min(event.totalOffered[side.ordinal()], totalMBNeeded);

				event.actuallyOffered[side.ordinal()] = fluidFromThisSide;
				totalMBNeeded -= fluidFromThisSide;
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PipeEventHandler
	public void onMoveCenter(PipeEventFluid.OnMoveToCentre event)
	{
		if(canSend())
		{
			ArrayList<PipeBehaviorTeleportFluids> connectedPipes = (ArrayList)TeleportManager.instance.getConnectedPipes(this, false, true);


			int totalRecieved = 0;
			for (EnumFacing side : EnumFacing.VALUES){
				if (side == teleportSide){continue;}
				totalRecieved += event.fluidEnteringCentre[side.ordinal()];
			}

			Log.debug("[FluidTeleportPipe] Got " + totalRecieved + " MB of fluid");
			
			FluidStack remaining = new FluidStack(event.fluid.getFluid(), totalRecieved);
						
			// loop until we're out of fluid, or until no pipes need it
			while(remaining.amount > 0 && connectedPipes.size() > 0)
			{
				// divide the fluid into apportionments for each pipe
				FluidStack maxPerIteration = remaining.copy();
				maxPerIteration.amount /= connectedPipes.size(); // it's OK if we have rounding errors, it will get resolved eventually
				
				// insert one allocation into each pipe that needs it
				Iterator<PipeBehaviorTeleportFluids> pipeIter = connectedPipes.iterator();
				while(pipeIter.hasNext())
				{
					PipeBehaviorTeleportFluids pipe = pipeIter.next();
					
					int inserted = ((PipeFlowFluids) pipe.pipe.getFlow()).insertFluidsForce(maxPerIteration, null, false);
					
					if(inserted == 0)
					{
						// this pipe is done, remove it
						pipeIter.remove();
					}
					else
					{
						remaining.amount -= inserted;
					}
					
				}
			}
			
			if(remaining.amount > 0)
			{
				Log.warn("PipeLiquidsTeleport's PreMoveToCentre event handler requested more fluid than can be handled!  " + remaining.amount + " MB is left and will be rejected.");
			}
			
			// update event data
			for(EnumFacing side : EnumFacing.VALUES)
			{
				// allow no fluid to enter the center, regardless
				event.fluidEnteringCentre[side.ordinal()] = 0;
				
				if(remaining.amount > 0)
				{
					// decrease the amount of fluid entering the side to match what was actually consumed
					int fluidBlockedFromEntering = Math.min(event.fluidLeavingSide[side.ordinal()], remaining.amount);
					
					event.fluidLeavingSide[side.ordinal()] -= fluidBlockedFromEntering;
					remaining.amount -= fluidBlockedFromEntering;
				}
			}

			
		}
	}
	
	/**
	 * Returns the number of millibuckets of the given fluid that this pipe's center tank can accept
	 * @param fluid
	 * @return
	 */
	public int getMaxAcceptableMB(Fluid fluid)
	{
		// try inserting an infinite amount, and see how much is returned
		return ((PipeFlowFluids) pipe.getFlow()).insertFluidsForce(new FluidStack(fluid, Integer.MAX_VALUE), null, true);
	}

}
