/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.blueprints;

import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import additionalpipes.pipes.PipeItemsDistributor;
import additionalpipes.pipes.PipeLogicDistributor;
import buildcraft.api.blueprints.BptSlotInfo;
import buildcraft.api.blueprints.IBptContext;
import buildcraft.core.blueprints.BptItem;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;

@Deprecated
public class BptItemPipeDistributor extends BptItem {

	public BptItemPipeDistributor() {
	}

	@Override
	public void addRequirements(BptSlotInfo slot, LinkedList<ItemStack> requirements) {

	}

	@Override
	public void postProcessing(BptSlotInfo slot, IBptContext context) {
		context.world().setBlockMetadataWithNotify(slot.x, slot.y, slot.z, slot.meta, 0);
	}

	@Override
	public void rotateLeft(BptSlotInfo slot, IBptContext context) {
		int orientation = slot.meta & 7;
		int others = slot.meta - orientation;

		slot.meta = ForgeDirection.VALID_DIRECTIONS[orientation].getRotation(ForgeDirection.DOWN).ordinal() + others;
	}

	@Override
	public void initializeFromWorld(BptSlotInfo bptSlot, IBptContext context, int x, int y, int z) {
		Pipe<?> pipe = BlockGenericPipe.getPipe(context.world(), x, y, z);
		if (!(pipe instanceof PipeItemsDistributor)) {
			return;
		}
		PipeLogicDistributor logic = ((PipeItemsDistributor) pipe).logic;

		for (int i = 0; i < logic.distData.length; i++) {
			bptSlot.cpt.setInteger("distData" + i, logic.distData[i]);
		}
	}

	@Override
	public void buildBlock(BptSlotInfo slot, IBptContext context) {
		Pipe<?> pipe = BlockGenericPipe.getPipe(context.world(), slot.x, slot.y, slot.z);
		if (!(pipe instanceof PipeItemsDistributor)) {
			return;
		}
		PipeLogicDistributor logic = ((PipeItemsDistributor) pipe).logic;

		for (int i = 0; i < logic.distData.length; i++) {
			logic.distData[i] = slot.cpt.getInteger("distData" + i);
		}
	}

}
