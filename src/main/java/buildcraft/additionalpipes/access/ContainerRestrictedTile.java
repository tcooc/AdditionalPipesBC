/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.access;

import net.minecraft.entity.player.EntityPlayer;
import buildcraft.additionalpipes.api.AccessRule;
import buildcraft.additionalpipes.api.IRestrictedTile;
import buildcraft.additionalpipes.gui.ContainerAP;
import buildcraft.additionalpipes.utils.RestrUtils;

public abstract class ContainerRestrictedTile extends ContainerAP {

	public final PropertyString propOwner;
	public final PropertyInteger propAccess;

	private final IRestrictedTile tile;

	public ContainerRestrictedTile(IRestrictedTile tile, int inventorySize) {
		super(inventorySize);
		this.tile = tile;

		propOwner = addPropertyToContainer(new PropertyString());
		propAccess = addPropertyToContainer(new PropertyInteger());
	}

	@Override
	protected boolean onChangeProperty(int index, Property prop, EntityPlayer player) {
		if (index == propAccess.index) {
			if (tile.hasPermission(player)) {
				tile.setAccessRule(AccessRule.values()[((PropertyInteger) prop).value]);
			} else {
				RestrUtils.accessDenied(player, tile.getOwnerName());
			}
		}
		return true;
	}

	@Override
	protected Object getPropertyValue(int index) {
		if (index == propOwner.index) {
			return tile.getOwnerName();
		} else if (index == propAccess.index) {
			return tile.getAccessRule().ordinal();
		} else {
			return null;
		}
	}

	protected boolean hasPermission(String username) {
		return username.equalsIgnoreCase(propOwner.value);
	}

	protected boolean canAccess(String username) {
		return hasPermission(username) || AccessRule.values()[propAccess.value] != AccessRule.PRIVATE;
	}

	protected boolean canEdit(String username) {
		return hasPermission(username) || AccessRule.values()[propAccess.value] == AccessRule.SHARED;
	}

}
