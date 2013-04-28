package buildcraft.additionalpipes.gates;

import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.transport.IPipe;
import buildcraft.transport.ITriggerPipe;
import buildcraft.transport.Pipe;

public class TriggerPhasedSignal extends APTrigger implements ITriggerPipe {

	private IPipe.WireColor colour;

	public TriggerPhasedSignal(int id, IPipe.WireColor colour) {
		super(id);
		this.colour = colour;
	}

	@Override
	public String getDescription() {
		switch (colour) {
		case Red:
			return "Red Phased Signal";
		case Blue:
			return "Blue Phased Signal";
		case Green:
			return "Green Phased Signal";
		default:
		case Yellow:
			return "Yellow Phased Signal";
		}
	}

	@Override
	public int getIconIndex() {
		switch (colour) {
		case Red:
			return 2;
		case Blue:
			return 4;
		case Green:
			return 6;
		default:
		case Yellow:
			return 8;
		}
	}

	@Override
	public boolean isTriggerActive(Pipe pipe, ITriggerParameter parameter) {
		PipeTeleport teleportPipe = (PipeTeleport) pipe;
		return TeleportManager.instance.phasedSignals.get(teleportPipe.logic.getFrequency())[colour.ordinal()] > 0;
	}

}
