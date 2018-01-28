package buildcraft.additionalpipes.pipes;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.utils.Log;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;


public class TeleportManager extends TeleportManagerBase
{
	public static final TeleportManager instance = new TeleportManager();
	
	private static HashMap<TeleportPipeType, Multimap<Integer, WeakReference<ITeleportPipe>>> pipes;

	public final Map<Integer, String> frequencyNames;

	private TeleportManager() 
	{
		//create the multi-multimap
		pipes = new HashMap<>();
		for(TeleportPipeType type : TeleportPipeType.values())
		{
			pipes.put(type, LinkedListMultimap.create());
		}
		
		// then the regular hashmap for frequency names
		frequencyNames = new HashMap<>();
	}
	
	/**
	 * Get a collection containing the pipes in a channel (a channel consists of a frequency and a type).
	 * @param frequency
	 * @param type
	 * @return
	 */
	private Collection<ITeleportPipe> getPipesInChannel(int frequency, TeleportPipeType type)
	{
		return pipes.get(type).get(frequency).stream()
			.map(Reference::get)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public void add(ITeleportPipe pipe, int frequency)
	{
		pipes.get(pipe.getType()).put(frequency, new WeakReference<>(pipe));

		//if unit tests are being run, pipe.container will be null.
		if(pipe.getContainer() != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe added: %s @ (%s), %d pipes in channel", pipe.getType().toString().toLowerCase(),
					pipe.getPosition().toString(), getPipesInChannel(frequency, pipe.getType()).size()));
		}
	}

	@Override
	public void remove(ITeleportPipe pipe, int frequency)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			return;

		}
		pipes.get(pipe.getType()).remove(frequency, pipe);

		//if unit tests are being run, pipe.container will be null.
		if(pipe.getContainer() != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe removed: %s @ (%s), %d pipes in channel", pipe.getType().toString().toLowerCase(),
					pipe.getPosition().toString(), getPipesInChannel(frequency, pipe.getType()).size()));
		}
	}

	@Override
	public void reset() {
		for(TeleportPipeType type : TeleportPipeType.values())
		{
			pipes.get(type).clear();
		}

		frequencyNames.clear();
		Log.info("Reset teleport manager.");
	}

	/**
	 * Get pipes connected to the provided one. (template function version)
	 * @param pipe
	 * @param includeSend whether or not to return connected pipes that send stuff.
	 * @param includeReceive whether or not to return connected pipes that receive stuff.
	 * @return
	 */
	public ArrayList<ITeleportPipe> getConnectedPipes(ITeleportPipe pipe, boolean includeSend, boolean includeReceive) 
	{
		Collection<ITeleportPipe> channel = getPipesInChannel(pipe.getFrequency(), pipe.getType());
		
		ArrayList<ITeleportPipe> connected = new ArrayList<ITeleportPipe>();
		
		for(ITeleportPipe other : channel)
		{
			if(other.getContainer() != null && other.getContainer().isInvalid())
			{
				continue;
			}
			
			// pipe is open or includeReceive &&
			// both public or same owner
			if(pipe != other)
			{
				if((other.canReceive() && includeReceive) || (other.canSend() && includeSend))
				{
					if(pipe.isPublic() ? other.isPublic() : (other.getOwnerUUID() != null && other.getOwnerUUID().equals(pipe.getOwnerUUID())))
					{
						connected.add(other);
					}	
				}
			}

		}
		return connected;
	}
	
	public Collection<ITeleportPipe> getAllPipesInNetwork(TeleportPipeType type) 
	{
		return pipes.get(type).values().stream()
			.map(Reference::get)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * Get the name of the provided frequency.
	 * @param frequency
	 * @return the name of the frequency, or an empty string if it has no name.
	 */
	@Override
	public String getFrequencyName(int frequency)
	{
		String name = frequencyNames.get(frequency);
		return name == null ? "" : name;
	}

	// TODO
	
	@Override
	public void setFrequencyName(int frequency, String name) 
	{
		frequencyNames.put(frequency, name);
	}

	public File getWorldSave(World world) 
	{
		return world.getSaveHandler().getMapFileFromName("foo").getParentFile().getParentFile();
	}

}
