package buildcraft.additionalpipes.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class Textures {
	public static APActionTriggerIconProvider actionIconProvider=new APActionTriggerIconProvider();
	public static APPipeIconProvider pipeIconProvider=new APPipeIconProvider();
	public static Icon tetherTexture;
	public static void registerIcons(IconRegister par1IconRegister)
	{
		actionIconProvider.registerIcons(par1IconRegister);
		pipeIconProvider.registerIcons(par1IconRegister);
		if(par1IconRegister==Minecraft.getMinecraft().renderEngine.textureMapBlocks)
		{
			tetherTexture=par1IconRegister.registerIcon("additionalpipes:tether");
		}
	}
}

