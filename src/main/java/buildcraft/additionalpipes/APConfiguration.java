package buildcraft.additionalpipes;

import java.io.File;

import buildcraft.additionalpipes.utils.Log;
import buildcraft.lib.registry.RegistryConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;


public class APConfiguration
{	
	public static int chunkSightRange; // config option
	
	public static boolean chunkSightAutorefresh = true;
	
	public static boolean enableDebugLog;
	
	public static boolean enableChunkloaderRecipe;
	
	// keybinding
	public static int laserKeyCode; // config option (& in options menu)
	
	public static float powerTransmittanceCfg; // config option
	
	public static int waterPumpWaterPerTick; // in millibuckets / tick
	
	public static int gravityFeedPipeTicksPerPull;
	
	public static boolean enableTriggers = true;
	
	//set from config
	public static boolean filterRightclicks = false;

	
	public static void loadConfigs(File configFile)
	{
		Configuration config = new Configuration(configFile);
		try 
		{
			config.load();
						
			Property powerTransmittance = config.get(Configuration.CATEGORY_GENERAL, "powerTransmittance", 90);
			powerTransmittance.setComment("Percentage of power a power teleport pipe transmits. Between 0 and 100.");
			powerTransmittanceCfg = powerTransmittance.getInt() / 100.0f;
			if(powerTransmittanceCfg > 1.00)
			{
				powerTransmittanceCfg = 1f;
			}
			else if(powerTransmittanceCfg < 0.0)
			{
				powerTransmittanceCfg = 0.0f;
			}

			Property chunkSightRangeProperty = config.get(Configuration.CATEGORY_GENERAL, "chunkSightRange", 8);
			chunkSightRangeProperty.setComment("Range of chunk load boundaries.");
			chunkSightRange = chunkSightRangeProperty.getInt();

			Property laserKey = config.get(Configuration.CATEGORY_GENERAL, "laserKeyChar", 68);
			laserKey.setComment("Default key to toggle chunk load boundaries.");
			laserKeyCode = laserKey.getInt();
			
			Property filterRightclicksProperty = config.get(Configuration.CATEGORY_GENERAL, "filterRightclicks", false);
			filterRightclicksProperty.setComment("When right clicking on something with a gui, do not show the gui if you have a pipe in your hand");
			filterRightclicks = filterRightclicksProperty.getBoolean();
			
			Property enableDebugLogProperty = config.get(Configuration.CATEGORY_GENERAL, "enableDebugLog", false);
			enableDebugLogProperty.setComment("Enable debug logging for development");
			enableDebugLog = enableDebugLogProperty.getBoolean();
			
			Property waterPerTickProperty = config.get(Configuration.CATEGORY_GENERAL, "waterPumpWaterPerTick", 90);
			waterPerTickProperty.setComment("Amount of water the Water Pump Pipe produces in millibuckets/tick");
			waterPumpWaterPerTick = waterPerTickProperty.getInt();
			
			Property gpPullRateProperty = config.get(Configuration.CATEGORY_GENERAL, "gravityFeedPipeTicksPerPull", 20);
			gpPullRateProperty.setComment("How many ticks the Gravity Feed Pipe needs to extract an item");
			gravityFeedPipeTicksPerPull = gpPullRateProperty.getInt();
			
			Property enableChunkloaderProperty = config.get(Configuration.CATEGORY_GENERAL, "enableChunkloaderRecipe", true);
			enableChunkloaderProperty.setComment("Whether or not the chunkloader will have a crafting recipe.  Set to false to make it a creative-only item.");
			enableChunkloaderRecipe = enableChunkloaderProperty.getBoolean();
		} 
		catch(Exception e)
		{
			Log.error("Error loading Additional Pipes configs." + e);
		}
		finally
		{
			config.save();
			
			// tell Buildcraft that this is our configuration file
			RegistryConfig.setRegistryConfig(AdditionalPipes.MODID, config);
		}
	}

}
