package buildcraft.additionalpipes.textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class APActionTriggerIconProvider implements IIconProvider
{
	public static int actionDisablePipeIconIndex = 0;
	public static int triggerCraftingIconIndex = 1;
	public static int triggerPowerDischargingIconIndex =2;
	public static int triggerPowerNeededIconIndex =3;
	public static int triggerSupplierFailedIconIndex =4;
	public static int triggerHasDestinationIconIndex =5;
	
	private Icon icons[];
	private final int iconCount = 9;
	public APActionTriggerIconProvider()
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
		if(iconRegister!=Minecraft.getMinecraft().renderEngine.textureMapItems)
			return;
		for(int i=0;i<iconCount;i++)
		{
			icons[i]=iconRegister.registerIcon("additionalpipes:triggers/"+i);
		}
	}
	
}