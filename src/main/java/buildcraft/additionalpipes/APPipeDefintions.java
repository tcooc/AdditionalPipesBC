package buildcraft.additionalpipes;

import com.google.common.collect.ImmutableSet;

import buildcraft.additionalpipes.pipes.PipeBehaviorAddition;
import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.additionalpipes.pipes.PipeBehaviorDistribution;
import buildcraft.additionalpipes.pipes.PipeBehaviorGravityFeed;
import buildcraft.additionalpipes.pipes.PipeBehaviorJeweled;
import buildcraft.additionalpipes.pipes.PipeBehaviorPriorityInsertion;
import buildcraft.additionalpipes.pipes.PipeBehaviorSwitch;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleportFluids;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleportItems;
import buildcraft.additionalpipes.pipes.PipeBehaviorWaterPump;
import buildcraft.additionalpipes.utils.PipeCreator;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.recipes.AssemblyRecipeBasic;
import buildcraft.api.recipes.IngredientStack;
import buildcraft.api.transport.pipe.PipeApi;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.api.transport.pipe.PipeDefinition.PipeDefinitionBuilder;
import buildcraft.lib.recipe.AssemblyRecipeRegistry;
import buildcraft.silicon.BCSiliconItems;
import buildcraft.transport.BCTransportConfig;
import buildcraft.transport.BCTransportItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class APPipeDefintions
{
	// Addition Pipe
	public static Item additionPipeItem;
	public static PipeDefinition additionPipeDef;
	
	// Advanced Wooden Pipe
	public static Item advWoodPipeItem;
	public static PipeDefinition advWoodPipeDef;
	
	// Closed Pipe
	public static Item closedPipeItem;
	public static PipeDefinition closedPipeDef;
	
	// Distribution Pipe
	public static Item distributionPipeItem;
	public static PipeDefinition distributionPipeDef;
	
	// Gravity Feed Pipe
	public static Item gravityFeedPipeItem;
	public static PipeDefinition gravityFeedPipeDef;
	
	// Priority Insertion Pipe
	public static Item priorityPipeItem;
	public static PipeDefinition priorityPipeDef;
	
	// Jeweled Pipe
	public static Item jeweledPipeItem;
	public static PipeDefinition jeweledPipeDef;
	
	// Item Teleport
	public static Item itemsTeleportPipeItem;
	public static PipeDefinition itemsTeleportPipeDef;
	
	// Liquid Teleport
	public static Item liquidsTeleportPipeItem;
	public static PipeDefinition liquidsTeleportPipeDef;
	
	// Power Teleport
	public static Item powerTeleportPipeItem;
	public static PipeDefinition powerTeleportPipeDef;
	
	// Switch pipes
	// Switch Transport Pipe
	public static Item itemsSwitchPipeItem;
	public static PipeDefinition itemsSwitchPipeDef;

	// Switch Fluid Pipe
	public static Item fluidsSwitchPipeItem;
	public static PipeDefinition fluidsSwitchPipeDef;
	
	// Switch Transport Pipe
	public static Item powerSwitchPipeItem;
	public static PipeDefinition powerSwitchPipeDef;
	
	// water pump pipe
	public static Item waterPumpPipeItem;
	public static PipeDefinition waterPumpPipeDef;
	
	
	public static void createPipes()
	{
		additionPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_addition").logic(PipeBehaviorAddition::new, PipeBehaviorAddition::new).define();
		additionPipeItem = PipeCreator.createPipeItem(additionPipeDef);
		
		advWoodPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_adv_wood").texSuffixes("_output", "_input").logic(PipeBehaviorAdvWood::new, PipeBehaviorAdvWood::new).enableColouring().define();
		advWoodPipeItem = PipeCreator.createPipeItem(advWoodPipeDef);
		
		closedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_closed").texSuffixes("_open", "_closed").logic(PipeBehaviorClosed::new, PipeBehaviorClosed::new).define();
		closedPipeItem = PipeCreator.createPipeItem(closedPipeDef);
		
		PipeDefinitionBuilder distPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_distribution").logic(PipeBehaviorDistribution::new, PipeBehaviorDistribution::new);
		attachSidedSuffixes(distPipeDefBuilder);
		distPipeDefBuilder.itemTex(EnumFacing.EAST.ordinal());
		distributionPipeDef = distPipeDefBuilder.define();
		distributionPipeItem = PipeCreator.createPipeItem(distributionPipeDef);
		
		gravityFeedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_gravity_feed").texSuffixes("_up", "_sides").logic(PipeBehaviorGravityFeed::new, PipeBehaviorGravityFeed::new).enableColouring().define();
		gravityFeedPipeItem = PipeCreator.createPipeItem(gravityFeedPipeDef);
		
		PipeDefinitionBuilder priorityPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_priority").logic(PipeBehaviorPriorityInsertion::new, PipeBehaviorPriorityInsertion::new);
		attachSidedSuffixes(priorityPipeDefBuilder);
		priorityPipeDef = priorityPipeDefBuilder.define();
		priorityPipeItem = PipeCreator.createPipeItem(priorityPipeDef);
		
		PipeDefinitionBuilder jeweledPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_jeweled").logic(PipeBehaviorJeweled::new, PipeBehaviorJeweled::new);
		attachSidedSuffixes(jeweledPipeDefBuilder);
		jeweledPipeDef = jeweledPipeDefBuilder.define();
		jeweledPipeItem = PipeCreator.createPipeItem(jeweledPipeDef);

		itemsTeleportPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_teleport").logic(PipeBehaviorTeleportItems::new, PipeBehaviorTeleportItems::new).define();
		itemsTeleportPipeItem = PipeCreator.createPipeItem(itemsTeleportPipeDef);	
		
		// add assembly recipe for Item Teleport Pipe
		ImmutableSet<IngredientStack> tpRecipeIngredients = ImmutableSet.<IngredientStack>of(
				IngredientStack.of(new ItemStack(BCSiliconItems.redstoneChipset, 1, 4)),
				IngredientStack.of(new ItemStack(BCTransportItems.pipeItemDiamond)),
				IngredientStack.of(new ItemStack(BCSiliconItems.redstoneChipset, 1, 3)));
		AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("teleportPipe", 10000 * MjAPI.MJ, tpRecipeIngredients, new ItemStack(itemsTeleportPipeItem, 8)));
		
		liquidsTeleportPipeDef = new PipeDefinitionBuilder().flowFluid().idTexPrefix("pipe_fluids_teleport").logic(PipeBehaviorTeleportFluids::new, PipeBehaviorTeleportFluids::new).define();
		liquidsTeleportPipeItem = PipeCreator.createPipeItem(liquidsTeleportPipeDef);
		
		//powerTeleportPipeDef = new PipeDefinitionBuilder().flowPower().idTexPrefix("pipe_power_teleport").logic(PipeBehaviorTeleportPower::new, PipeBehaviorTeleportPower::new).define();
		//powerTeleportPipeItem = PipeCreator.createPipeItemAndRecipe(1, powerTeleportPipeDef, true, new Object[] {"dustRedstone", itemsTeleportPipeItem});
		
		itemsSwitchPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipe_items_switch").texSuffixes("_closed", "_open").logic(PipeBehaviorSwitch::new, PipeBehaviorSwitch::new).enableColouring().define();
		itemsSwitchPipeItem = PipeCreator.createPipeItem(itemsSwitchPipeDef);
		
		fluidsSwitchPipeDef = new PipeDefinitionBuilder().flowFluid().idTexPrefix("pipe_fluids_switch").texSuffixes("_closed", "_open").logic(PipeBehaviorSwitch::new, PipeBehaviorSwitch::new).enableColouring().define();
		fluidsSwitchPipeItem = PipeCreator.createPipeItem(fluidsSwitchPipeDef);
		
		powerSwitchPipeDef = new PipeDefinitionBuilder().flowPower().idTexPrefix("pipe_power_switch").texSuffixes("_closed", "_open").logic(PipeBehaviorSwitch::new, PipeBehaviorSwitch::new).define();
		powerSwitchPipeItem = PipeCreator.createPipeItem(powerSwitchPipeDef);
		
		waterPumpPipeDef = new PipeDefinitionBuilder().flowFluid().idTexPrefix("pipe_fluids_water_pump").logic(PipeBehaviorWaterPump::new, PipeBehaviorWaterPump::new).define();
		waterPumpPipeItem = PipeCreator.createPipeItem(waterPumpPipeDef);
	}
	
	public static void setFluidCapacities()
	{
		// set Liquids Teleport Pipe to be the same as the Diamond Fluid Pipe
		PipeApi.fluidTransferData.put(liquidsTeleportPipeDef, new PipeApi.FluidTransferInfo(BCTransportConfig.baseFlowRate * 8, 10));
		
		// set Switch Fluid Pipe to be halfway between the Iron and Gold FLuid Pipes
		PipeApi.fluidTransferData.put(fluidsSwitchPipeDef, new PipeApi.FluidTransferInfo(BCTransportConfig.baseFlowRate * 6, 10));
		
		// set Water Pump Pipe to match its output capacity
		PipeApi.fluidTransferData.put(waterPumpPipeDef, new PipeApi.FluidTransferInfo(APConfiguration.waterPumpWaterPerTick, 10));
	}
	
	/**
	 * Attach "_down" through "_east" suffixes to a definition, with their indices corresponding to EnumFacing.values()[] ordering
	 * @param builder
	 */
	private static void attachSidedSuffixes(PipeDefinitionBuilder builder)
	{
		String[] suffixes = new String[6];
		
		for(int dirIndex = 0; dirIndex < EnumFacing.VALUES.length; ++dirIndex)
		{
			suffixes[dirIndex] = "_" + EnumFacing.VALUES[dirIndex].getName();
		}
		
		builder.texSuffixes(suffixes);
	}
}
