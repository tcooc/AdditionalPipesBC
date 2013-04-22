package buildcraft.additionalpipes.textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class APPipeIconProvider implements IIconProvider
{
	private Icon icons[];
	private final int iconCount = 25;
	public APPipeIconProvider()
	{
		icons=new Icon[iconCount];
	}
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int iconIndex) {
		if(iconIndex>=iconCount)
			return null;
		return icons[iconIndex];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		if(iconRegister!=Minecraft.getMinecraft().renderEngine.textureMapBlocks)
			return;
		for(int i=0;i<iconCount;i++)
		{
			icons[i]=iconRegister.registerIcon("additionalpipes:pipes/"+i);
		}
	}
	
}