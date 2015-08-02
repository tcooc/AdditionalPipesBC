/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.block.components;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import additionalpipes.api.IRestrictedTile;
import buildcraft.core.proxy.CoreProxy;

public abstract class BlockRestricted extends BlockContainer {

	protected BlockRestricted(int id, Material material) {
		super(id, material);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof IRestrictedTile)) {
			return false;
		}

		if (CoreProxy.proxy.isSimulating(world)) {
			IRestrictedTile restricted = (IRestrictedTile) tile;
			restricted.initOwner(player.username);
			if (!restricted.tryAccess(player)) {
				return true;
			}

			onBlockAccessed(world, x, y, z, player, side, xOffset, yOffset, zOffset);
		}

		return true;
	}

	protected abstract void onBlockAccessed(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset);

}
