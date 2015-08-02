/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.blueprints;

import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import additionalpipes.api.AccessRule;
import additionalpipes.pipes.ITeleportLogicProvider;
import additionalpipes.pipes.PipeLogicTeleport;
import buildcraft.api.blueprints.BptSlotInfo;
import buildcraft.api.blueprints.IBptContext;
import buildcraft.core.blueprints.BptItem;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;

@Deprecated
public class BptItemPipeTeleport extends BptItem {

	public BptItemPipeTeleport() {
	}

	@Override
	public void addRequirements(BptSlotInfo slot, LinkedList<ItemStack> requirements) {

	}

	@Override
	public void postProcessing(BptSlotInfo slot, IBptContext context) {
	}

	@Override
	public void rotateLeft(BptSlotInfo slot, IBptContext context) {
	}

	@Override
	public void initializeFromWorld(BptSlotInfo bptSlot, IBptContext context, int x, int y, int z) {
		Pipe<?> pipe = BlockGenericPipe.getPipe(context.world(), x, y, z);
		if (!(pipe instanceof ITeleportLogicProvider)) {
			return;
		}
		PipeLogicTeleport logic = ((ITeleportLogicProvider) pipe).getLogic();

		bptSlot.cpt.setInteger("freq", logic.freq);
		bptSlot.cpt.setBoolean("canReceive", logic.canReceive);
		bptSlot.cpt.setString("owner", logic.owner);
		bptSlot.cpt.setInteger("accessRule", logic.accessRule.ordinal());
		bptSlot.cpt.setBoolean("isPublic", logic.isPublic);
	}

	@Override
	public void buildBlock(BptSlotInfo slot, IBptContext context) {
		Pipe<?> pipe = BlockGenericPipe.getPipe(context.world(), slot.x, slot.y, slot.z);
		if (!(pipe instanceof ITeleportLogicProvider)) {
			return;
		}
		PipeLogicTeleport logic = ((ITeleportLogicProvider) pipe).getLogic();

		logic.freq = slot.cpt.getInteger("freq");
		logic.canReceive = slot.cpt.getBoolean("canReceive");
		logic.owner = slot.cpt.getString("owner");
		logic.accessRule = AccessRule.values()[slot.cpt.getInteger("accessRule")];
		logic.isPublic = slot.cpt.getBoolean("isPublic");
		logic.doModify();
	}

}
