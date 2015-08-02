package buildcraft.additionalpipes.utils;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerUtils
{
	/**
	 * Get a player's UUID.
	 * Calling this is easier than the more complicated default syntax.
	 * @param player
	 */
	public static UUID getUUID(EntityPlayer player)
	{
		//                  getUUIDForProfile()
		return EntityPlayer.func_146094_a(player.getGameProfile());
	}
}
