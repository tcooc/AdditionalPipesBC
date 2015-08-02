/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.triggers;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import additionalpipes.AdditionalPipes;
import additionalpipes.pipes.ITeleportLogicProvider;
import additionalpipes.pipes.PipePowerAdvancedWood;
import buildcraft.BuildCraftTransport;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;
import buildcraft.transport.Gate;
import buildcraft.transport.Pipe;

import com.google.common.collect.Lists;

public class PipeTriggerProvider implements ITriggerProvider {

	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipe ipipe) {
		Pipe<?> pipe = (Pipe<?>) ipipe;
		LinkedList<ITrigger> result = Lists.newLinkedList();

		if (pipe instanceof PipePowerAdvancedWood) {
			result.add(BuildCraftTransport.triggerPipeRequestsEnergy);
		} else if (pipe instanceof ITeleportLogicProvider && pipe.hasGate()) {
			if (/*pipe.wireSet[IPipe.WireColor.Red.ordinal()] && */pipe.gate.kind.ordinal() >= Gate.GateKind.AND_2.ordinal()) {
				result.add(AdditionalPipes.triggerRemoteRedSignalActive);
				result.add(AdditionalPipes.triggerRemoteRedSignalInactive);
			}
			if (/*pipe.wireSet[IPipe.WireColor.Blue.ordinal()] && */pipe.gate.kind.ordinal() >= Gate.GateKind.AND_3.ordinal()) {
				result.add(AdditionalPipes.triggerRemoteBlueSignalActive);
				result.add(AdditionalPipes.triggerRemoteBlueSignalInactive);
			}
			if (/*pipe.wireSet[IPipe.WireColor.Green.ordinal()] && */pipe.gate.kind.ordinal() >= Gate.GateKind.AND_4.ordinal()) {
				result.add(AdditionalPipes.triggerRemoteGreenSignalActive);
				result.add(AdditionalPipes.triggerRemoteGreenSignalInactive);
			}
			if (/*pipe.wireSet[IPipe.WireColor.Yellow.ordinal()] && */pipe.gate.kind.ordinal() >= Gate.GateKind.AND_4.ordinal()) {
				result.add(AdditionalPipes.triggerRemoteYellowSignalActive);
				result.add(AdditionalPipes.triggerRemoteYellowSignalInactive);
			}
		}
		return result;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		return null;
	}

}
