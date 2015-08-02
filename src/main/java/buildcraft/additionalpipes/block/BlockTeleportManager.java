/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.block;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import additionalpipes.AdditionalPipes;
import additionalpipes.api.AccessRule;
import additionalpipes.block.components.BlockRestricted;
import additionalpipes.inventory.APGuiIds;
import additionalpipes.tileentity.TileTeleportManager;
import additionalpipes.tileentity.TileTeleportManagerEnergy;
import additionalpipes.utils.APUtils;
import buildcraft.api.tools.IToolWrench;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.inventory.InvUtils;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.core.utils.Utils;

import com.google.common.primitives.Ints;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTeleportManager extends BlockRestricted {

	private Icon textureTop;
	private Icon textureSide;

	public BlockTeleportManager(int blockID) {
		super(blockID, Material.iron);
		setHardness(30.0F);
		setCreativeTab(CreativeTabBuildCraft.tabBuildCraft);
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		if (AdditionalPipes.disableEnergyUsage) {
			return new TileTeleportManager();
		}
		return new TileTeleportManagerEnergy();
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		if (side == 0 || side == 1) {
			return textureTop;
		} else {
			return textureSide;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityLiving, stack);

		if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("owner")) {
			TileTeleportManager manager = (TileTeleportManager) world.getBlockTileEntity(x, y, z);
			manager.owner = stack.stackTagCompound.getString("owner");
			manager.accessRule = AccessRule.values()[stack.stackTagCompound.getInteger("accessRule")];
			manager.isPublic = stack.stackTagCompound.getBoolean("isPublic");
			manager.maps.addAll(Ints.asList(stack.stackTagCompound.getIntArray("maps")));
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		Utils.preDestroyBlock(world, x, y, z);
		TileTeleportManager manager = (TileTeleportManager) world.getBlockTileEntity(x, y, z);
		if (manager != null) {
			for (int map : manager.maps) {
				InvUtils.dropItems(world, APUtils.createMapStack(map, world), x, y, z);
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(player, x, y, z)) {
			if (player.isSneaking()) {
				dropBlockManager(world, x, y, z, player);
				((IToolWrench) equipped).wrenchUsed(player, x, y, z);
				return true;
			}
		}

		return super.onBlockActivated(world, x, y, z, player, side, xOffset, yOffset, zOffset);
	}

	@Override
	protected void onBlockAccessed(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset) {
		player.openGui(AdditionalPipes.instance, APGuiIds.TELEPORT_MANAGER, world, x, y, z);
	}

	public void dropBlockManager(World world, int x, int y, int z, EntityPlayer player) {
		TileTeleportManager manager = (TileTeleportManager) world.getBlockTileEntity(x, y, z);
		if (CoreProxy.proxy.isRenderWorld(world) || !manager.tryEdit(player)) {
			return;
		}
		world.removeBlockTileEntity(x, y, z);
		world.setBlockToAir(x, y, z);

		ItemStack stack = new ItemStack(AdditionalPipes.blockTeleportManager, 1);
		if (!manager.owner.isEmpty()) {
			stack.setTagCompound(new NBTTagCompound());

			stack.stackTagCompound.setString("owner", manager.owner);
			stack.stackTagCompound.setInteger("accessRule", manager.accessRule.ordinal());
			stack.stackTagCompound.setBoolean("isPublic", manager.isPublic);
			stack.stackTagCompound.setIntArray("maps", Ints.toArray(manager.maps));
		}

		InvUtils.dropItems(world, stack, x, y, z);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addCreativeItems(ArrayList itemList) {
		itemList.add(new ItemStack(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		textureTop = iconRegister.registerIcon("additionalpipes:manager_topbottom");
		textureSide = iconRegister.registerIcon("additionalpipes:manager_side");
	}

}
