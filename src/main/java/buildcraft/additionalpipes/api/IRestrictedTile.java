/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.api;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public interface IRestrictedTile {

	/**
	 * Gets the UUID of the owner of the pipe.
	 * 
	 * Can be null if the owner has not been set.
	 * @return
	 */
	public UUID getOwnerUUID();
	
	/**
	 * Get the string username of the owner.
	 * 
	 * If an owner has not been set, returns empty string.
	 * @return
	 */
	public String getOwnerName();
		
	public void initOwner(UUID ownerUUID, String ownerName);

	public AccessRule getAccessRule();

	public void setAccessRule(AccessRule rule);

	public boolean hasPermission(EntityPlayer player);

	public boolean canAccess(EntityPlayer player);

	public boolean canEdit(EntityPlayer player);

	public boolean tryAccess(EntityPlayer player);

	public boolean tryEdit(EntityPlayer player);

}
