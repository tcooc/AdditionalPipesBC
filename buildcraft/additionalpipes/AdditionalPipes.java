package buildcraft.additionalpipes;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftSilicon;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.gates.GateProvider;
import buildcraft.additionalpipes.gates.TriggerPhasedSignal;
import buildcraft.additionalpipes.gates.TriggerPipeClosed;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsRedstone;
import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsRedstone;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsWaterPump;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
import buildcraft.additionalpipes.pipes.PipeSwitchItems;
import buildcraft.additionalpipes.pipes.PipeSwitchLiquids;
import buildcraft.additionalpipes.pipes.PipeSwitchPower;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.recipes.AssemblyRecipe;
import buildcraft.api.transport.IPipe;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid=AdditionalPipes.MODID, name=AdditionalPipes.NAME,
dependencies="after:BuildCraft|Transport;after:BuildCraft|Silicon;after:BuildCraft|Transport;after:BuildCraft|Factory",
version=AdditionalPipes.VERSION)
@NetworkMod(channels={AdditionalPipes.CHANNEL, AdditionalPipes.CHANNELNBT},
clientSideRequired=true, serverSideRequired=true, packetHandler=NetworkHandler.class)
public class AdditionalPipes {
	public static final String MODID = "APUnofficial";
	public static final String NAME = "Additional Pipes Unofficial";
	public static final String VERSION = "2.2.0";
	public static final String CHANNEL = MODID;
	public static final String CHANNELNBT = CHANNEL + "NBT";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MutiPlayerProxyClient",
			serverSide = "buildcraft.additionalpipes.MutiPlayerProxy")
	public static MutiPlayerProxy proxy;

	public File configFile;

	public Logger logger;

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface CfgId {
		public boolean block() default false;
	}
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface CfgBool {}

	public static final String LOC_PATH = "/lang/additionalpipes/";
	//textures
	public static final String BASE_PATH = "/mods/additionalpipes";
	public static final String TEXTURE_PATH = BASE_PATH + "/textures";

	public static final String TEXTURE_GUI_TELEPORT = TEXTURE_PATH + "/gui/blankSmallGui.png";
	public static final String TEXTURE_GUI_ADVANCEDWOOD = TEXTURE_PATH + "/gui/advancedWoodGui.png";
	public static final String TEXTURE_GUI_DISTRIBUTION = TEXTURE_PATH + "/gui/distributionGui.png";

	//chunk load boundaries
	public ChunkLoadViewDataProxy chunkLoadViewer;
	public @CfgBool boolean chunkSight = true;
	public int chunkSightRange = 8; //config option
	public @CfgBool boolean chunkSightAutorefresh = true;

	//teleport scanner TODO
	//public Item teleportScanner;
	//public @CfgId int teleportScannerId = 14061;
	//meter TODO
	//public Item powerMeter;
	//public @CfgId int powerMeterId = 14060;

	//Redstone Liquid
	public Item pipeLiquidsRedstone;
	public @CfgId int pipeLiquidsRedstoneId = 14042;
	//Redstone
	public Item pipeItemsRedStone;
	public @CfgId int pipeItemsRedStoneId = 14043;
	//Advanced Insertion
	public Item pipeItemsAdvancedInsertion;
	public @CfgId int pipeItemsAdvancedInsertionId = 14044;
	//Advanced Wood
	public Item pipeItemsAdvancedWood;
	public @CfgId int pipeItemsAdvancedWoodId = 14045;
	//Distributor
	public Item pipeItemsDistributor;
	public @CfgId int pipeItemsDistributorId = 14046;
	//Item Teleport
	public Item pipeItemsTeleport;
	public @CfgId int pipeItemsTeleportId = 14047;
	//Liquid Teleport
	public Item pipeLiquidsTeleport;
	public @CfgId int pipeLiquidsTeleportId = 14048;
	//Power Teleport
	public Item pipePowerTeleport;
	public @CfgId int pipePowerTeleportId = 14049;
	//Items Closed
	public Item pipeItemsClosed;
	public @CfgId int pipeItemsClosedId = 14050;
	//Switch pipes
	public Item pipePowerSwitch;
	public @CfgId int pipePowerSwitchId = 14051;
	public Item pipeItemsSwitch;
	public @CfgId int pipeItemsSwitchId = 14052;
	public Item pipeLiquidsSwitch;
	public @CfgId int pipeLiquidsSwitchId = 14053;
	//water pump pipe
	public Item pipeLiquidsWaterPump;
	public @CfgId int pipeLiquidsWaterPumpId = 14054;
	//chunk loader
	public Block blockChunkLoader;
	public @CfgId(block=true) int chunkLoaderId = 1890;

	public @CfgBool boolean enableTriggers = true;
	public ITrigger triggerPipeClosed;

	public ITrigger triggerPhasedSignalRed;
	public ITrigger triggerPhasedSignalBlue;
	public ITrigger triggerPhasedSignalGreen;
	public ITrigger triggerPhasedSignalYellow;
	//keybinding
	public int laserKeyCode = 64; //config option (& in options menu)
	//misc
	public @CfgBool boolean allowWRRemove = false;
	public double powerLossCfg = .95; //config option

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		logger = Logger.getLogger(MODID);
		logger.setParent(FMLLog.getLogger());
		logger.setLevel(Level.WARNING); //DEBUG

		configFile = event.getSuggestedConfigurationFile();
		loadConfigs(false);

		Localization.addLocalization(LOC_PATH, "en_US");

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Init
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		ForgeChunkManager.setForcedChunkLoadingCallback(this,  new ChunkLoadingHandler());
		chunkLoadViewer = new ChunkLoadViewDataProxy(chunkSightRange);
		TickRegistry.registerScheduledTickHandler(chunkLoadViewer, Side.CLIENT);
		proxy.registerKeyHandler();
		proxy.registerRendering();

		//powerMeter = new ItemPowerMeter(powerMeterId).setItemName("powerMeter");
		//LanguageRegistry.addName(powerMeter, "Power Meter");
		loadConfigs(true);
		loadPipes();

		if(enableTriggers) {
			triggerPipeClosed = new TriggerPipeClosed(212);
			triggerPhasedSignalRed = new TriggerPhasedSignal(213, IPipe.WireColor.Red);
			triggerPhasedSignalBlue = new TriggerPhasedSignal(214, IPipe.WireColor.Blue);
			triggerPhasedSignalGreen = new TriggerPhasedSignal(215, IPipe.WireColor.Green);
			triggerPhasedSignalYellow = new TriggerPhasedSignal(216, IPipe.WireColor.Yellow);
			ActionManager.registerTriggerProvider(new GateProvider());
		}

		if (allowWRRemove) {
			//Additional Pipes
			GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport), new Object[]{"A", 'A', pipePowerTeleport});
			GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport), new Object[]{"A", 'A', pipeLiquidsTeleport});
			GameRegistry.addRecipe(new ItemStack(pipeItemsRedStone), new Object[]{"A", 'A', pipeLiquidsRedstone});
			//BC Liquid
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsCobblestone});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsGold});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsIron});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsStone});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsWood});
			//BC Power
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[]{"A", 'A', BuildCraftTransport.pipePowerGold});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[]{"A", 'A', BuildCraftTransport.pipePowerStone});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[]{"A", 'A', BuildCraftTransport.pipePowerWood});
		}

		//ChunkLoader
		if(chunkLoaderId != 0) {
			blockChunkLoader = new BlockChunkLoader(chunkLoaderId > 0 ? chunkLoaderId : -chunkLoaderId, 32);
			blockChunkLoader.setUnlocalizedName("TeleportTether");
			GameRegistry.registerBlock(blockChunkLoader, ItemBlock.class, "chunkLoader");
			GameRegistry.registerTileEntity(TileChunkLoader.class, "TeleportTether");
			if (chunkLoaderId > 0) {
				GameRegistry.addRecipe(new ItemStack(blockChunkLoader), new Object[]{"iii", "iLi", "iii", 'i', Item.ingotIron, 'L', new ItemStack(Item.dyePowder, 1, 4)});
			}
		}
	}

	@ServerStarting
	public void onServerStart(FMLServerStartingEvent event) {
		//event.registerServerCommand(new CommandAdditionalPipes());
		TeleportManager.instance.reset();
	}

	private void loadConfigs(boolean postInit) {
		if((!configFile.exists() && !postInit) || (configFile.exists() && postInit)) {
			return;
		}
		Configuration config = new Configuration(configFile);
		try {
			config.load();
			config.addCustomCategoryComment(Configuration.CATEGORY_BLOCK, "Set id to 0 to disable loading the block, add - in front of id to disable recipe only.");
			config.addCustomCategoryComment(Configuration.CATEGORY_ITEM, "Set id to 0 to disable loading the item, add - in front of id to disable recipe only.");
			config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "Disabling items/blocks only disables recipes.");
			Field[] fields = AdditionalPipes.class.getFields();
			for(Field field : fields){
				if(!Modifier.isStatic(field.getModifiers())) {

					CfgId annotation = field.getAnnotation(CfgId.class);
					if(annotation != null) {
						int id = field.getInt(this);
						if(annotation.block()){
							if(config.getCategory(Configuration.CATEGORY_BLOCK).containsKey(field.getName())) {
								id = config.get(Configuration.CATEGORY_BLOCK, field.getName(), id).getInt(id);
								if(id > 0) id = config.getBlock(field.getName(), id).getInt(id);
							} else {
								id = config.getBlock(field.getName(), id).getInt(id);
							}
						}else{
							if(config.getCategory(Configuration.CATEGORY_ITEM).containsKey(field.getName())) {
								id = config.get(Configuration.CATEGORY_ITEM, field.getName(), id).getInt(id);
								if(id > 0) id = config.getItem(field.getName(), id).getInt(id);
							} else {
								id = config.getItem(field.getName(), id).getInt(id);
							}
						}
						field.setInt(this, id);
					} else {
						if(field.isAnnotationPresent(CfgBool.class)){
							boolean bool = field.getBoolean(this);
							bool = config.get(Configuration.CATEGORY_GENERAL,
									field.getName(), bool).getBoolean(bool);
							field.setBoolean(this, bool);
						}
					}

				}
			}

			Property powerLoss = config.get(Configuration.CATEGORY_GENERAL,
					"powerLoss", (int) (powerLossCfg * 100));
			powerLoss.comment = "Percentage of power a power teleport pipe transmits. Between 0 and 100.";
			powerLossCfg = powerLoss.getInt() / 100.0;
			if(powerLossCfg > 1.00) {
				powerLossCfg = 0.99;
			} else if(powerLossCfg < 0.0) {
				powerLossCfg = 0.0;
			}

			Property chunkLoadSightRange = config.get(Configuration.CATEGORY_GENERAL,
					"chunkSightRange", chunkSightRange);
			chunkLoadSightRange.comment = "Range of chunk load boundaries.";

			Property laserKey = config.get(Configuration.CATEGORY_GENERAL,
					"laserKeyChar", laserKeyCode);
			laserKey.comment = "Default key to toggle chunk load boundaries.";
			laserKeyCode = laserKey.getInt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading Additional Pipes configs.", e);
		} finally {
			config.save();
		}
	}

	private void loadPipes(){
		// Item Teleport Pipe
		if(pipeItemsTeleportId != 0) {
			pipeItemsTeleport = createPipeSpecial(pipeItemsTeleportId > 0 ? pipeItemsTeleportId : -pipeItemsTeleportId, PipeItemsTeleport.class);
			if (pipeItemsTeleportId > 0) {
				GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport, 4), new Object[]{"dgd", 'd', BuildCraftCore.diamondGearItem, 'g', Block.glass});
				AssemblyRecipe.assemblyRecipes.add(
						new AssemblyRecipe(new ItemStack[]{
								new ItemStack(BuildCraftSilicon.redstoneChipset, 1 , 4),
								new ItemStack(BuildCraftTransport.pipeItemsDiamond, 2),
								new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 3)},
								10000, new ItemStack(pipeItemsTeleport, 2)));
			}

		}

		// Liquid Teleport Pipe
		if(pipeLiquidsTeleportId != 0) {
			pipeLiquidsTeleport = createPipeSpecial(pipeLiquidsTeleportId > 0 ? pipeLiquidsTeleportId : -pipeLiquidsTeleportId, PipeLiquidsTeleport.class);
			if (pipeItemsTeleport != null && pipeLiquidsTeleportId > 0) {
				GameRegistry.addRecipe(new ItemStack(pipeLiquidsTeleport), new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemsTeleport});
			}
		}

		// Power Teleport Pipe
		if(pipePowerTeleportId != 0) {
			pipePowerTeleport = createPipeSpecial(pipePowerTeleportId > 0 ? pipePowerTeleportId : -pipePowerTeleportId, PipePowerTeleport.class);
			if (pipeItemsTeleport != null && pipePowerTeleportId > 0) {
				GameRegistry.addRecipe(new ItemStack(pipePowerTeleport), new Object[]{"r", "P", 'r', Item.redstone, 'P', pipeItemsTeleport});
			}
		}

		// Distributor Pipe
		pipeItemsDistributor = doCreatePipeAndRecipe(pipeItemsDistributorId, PipeItemsDistributor.class,
				new Object[]{" r ", "IgI", 'r', Item.redstone, 'I', Item.ingotIron, 'g', Block.glass});

		// Advanced Wooded Pipe
		pipeItemsAdvancedWood = doCreatePipeAndRecipe(pipeItemsAdvancedWoodId, 8, PipeItemsAdvancedWood.class,
				new Object[]{"WgW", 'W', BuildCraftCore.woodenGearItem, 'g', Block.glass});

		// Advanced Insertion Pipe
		pipeItemsAdvancedInsertion = doCreatePipeAndRecipe(pipeItemsAdvancedInsertionId, 8, PipeItemsAdvancedInsertion.class,
				new Object[]{"IgI", 'I', BuildCraftCore.ironGearItem, 'g', Block.glass});

		// Redstone Pipe
		pipeItemsRedStone = doCreatePipeAndRecipe(pipeItemsRedStoneId, 8, PipeItemsRedstone.class,
				new Object[]{"RgR", 'R', Item.redstone, 'g', Block.glass});
		// Redstone Liquid Pipe
		pipeLiquidsRedstone = doCreatePipeAndRecipe(pipeLiquidsRedstoneId, PipeLiquidsRedstone.class,
				new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemsRedStone});

		// Closed Items Pipe
		pipeItemsClosed = doCreatePipeAndRecipe(pipeItemsClosedId, PipeItemsClosed.class,
				new Object[]{"r", "I", 'I', BuildCraftTransport.pipeItemsVoid, 'i', BuildCraftCore.ironGearItem});
		//switch pipes
		pipeItemsSwitch = doCreatePipeAndRecipe(pipeItemsSwitchId, 8, PipeSwitchItems.class,
				new Object[]{"GgG", 'g', Block.glass, 'G', BuildCraftCore.goldGearItem});
		pipePowerSwitch = doCreatePipeAndRecipe(pipePowerSwitchId, PipeSwitchPower.class,
				new Object[]{"r", "I", 'I', pipeItemsSwitch, 'r', Item.redstone});
		pipeLiquidsSwitch = doCreatePipeAndRecipe(pipeLiquidsSwitchId, PipeSwitchLiquids.class,
				new Object[]{"w", "I", 'I', pipeItemsSwitch, 'w', BuildCraftTransport.pipeWaterproof});

		//water pump pipe
		pipeLiquidsWaterPump = doCreatePipeAndRecipe(pipeLiquidsWaterPumpId, PipeLiquidsWaterPump.class,
				new Object[]{" L ", "rPr", " W ",
			'r', Item.redstone,
			'P', BuildCraftCore.ironGearItem,
			'L', BuildCraftTransport.pipeLiquidsGold,
			'w', BuildCraftTransport.pipeWaterproof,
			'W', BuildCraftTransport.pipeLiquidsWood});
	}

	private Item doCreatePipeAndRecipe(int id, Class<? extends Pipe> clas, Object[] recipe) {
		return doCreatePipeAndRecipe(id, 1, clas, recipe);
	}

	private Item doCreatePipeAndRecipe(int id, int output, Class<? extends Pipe> clas, Object[] recipe) {
		if(id == 0) return null;
		Item pipe = createPipe(id > 0 ? id : -id, clas);
		for(Object obj : recipe) {
			if(obj == null) return pipe;
		}
		GameRegistry.addRecipe(new ItemStack(pipe, output), recipe);
		return pipe;
	}

	private static Item createPipe(int id, Class<? extends Pipe> clas) {
		Item res = BlockGenericPipe.registerPipe(id, clas);
		res.setUnlocalizedName(clas.getSimpleName());
		proxy.registerPipeRendering(res);
		return res;
	}

	//special pipe code
	private static class ItemPipeAP extends ItemPipe {
		protected ItemPipeAP(int i) {
			super(i);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public EnumRarity getRarity(ItemStack stack){
			return EnumRarity.rare;
		}
	}

	private Item createPipeSpecial(int id, Class<? extends Pipe> clas) {
		ItemPipe item = new ItemPipeAP(id);
		item.setUnlocalizedName(clas.getSimpleName());
		proxy.registerPipeRendering(item);
		BlockGenericPipe.pipes.put(item.itemID, clas);
		proxy.createPipeSpecial(item, id, clas);

		return item;
	}

	//legacy method
	public static boolean isPipe(Item item) {
		if (item != null && BlockGenericPipe.pipes.containsKey(item.itemID)) {
			return true;
		}
		return false;
	}
	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) throws IOException{
		Textures.registerIcons(event.map);
	}
}
