package buildcraft.additionalpipes.pipes;

import buildcraft.transport.PipeTransportLiquids;

public class PipeSwitchLiquids extends PipeSwitch {

	public PipeSwitchLiquids(int itemID) {
		super(new PipeTransportLiquids(), itemID, 22);
	}

}
