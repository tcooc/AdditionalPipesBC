/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import buildcraft.core.ItemBlockBuildCraft;
import buildcraft.core.utils.StringUtils;

public class ItemTeleportManager extends ItemBlockBuildCraft {

	public ItemTeleportManager(int itemID) {
		super(itemID);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {
		if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("owner")) {
			list.add(StringUtils.localize("gui.owner") + ": " + stack.stackTagCompound.getString("owner"));
		}
	}

}
