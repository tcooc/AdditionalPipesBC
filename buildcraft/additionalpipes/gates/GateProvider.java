package buildcraft.additionalpipes.gates;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;

public class GateProvider implements ITriggerProvider {

	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipe pipe) {
		LinkedList <ITrigger> list = new LinkedList<ITrigger>();
		if(pipe instanceof PipeItemsClosed) {
			list.add(AdditionalPipes.instance.triggerPipeClosed);
		}
		if(pipe instanceof PipeTeleport) {
			list.add(AdditionalPipes.instance.triggerPhasedSignalRed);
			list.add(AdditionalPipes.instance.triggerPhasedSignalGreen);
			list.add(AdditionalPipes.instance.triggerPhasedSignalBlue);
			list.add(AdditionalPipes.instance.triggerPhasedSignalYellow);
		}
		return list;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		return null;
	}

}
