package buildcraft.additionalpipes;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MutiPlayerProxyClient extends MutiPlayerProxy {

	@Override
	public void registerKeyHandler() {
		KeyHandler.laserKey = new KeyBinding("Toggle chunk loading boundries",
				AdditionalPipes.instance.laserKeyCode);

		KeyBinding[] bindings = new KeyBinding[] { KeyHandler.laserKey };
		boolean[] repeatableBindings = new boolean[] { false };

		KeyHandler keyHandler = new KeyHandler(bindings, repeatableBindings);
		KeyBindingRegistry.registerKeyBinding(keyHandler);
	}

	@Override
	public void registerPipeRendering(Item res){
		MinecraftForgeClient.registerItemRenderer(res.itemID, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void createPipeSpecial(ItemPipe item, int id, Class<? extends Pipe> clas) {
		try {
			Pipe dummyPipe = clas.getConstructor(int.class).newInstance(id);
			if (dummyPipe != null){
				item.setPipesIcons(dummyPipe.getIconProvider());
				//TODO look around
				item.setPipeIconIndex(dummyPipe.getIconIndex(ForgeDirection.VALID_DIRECTIONS[0]));
				//item.setTextureIndex(dummyPipe.getTextureIndexForItem());
			}
		} catch(Exception e) {}
	}
}
