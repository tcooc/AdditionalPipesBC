package buildcraft.additionalpipes;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;

public class KeyHandler extends KeyBindingRegistry.KeyHandler {

	public static KeyBinding laserKey;

	public KeyHandler(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return AdditionalPipes.MODID + ": " + this.getClass().getSimpleName();
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		if (tickEnd && kb.keyCode == laserKey.keyCode) {
			ChunkLoadViewDataProxy viewer = AdditionalPipes.instance.chunkLoadViewer;
			viewer.toggleLasers();
			if(viewer.lasersActive()) {
				viewer.requestPersistentChunks();
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
