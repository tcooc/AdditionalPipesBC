package buildcraft.additionalpipes;

import java.io.File;

import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.chunkloader.BlockTeleportTether;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileTeleportTether;
import buildcraft.additionalpipes.gates.GateProvider;
import buildcraft.additionalpipes.gates.TriggerPipeClosed;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.item.ItemDogDeaggravator;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.sound.APSounds;
import buildcraft.additionalpipes.test.TeleportManagerTest;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.lib.registry.CreativeTabManager;
import buildcraft.lib.registry.CreativeTabManager.CreativeTabBC;
import buildcraft.silicon.BCSiliconItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = AdditionalPipes.MODID, name = AdditionalPipes.NAME, dependencies = "required-after:buildcrafttransport@[7.99.13,);required-after:buildcraftsilicon;required-after:buildcraftfactory", version = AdditionalPipes.VERSION)
public class AdditionalPipes {
	public static final String MODID = "additionalpipes";
	public static final String NAME = "Additional Pipes";
	public static final String VERSION = "6.0.0.11";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MultiPlayerProxyClient", serverSide = "buildcraft.additionalpipes.MultiPlayerProxy")
	public static MultiPlayerProxy proxy;

	public File configFile;
	
	// chunk load boundaries
	//public ChunkLoadViewDataProxy chunkLoadViewer;
	
	public CreativeTabBC creativeTab;
	


	// obsidian fluid pipe
	public Item pipeLiquidsObsidian;
	
	// chunk loader
	public BlockTeleportTether blockTeleportTether;
	
	//dog deaggravator
	public Item dogDeaggravator;
	
	public ITriggerInternal triggerPipeClosed;

	Block blockFoo;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{		
		PacketHandler.init();

		configFile = event.getSuggestedConfigurationFile();
		APConfiguration.loadConfigs(configFile);
		MinecraftForge.EVENT_BUS.register(this);
		
		//create BuildCraft creative tab
		creativeTab = CreativeTabManager.createTab("apcreativetab");
		
		Log.info("Registering pipes");
		APPipeDefintions.createPipes();
		APPipeDefintions.setFluidCapacities();
		
		Log.info("Registering gates");
		proxy.registerSprites();
		triggerPipeClosed = new TriggerPipeClosed();
		StatementManager.registerTriggerProvider(new GateProvider());
		
		// create blocks
		blockTeleportTether = new BlockTeleportTether();
		blockTeleportTether.setRegistryName("teleport_tether");

	}
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		Log.info("Registering blocks");
		
		event.getRegistry().register(blockTeleportTether);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		
		Log.info("Registering items");
		dogDeaggravator = new ItemDogDeaggravator();
		event.getRegistry().register(dogDeaggravator);
	    
		event.getRegistry().register(new ItemBlock(blockTeleportTether).setRegistryName(blockTeleportTether.getRegistryName()));
	}
	
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		Log.info("Registering recipes");		
		
		if(APConfiguration.enableChunkloaderRecipe)
		{
			Log.debug("Chunkloader recipe enabled!");
			
			ShapedOreRecipe chunkloaderRecipe = new ShapedOreRecipe(new ResourceLocation(MODID, "recipes/teleport_tether"), blockTeleportTether, "iii", "iLi", "ici", 'i', "ingotIron", 'L', "gemLapis", 'c', new ItemStack(BCSiliconItems.redstoneChipset, 1, 3));
			chunkloaderRecipe.setRegistryName("teleport_tether");
			event.getRegistry().register(chunkloaderRecipe);
		}
	}
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		Log.info("Registering sounds");
		
		APSounds.register(event.getRegistry());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	
		
		Log.info("Registering chunk load handler");
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
		//chunkLoadViewer = new ChunkLoadViewDataProxy(APConfiguration.chunkSightRange);
		//MinecraftForge.EVENT_BUS.register(chunkLoadViewer);
		
		GameRegistry.registerTileEntity(TileTeleportTether.class, "teleport_tether");
		
		// the lasers key function depends on the chunk loading code, so it can only be enabled if the chunk loader is
		proxy.registerKeyHandler();

		
		//set creative tab icon
		creativeTab.setItem(new ItemStack(APPipeDefintions.itemsTeleportPipeItem));
		
		// having debug logging is a good indicator that we're in a development environment
		if(APConfiguration.enableDebugLog)
		{
			Log.info("Running Teleport Manager Tests");
			TeleportManagerTest.runAllTests();
		}
		
		//set the reference in the API
		TeleportManagerBase.INSTANCE = TeleportManager.instance;
		
		Log.info("Setting up renderings...");
		proxy.registerRendering();

	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandAdditionalPipes());
	}

	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent event) {
		Log.debug("World unloaded, clearing teleport manager");
		TeleportManager.instance.reset();
	}


}
