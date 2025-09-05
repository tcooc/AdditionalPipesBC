package buildcraft.additionalpipes.textures;

import buildcraft.additionalpipes.gui.GuiJeweledPipe;
import buildcraft.api.core.render.ISprite;
import buildcraft.lib.client.sprite.SpriteHolderRegistry;
import net.minecraft.util.ResourceLocation;

public class Textures {

	// textures
	public static final ResourceLocation ITEMS = new ResourceLocation("textures/atlas/items.png");
	public static final ResourceLocation DISPENSER = new ResourceLocation("textures/gui/container/dispenser.png");
	
	public static final String TEXTURE_PATH = "textures";

	public static final ResourceLocation GUI_TELEPORT = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/background_generic2.png");
	public static final ResourceLocation GUI_ADVANCEDWOOD = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/background_adv_wood.png");
	public static final ResourceLocation GUI_DISTRIBUTION = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/background_distribution.png");
	public static final ResourceLocation GUI_PRIORITY = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/background_priority.png");
	public static final ResourceLocation GUI_JEWELED = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/background_jeweled.png");
	
	public static final ResourceLocation GUI_OUTLINE_JEWELED[] = new ResourceLocation[GuiJeweledPipe.NUM_TABS];
	
	static
	{
		for(int tabNumber = 1; tabNumber <= GuiJeweledPipe.NUM_TABS; ++tabNumber)
		{
			 GUI_OUTLINE_JEWELED[tabNumber - 1] = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/jeweled_gui_outline_" + tabNumber + ".png");
		}
	}
	
    // gets set by MultiPlayerProxyClient.registerSprites()
    public static ISprite TRIGGER_PIPE_CLOSED = SpriteHolderRegistry.getHolder("additionalpipes:items/triggers/pipe_closed");

}
