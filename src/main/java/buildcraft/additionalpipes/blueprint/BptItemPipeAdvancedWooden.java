/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.blueprints;

import additionalpipes.pipes.PipeItemsAdvancedWood;
import additionalpipes.pipes.PipeLogicAdvancedWood;
import buildcraft.api.blueprints.BptBlockUtils;
import buildcraft.api.blueprints.BptSlotInfo;
import buildcraft.api.blueprints.IBptContext;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.blueprints.BptItemPipeWooden;

@Deprecated
public class BptItemPipeAdvancedWooden extends BptItemPipeWooden {

	public BptItemPipeAdvancedWooden() {
	}

	@Override
	public void initializeFromWorld(BptSlotInfo bptSlot, IBptContext context, int x, int y, int z) {
		Pipe<?> pipe = BlockGenericPipe.getPipe(context.world(), x, y, z);
		if (!(pipe instanceof PipeItemsAdvancedWood)) {
			return;
		}
		PipeLogicAdvancedWood logic = ((PipeItemsAdvancedWood) pipe).logic;

		BptBlockUtils.initializeInventoryContents(bptSlot, context, logic.getFilters());
		bptSlot.cpt.setBoolean("exclude", logic.exclude);
	}

	@Override
	public void buildBlock(BptSlotInfo slot, IBptContext context) {
		Pipe<?> pipe = BlockGenericPipe.getPipe(context.world(), slot.x, slot.y, slot.z);
		if (!(pipe instanceof PipeItemsAdvancedWood)) {
			return;
		}
		PipeLogicAdvancedWood logic = ((PipeItemsAdvancedWood) pipe).logic;

		BptBlockUtils.buildInventoryContents(slot, context, logic.getFilters());
		logic.exclude = slot.cpt.getBoolean("exclude");
	}

}
