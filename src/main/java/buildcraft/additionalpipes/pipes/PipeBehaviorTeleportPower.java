/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipe.ConnectedType;
import buildcraft.api.transport.pipe.IPipeHolder;
import buildcraft.transport.pipe.flow.IPipeTransportPowerHook;
import buildcraft.transport.pipe.flow.PipeFlowPower;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class PipeBehaviorTeleportPower extends PipeBehaviorTeleport implements IPipeTransportPowerHook
{

	/**
	 * Data class used in receivePower() to keep track of what TEs need power
	 * @author jamie
	 *
	 */
	private static class PowerRequest {
		public final TileEntity tile;
		public final EnumFacing orientation; // side on tile entity of power request
		public final boolean isPipe;

		public PowerRequest(TileEntity te, EnumFacing o) {
			tile = te;
			orientation = o;
			isPipe = te instanceof IPipeHolder;
		}
	}

	public PipeBehaviorTeleportPower(IPipe pipe, NBTTagCompound tagCompound)
	{
		super(pipe, tagCompound, TeleportPipeType.POWER);
	}

	public PipeBehaviorTeleportPower(IPipe pipe)
	{
		super(pipe, TeleportPipeType.POWER);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int requestPower(EnumFacing from, long value)
	{
		int requested = 0;

		if(!canReceive()) 
		{ // No need to waste CPU
			return requested;
		}

		ArrayList<PipeBehaviorTeleportPower> pipeList = (ArrayList)TeleportManager.instance.getConnectedPipes(this, true, false);

		if(pipeList.size() <= 0) {
			return requested;
		}

		
		// for each teleport pipe connected to us, tell pipes connected to THEM that we are requesting the power
		for(PipeBehaviorTeleportPower otherPipe : pipeList) 
		{
			LinkedList<EnumFacing> possibleMovements = getRealPossibleMovements(otherPipe);
			for(EnumFacing orientation : possibleMovements) 
			{
				IPipe nearbyPipe = otherPipe.pipe.getConnectedPipe(from);
				
				PipeFlowPower nearbyFlow = (PipeFlowPower) nearbyPipe.getFlow();
				
				// will uncomment when Buildcraft API is added
				//nearbyFlow.requestPower(orientation.getOpposite(), value);
			}
		}
		return requested;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int receivePower(EnumFacing from, long energy) 
	{
		List<PipeBehaviorTeleportPower> connectedPipes = (List)TeleportManager.instance.getConnectedPipes(this, false, true);
		List<PipeBehaviorTeleportPower> sendingToList = new LinkedList<PipeBehaviorTeleportPower>();

		// no connected pipes, leave!
		if(connectedPipes.size() <= 0 || (state.ordinal() & 0x1) == 0)
		{
			return 0;
		}

		for(PipeBehaviorTeleportPower pipe : connectedPipes) 
		{
			if(getPowerRequestsByNeighbors(pipe).size() > 0) 
			{
				sendingToList.add(pipe);
			}
		}

		// no pipes need energy, leave!
		if(sendingToList.size() <= 0)
		{
			Log.debug("No pipes want power on channel " + getFrequency());
			
			return 0;
		}

		double powerToSend = APConfiguration.powerTransmittanceCfg * energy / sendingToList.size();

		for(PipeBehaviorTeleportPower receiver : sendingToList)
		{
			List<PowerRequest> needsPower = getPowerRequestsByNeighbors(receiver);

			if(needsPower.size() <= 0) {
				continue;
			}

			int dividedPowerToSend = MathHelper.ceil(powerToSend / needsPower.size());

			for(PowerRequest powerEntry : needsPower) 
			{
				if(powerEntry.isPipe)
				{
					PipeFlowPower nearbyFlow = (PipeFlowPower)(((TilePipeHolder)powerEntry.tile).getPipe().getFlow());
					
					// will uncomment when Buildcraft API is added
					//nearbyFlow.addPower(powerEntry.orientation, dividedPowerToSend);
				}
				else if (powerEntry.tile.hasCapability(MjAPI.CAP_RECEIVER, powerEntry.orientation)) 
				{
					IMjReceiver recv = powerEntry.tile.getCapability(MjAPI.CAP_RECEIVER, powerEntry.orientation);
					recv.receivePower(dividedPowerToSend, false);
				}
				else
				{
					Log.error("Don't know how to transmit power to tile " + powerEntry.tile.toString());
				}

			}
		}
		
		return (int) energy;
	}

	private List<PowerRequest> getPowerRequestsByNeighbors(PipeBehaviorTeleportPower pipe) 
	{
		LinkedList<EnumFacing> possibleMovements = getRealPossibleMovements(pipe);
		List<PowerRequest> needsPower = new LinkedList<PowerRequest>();

		if(possibleMovements.size() > 0)
		{
			for(EnumFacing orientation : possibleMovements)
			{
				TileEntity tile = pipe.pipe.getConnectedTile(orientation);
				if(tile instanceof TilePipeHolder) 
				{
					TilePipeHolder adjacentPipe = (TilePipeHolder) tile;
					if(pipeNeedsPower(adjacentPipe)) 
					{
						needsPower.add(new PowerRequest(adjacentPipe, orientation.getOpposite()));
					}
				}
			
				else if(tile != null)
				{
					if(getPowerRequestedByTileEntity(tile, orientation) > 0)
					{
						needsPower.add(new PowerRequest(tile, orientation.getOpposite()));
					}
				}
			}
		}
		

		return needsPower;
	}

	// precondition: power pipe that isn't tp
	private static boolean pipeNeedsPower(TilePipeHolder tile)
	{
		if(tile.getPipe().getFlow() instanceof PipeFlowPower) 
		{
			PipeFlowPower flowPower = (PipeFlowPower) tile.getPipe().getFlow();
			
			for(EnumFacing side : EnumFacing.VALUES)
			{
				// will uncomment when Buildcraft API is added
				//if(flowPower.getNextRequestedPower(side) > 0)
				//{
				//	return true;
				//}
			}
		}
		return false;
	}
	
	private long getPowerRequestedByTileEntity(TileEntity tile, EnumFacing sideOnPipe)
	{
		long power = 0;
				
		IMjReceiver recv = pipe.getHolder().getCapabilityFromPipe(sideOnPipe, MjAPI.CAP_RECEIVER);
        if (recv != null && recv.canReceive()) 
        {
            long requested = recv.getPowerRequested();
            if (requested > 0) {
            	power += requested;
            }
        }
		
		return power;
	}

	// returns all adjacent pipes
	private static LinkedList<EnumFacing> getRealPossibleMovements(PipeBehaviorTeleportPower pipe) 
	{
		LinkedList<EnumFacing> result = new LinkedList<EnumFacing>();

		for(EnumFacing orientation : EnumFacing.values()) 
		{
			if(pipe.pipe.isConnected(orientation) && pipe.pipe.getConnectedType(orientation) == ConnectedType.PIPE) 
			{
				result.add(orientation);
			}
		}

		return result;
	}
	
}
