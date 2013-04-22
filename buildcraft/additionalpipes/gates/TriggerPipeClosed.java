package buildcraft.additionalpipes.gates;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.transport.ITriggerPipe;
import buildcraft.transport.Pipe;

public class TriggerPipeClosed extends APTrigger implements ITriggerPipe {

	public TriggerPipeClosed(int id) {
		super(id);
	}

	@Override
	public String getDescription() {
		return "Pipe Closed";
	}

	@Override
	public boolean isTriggerActive(Pipe pipe, ITriggerParameter parameter) {
		PipeItemsClosed closedPipe = (PipeItemsClosed) pipe;
		for(int i = 0; i < closedPipe.getSizeInventory(); i++) {
			if(closedPipe.getStackInSlot(i) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getIconIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

}
