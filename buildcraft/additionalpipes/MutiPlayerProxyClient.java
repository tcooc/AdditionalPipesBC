package buildcraft.additionalpipes;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
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

}
