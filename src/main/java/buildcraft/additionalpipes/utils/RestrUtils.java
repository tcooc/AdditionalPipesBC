/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import buildcraft.additionalpipes.api.IRestrictedTile;

public class RestrUtils {

	public static void accessDenied(EntityPlayer player, String owner) {
		player.addChatComponentMessage(new ChatComponentText("\u00A7c" + StatCollector.translateToLocalFormatted("chat.edit.denied", owner)));
	}

	public static boolean tryAccess(IRestrictedTile tile, EntityPlayer player) {
		if (!tile.canAccess(player)) {
			accessDenied(player, tile.getOwnerName());
			return false;
		}
		return true;
	}

	public static boolean tryEdit(IRestrictedTile tile, EntityPlayer player) {
		if (!tile.canEdit(player)) {
			accessDenied(player, tile.getOwnerName());
			return false;
		}
		return true;
	}

}
