/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.api.core.Position;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;
import buildcraft.transport.utils.TransportUtils;

public class PipeItemsTeleport extends PipeTeleport {
	private static final int ICON = 0;

	private final PipeTransportItems transport;

	public PipeItemsTeleport(int itemID) {
		super(new PipeTransportItems(), itemID);
		transport = (PipeTransportItems) super.transport;
	}
	
	public void eventHandler(PipeEventItem.Entered event)
	{
		/*if(!AdditionalPipes.proxy.isServer(getWorld())) {
			return;
		}*/
		
		List<PipeTeleport> connectedTeleportPipes = TeleportManager.instance.getConnectedPipes(this, false);
		// no teleport pipes connected, use default
		if(connectedTeleportPipes.size() <= 0 || (state & 0x1) == 0) {
			return;
		}

		// output to random pipe
		LinkedList<ForgeDirection> outputOrientations = new LinkedList<ForgeDirection>();
		PipeTeleport otherPipe = connectedTeleportPipes.get(rand.nextInt(connectedTeleportPipes.size()));

		// find possible output orientations
		for(ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if(otherPipe.outputOpen(o))
				outputOrientations.add(o);
		}
		// no outputs found, default behaviour
		if(outputOrientations.size() <= 0) {
			return;
		}

		ForgeDirection newOrientation = outputOrientations.get(rand.nextInt(outputOrientations.size()));
		TileGenericPipe destination = (TileGenericPipe) otherPipe.container.getTile(newOrientation);

		if(destination == null) {
			return;
		}
		
		Position insertPoint = new Position(destination.xCoord + 0.5, destination.yCoord + TransportUtils.getPipeFloorOf(event.item.getItemStack()), destination.zCoord + 0.5, newOrientation.getOpposite());
		insertPoint.moveForwards(0.5);
		event.item.setPosition(insertPoint.x, insertPoint.y, insertPoint.z);
		
		((PipeTransportItems) destination.pipe.transport).injectItem(event.item, newOrientation);

		AdditionalPipes.instance.logger.info(event.item + " from " + getPosition() + " to " + otherPipe.getPosition() + " " + newOrientation);
		event.cancelled = true;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}

}
