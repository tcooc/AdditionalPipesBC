package buildcraft.additionalpipes.pipes;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.Textures;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.pipes.PipeLogic;

public abstract class APPipe extends Pipe {

	public APPipe(PipeTransport transport, PipeLogic logic, int itemID) {
		super(transport, logic, itemID);
	}

	@Override
	public IIconProvider getIconProvider()
	{
		return Textures.pipeIconProvider;
	}

	@Override
	public abstract int getIconIndex(ForgeDirection direction);
}
