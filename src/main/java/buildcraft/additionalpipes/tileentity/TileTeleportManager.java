/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.tileentity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import buildcraft.additionalpipes.api.AccessRule;
import buildcraft.additionalpipes.api.IRestrictedTile;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.PipeTeleport;
import buildcraft.additionalpipes.utils.PlayerUtils;
import buildcraft.additionalpipes.utils.RestrUtils;
import buildcraft.additionalpipes.utils.WaseiUtils;
import buildcraft.api.core.Position;
import buildcraft.core.lib.block.TileBuildCraft;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

public class TileTeleportManager extends TileBuildCraft implements IRestrictedTile
{

	public UUID ownerUUID;
	public String ownerName = "";
	public AccessRule accessRule = AccessRule.SHARED;
	public boolean isPublic = false;
	public final List<Integer> maps = Lists.newLinkedList();

	public boolean matchesOwner(ITeleportPipe pipe) 
	{
		return isPublic ? pipe.isPublic() : pipe.getOwnerUUID() != null && pipe.getOwnerUUID().equals(ownerUUID);
	}

	private int getMapDataDimension(MapData mapData) {
		return mapData.dimension;
	}

	public Collection<Integer> getMapsLinkTo(final PipeTeleport<?> pipe) {
		if (!matchesOwner(pipe)) {
			return Collections.emptyList();
		}

		return Collections2.filter(maps, new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return hasLinkedWithMap(input, pipe);
			}
		});
	}

	private boolean hasLinkedWithMap(int map, PipeTeleport<?> pipe) {
		final int mapWidth = 128;
		final int mapHeight = 128;

		MapData mapData = WaseiUtils.getMapData(WaseiUtils.createMapStack(map, worldObj), worldObj);
		int size = 1 << mapData.scale;
		Position pos = pipe.getPosition();
		int mapX = (MathHelper.floor_double(pos.x) - mapData.xCenter) / size + mapWidth/2;
		int mapZ = (MathHelper.floor_double(pos.z) - mapData.zCenter) / size + mapHeight/2;
		int dimension = getMapDataDimension(mapData);
		if (pipe.getWorld().provider.dimensionId == dimension && mapX >= 0 && mapX < mapWidth && mapZ >= 0 && mapZ < mapHeight &&
				mapData.colors[mapX + mapZ*mapWidth] != 0) {
			return true;
		}
		return false;
	}

	public boolean hasLinked(PipeTeleport<?> pipe) {

		return !getMapsLinkTo(pipe).isEmpty();
	}


	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if(nbttagcompound.hasKey("ownerUUID"))
		{
			ownerUUID = UUID.fromString(nbttagcompound.getString("ownerUUID"));
			ownerName = nbttagcompound.getString("ownerName");
		}
		
		accessRule = AccessRule.values()[nbttagcompound.getInteger("accessRule")];
		isPublic = nbttagcompound.getBoolean("isPublic");
		maps.addAll(Ints.asList(nbttagcompound.getIntArray("maps")));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		if(ownerUUID != null)
		{
			nbttagcompound.setString("ownerUUID", ownerUUID.toString());
			nbttagcompound.setString("ownerName", ownerName);
		}
		nbttagcompound.setInteger("accessRule", accessRule.ordinal());
		nbttagcompound.setBoolean("isPublic", isPublic);
		nbttagcompound.setIntArray("maps", Ints.toArray(maps));
	}

	@Override
	public String getOwnerName() {
		return ownerName;
	}

	@Override
	public void initOwner(UUID ownerUUID, String username) {
		this.ownerUUID = ownerUUID;
		this.ownerName = username;
	}

	@Override
	public AccessRule getAccessRule() {
		return accessRule;
	}

	@Override
	public void setAccessRule(AccessRule rule) {
		accessRule = rule;
	}

	@Override
	public boolean hasPermission(EntityPlayer player) {
		return ownerUUID.equals(PlayerUtils.getUUID(player));
	}

	@Override
	public boolean canAccess(EntityPlayer player) {
		return hasPermission(player) || accessRule != AccessRule.PRIVATE;
	}

	@Override
	public boolean canEdit(EntityPlayer player) {
		return hasPermission(player) || accessRule == AccessRule.SHARED;
	}

	@Override
	public boolean tryAccess(EntityPlayer player) {
		return RestrUtils.tryAccess(this, player);
	}

	@Override
	public boolean tryEdit(EntityPlayer player) {
		return RestrUtils.tryEdit(this, player);
	}

	@Override
	public UUID getOwnerUUID()
	{
		return ownerUUID;
	}
}
