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

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

public class PipePowerTeleport extends PipeTeleport implements IPipeTransportPowerHook {

	private static class PowerRequest {
		public final TileGenericPipe tile;
		public final ForgeDirection orientation;
		public PowerRequest(TileGenericPipe te, ForgeDirection o) {
			tile = te;
			orientation = o;
		}
	}

	public PipePowerTeleport(int itemID) {
		super(new PipeTransportPower(), new PipeLogicTeleport(), itemID);
	}

	@Override
	public void requestEnergy(ForgeDirection from, int is) {
		((PipeTransportPower)transport).step();

		if (!logic.canReceive) { //No need to waste CPU
			return;
		}

		List<PipeTeleport> pipeList = TeleportManager.instance.getConnectedPipes(this, true);

		if (pipeList.size() <= 0) {
			return;
		}

		for (PipeTeleport pipe : pipeList) {
			LinkedList<ForgeDirection> possibleMovements = getRealPossibleMovements(pipe);
			for (ForgeDirection orientation : possibleMovements) {
				TileEntity tile = pipe.container.getTile(orientation);
				if (tile instanceof TileGenericPipe) {
					TileGenericPipe adjacentTile = (TileGenericPipe) tile;
					PipeTransportPower nearbyTransport = (PipeTransportPower) adjacentTile.pipe.transport;
					nearbyTransport.requestEnergy(orientation.getOpposite(), is);
				}
			}
		}
	}

	@Override
	public void receiveEnergy(ForgeDirection from, double val) {
		((PipeTransportPower)transport).step();
		List<PipeTeleport> connectedPipes = TeleportManager.instance.getConnectedPipes(this, false);
		List<PipeTeleport> sendingToList = new LinkedList<PipeTeleport>();

		//no connected pipes, leave!
		if (connectedPipes.size() <= 0) {
			return;
		}

		for (PipeTeleport pipe : connectedPipes) {
			if (getPipesNeedsPower(pipe).size() > 0) {
				sendingToList.add(pipe);
			}
		}

		//no pipes need energy, leave!
		if (sendingToList.size() <= 0) {
			return;
		}

		//TODO proportional power relay
		double powerToSend = AdditionalPipes.instance.powerLossCfg * val / sendingToList.size();

		for (PipeTeleport receiver : sendingToList) {
			List<PowerRequest> needsPower = getPipesNeedsPower(receiver);

			if (needsPower.size() <= 0) {
				continue;
			}

			double dividedPowerToSend = powerToSend / needsPower.size();

			for (PowerRequest powerEntry : needsPower) {
				PipeTransportPower nearbyTransport = (PipeTransportPower) powerEntry.tile.pipe.transport;
				nearbyTransport.receiveEnergy(powerEntry.orientation, dividedPowerToSend);
			}

		}
	}


	private List<PowerRequest> getPipesNeedsPower(PipeTeleport pipe) {
		LinkedList<ForgeDirection> possibleMovements = getRealPossibleMovements(pipe);
		List<PowerRequest> needsPower = new LinkedList<PowerRequest>();

		if (possibleMovements.size() > 0) {
			for (ForgeDirection orientation : possibleMovements) {
				TileEntity tile = pipe.container.getTile(orientation);
				if (tile instanceof TileGenericPipe){
					TileGenericPipe adjacentPipe = (TileGenericPipe) tile;
					if(pipeNeedsPower(adjacentPipe)) {
						needsPower.add(new PowerRequest(adjacentPipe, orientation.getOpposite()));
					}
				}
			}
		}

		return needsPower;
	}

	//precondition: power pipe that isn't tp
	private static boolean pipeNeedsPower(TileGenericPipe tile) {
		if (tile instanceof TileGenericPipe) {
			PipeTransportPower ttb = (PipeTransportPower) tile.pipe.transport;
			for (int i = 0; i < ttb.powerQuery.length; i++)
				if (ttb.powerQuery[i] > 0) {
					return true;
				}
		}
		return false;
	}

	//returns all adjacent pipes
	private static LinkedList<ForgeDirection> getRealPossibleMovements(PipeTeleport pipe) {
		LinkedList<ForgeDirection> result = new LinkedList<ForgeDirection>();

		for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) {
			if (pipe.outputOpen(orientation)) {
				TileEntity te = pipe.container.getTile(orientation);
				if ((te instanceof TileGenericPipe) &&
						Utils.checkPipesConnections(pipe.container, te)) {
					result.add(orientation);
				}
			}
		}

		return result;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return 3;
	}

}
