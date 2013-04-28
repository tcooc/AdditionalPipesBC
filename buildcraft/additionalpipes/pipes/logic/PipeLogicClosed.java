package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicClosed extends PipeLogic {
	@Override
	public boolean blockActivated(EntityPlayer player) {
		ItemStack equippedItem = player.getCurrentEquippedItem();
		if (equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem()))  {
			return false;
		}
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_CLOSED, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
}
