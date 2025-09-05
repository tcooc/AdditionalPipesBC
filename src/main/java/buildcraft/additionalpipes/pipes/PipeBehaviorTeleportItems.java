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

import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.additionalpipes.utils.TagStrings;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.transport.pipe.flow.PipeFlowItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PipeBehaviorTeleportItems extends PipeBehaviorTeleport
{
	final private static double TELEPORTED_ITEM_SPEED = .1;
	
	// side of the pipe that teleported items enter and exit from
	
	public PipeBehaviorTeleportItems(IPipe pipe, NBTTagCompound tagCompound)
	{
		super(pipe, tagCompound, TeleportPipeType.ITEMS);
	}

	public PipeBehaviorTeleportItems(IPipe pipe)
	{
		super(pipe, TeleportPipeType.ITEMS);
	}

	@Override
	public boolean canConnect(EnumFacing face, PipeBehaviour other)
	{
		// can't connect to all 6 sides since one side has to be the teleport side
		int numConnectedSides = 0; 
		for(EnumFacing direction : EnumFacing.VALUES)
		{
			if(pipe.isConnected(direction))
			{
				++numConnectedSides;
			}
		}
		
		if(numConnectedSides >= 5)
		{
			return false;
		}
		
		return super.canConnect(face, other);
	}
	
	/**
	 * Get the side of the pipe that items are teleported into and out of.
	 * Items that come from this side are not teleported again.
	 * 
	 * The teleport side will change only if a pipe is connected to teleportSide, or disconnected from the opposite of teleportSide.
	 * 
	 * If this returns null, then the pipe is connected on no sides.
	 * @return
	 */
/*	public EnumFacing getTeleportSide()
	{
		// teleportSide can only be calculated on the server
		if(isClient())
		{
			return null;
		}
		
		// check if we need to recalculate the teleport side
		if(teleportSide == null || pipe.isConnected(teleportSide) || !pipe.isConnected(teleportSide.getOpposite()))
		{
			Log.debug("[ItemTeleportPipe]" + getPosition().toString() + " Recalculating teleport side...");
			teleportSide = null;
			
			boolean allSidesConnected = true;
			
			// for recalculation: 
			// find the first unconnected side that is opposite to a connected side
			for(EnumFacing side : EnumFacing.VALUES)
			{
				//Log.debug("isConnected(" + side + ") = " + pipe.isConnected(side));
				if(!pipe.isConnected(side) && pipe.isConnected(side.getOpposite()))
				{
					teleportSide = side;
					break;
				}
				
				allSidesConnected = allSidesConnected && pipe.isConnected(side);
			}
			
			if(teleportSide == null && allSidesConnected)
			{
				// should never happen -- just arbitrarily choose down.
				teleportSide = EnumFacing.DOWN;
			}
			
			Log.debug("[ItemTeleportPipe]" + getPosition().toString() + " Teleport side set to " + String.valueOf(teleportSide));

		}
		
		return teleportSide;
	}*/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PipeEventHandler
	public void onReachCenter(PipeEventItem.ReachCenter event)
	{
		// only process event on server side
		if(pipe.getHolder().getPipeWorld().isRemote || !canSend()) 
		{
			return;
		}
		
		// if the item is coming from the teleportSide, it has already been teleported, so leave it be
		if(event.from == getTeleportSide())
		{
			return;
		}

		ArrayList<PipeBehaviorTeleportItems> connectedTeleportPipes = (ArrayList)TeleportManager.instance.getConnectedPipes(this, false, true);
		
		// no teleport pipes connected, use default
		if(connectedTeleportPipes.size() <= 0 || (state.ordinal() & States.SEND.ordinal()) == 0) {
			return;
		}

		// output to random pipe
		LinkedList<EnumFacing> outputOrientations = new LinkedList<EnumFacing>();
		PipeBehaviorTeleportItems otherPipe;
		
		int originalPipeNumber = pipe.getHolder().getPipeWorld().rand.nextInt(connectedTeleportPipes.size());
		int currentPipeNumber = originalPipeNumber;
		
		boolean found = false;
		int numberOfTries = 0;
		
		// find a pipe with something connected to it
		// The logic for this is... pretty complicated, actually.
		do
		{
			++numberOfTries;
			otherPipe = connectedTeleportPipes.get(currentPipeNumber);
			
			for(EnumFacing o : EnumFacing.values())
			{
				if(otherPipe.pipe.isConnected(o))
				{
					outputOrientations.add(o);
				}
			}
			
			// no outputs found, try again
			if(outputOrientations.size() <= 0) 
			{
				++currentPipeNumber;
				
				//loop back to the start
				if(currentPipeNumber >= connectedTeleportPipes.size())
				{
					currentPipeNumber = 0;
				}
			}
			else
			{
				found = true;
			}
		}
		while(numberOfTries < connectedTeleportPipes.size() && !found);

		//couldn't find any, so give up
		if(!found)
		{
			Log.debug("[ItemTeleportPipe]" + getPosition().toString() + "Unable to find a destination, dropping item " + event.getStack());
			return;
		}
		
		((PipeFlowItems)otherPipe.pipe.getFlow()).insertItemsForce(event.getStack(), otherPipe.getTeleportSide(), event.colour, TELEPORTED_ITEM_SPEED); 
		Log.debug("[ItemTeleportPipe]" + getPosition().toString() + event.getStack() + " from " + getPosition() + " to " + otherPipe.getPosition());
		event.setStack(ItemStack.EMPTY);
	}
	
	@PipeEventHandler
	public void orderSides(PipeEventItem.SideCheck event)
	{
		// if an item is going to be teleported, make sure it goes toward the teleport side and not another pipe input
		if(event.from != getTeleportSide())
		{
			try
			{
				event.increasePriority(getTeleportSide(), 100);
			}
			catch(NullPointerException ex)
			{
				// it seems like if a connected pipe is being broken right as event.increasePriority() is called, the event gets a null priorities array,
				// and we get this.
				// Just ignore it.
				Log.debug("Caught NPE from SideCheck.increasePriority()");
			}
		}
	}
	
}
