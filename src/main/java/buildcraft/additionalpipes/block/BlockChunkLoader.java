package buildcraft.additionalpipes.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.textures.Textures;

public class BlockChunkLoader extends BlockContainer {

	public BlockChunkLoader()
	{
		super(Material.cloth);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		// TODO implement?
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileChunkLoader();
	}

	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return Textures.tetherTexture;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		
	}
}