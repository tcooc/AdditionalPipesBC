package buildcraft.additionalpipes;

import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class MutiPlayerProxy {
	public boolean isServer(World world) {
		return !world.isRemote;
	}

	public void registerKeyHandler() {}

	public void registerRendering() {}

	public void registerPipeRendering(Item res) {}

	public void createPipeSpecial(ItemPipe item, int id, Class<? extends Pipe> clas) {}
}
