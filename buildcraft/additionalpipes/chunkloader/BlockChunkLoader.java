package buildcraft.additionalpipes.chunkloader;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.Textures;

public class BlockChunkLoader extends BlockContainer {

	public BlockChunkLoader(int BlockID, int i) {
		super(BlockID, Material.cloth);
		//setTextureFile(AdditionalPipes.TEXTURE_MASTER);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		//TODO implement?
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int p1, int p2) {
		super.breakBlock(world, i, j, k, p2, p2);
		//TODO implement?
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileChunkLoader();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	@Override
	public Icon getIcon(int par1, int par2)
	{
		return Textures.tetherTexture;
	}
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		//let Texutre class do it work
	}
}