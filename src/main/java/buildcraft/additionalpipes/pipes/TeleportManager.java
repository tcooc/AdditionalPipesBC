package buildcraft.additionalpipes.pipes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.utils.Log;
import net.minecraft.world.World;


public class TeleportManager extends TeleportManagerBase
{
	public static final TeleportManager instance = new TeleportManager();
	
	private static HashMap<TeleportPipeType, Multimap<Integer, ITeleportPipe>> pipes;

	public final Map<Integer, String> frequencyNames;

	private TeleportManager() 
	{
		//create the multi-multimap
		pipes = new HashMap<>();
		for(TeleportPipeType type : TeleportPipeType.values())
		{
			pipes.put(type, LinkedListMultimap.<Integer, ITeleportPipe>create());
		}
		
		// then the regular hashmap for frequency names
		frequencyNames = new HashMap<Integer, String>();
	}
	
	/**
	 * Get a collection containing the pipes in a channel (a channel consists of a frequency and a type).
	 * @param frequency
	 * @param type
	 * @return
	 */
	private Collection<ITeleportPipe> getPipesInChannel(int frequency, TeleportPipeType type)
	{
		return pipes.get(type).get(frequency);
	}

	@Override
	public void add(ITeleportPipe newPipe, int frequency)
	{
		Collection<ITeleportPipe> pipesInChannel = pipes.get(newPipe.getType()).get(frequency);
		
		// check if this pipe was left in the teleport manager because it didn't unload cleanly for some reason
		// This should not normally happen, but I am trying to be a bit defensive here since the users have been complaining about bugs
		for(Iterator<ITeleportPipe> pipesIter = pipesInChannel.iterator(); pipesIter.hasNext(); )
		{
			ITeleportPipe pipe = pipesIter.next();
			if(pipe.getPipeUUID() == newPipe.getPipeUUID())
			{
				pipesIter.remove();
			}
		}
		
		pipesInChannel.add(newPipe);
		
		//if unit tests are being run, pipe.container will be null.
		if(newPipe.getContainer() != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe added: %s @ (%s), %d pipes in channel", newPipe.getType().toString().toLowerCase(),
					newPipe.getPosition().toString(), getPipesInChannel(frequency, newPipe.getType()).size()));
		}
	}

	@Override
	public void remove(ITeleportPipe pipeToRemove, int frequency)
	{
		Collection<ITeleportPipe> pipesInChannel = pipes.get(pipeToRemove.getType()).get(frequency);
		
		// Remove all pipes matching the one provided
		for(Iterator<ITeleportPipe> pipesIter = pipesInChannel.iterator(); pipesIter.hasNext(); )
		{

			ITeleportPipe pipe = pipesIter.next();
			if(pipe.getPipeUUID() == pipeToRemove.getPipeUUID())
			{
				pipesIter.remove();
			}
		}

		//if unit tests are being run, pipe.container will be null.
		if(pipeToRemove.getContainer() != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe removed: %s @ (%s), %d pipes in channel", pipeToRemove.getType().toString().toLowerCase(),
					pipeToRemove.getPosition().toString(), getPipesInChannel(frequency, pipeToRemove.getType()).size()));
		}
	}

	@Override
	public void reset() {
		for(TeleportPipeType type : TeleportPipeType.values())
		{
			pipes.get(type).clear();
		}

		frequencyNames.clear();
		Log.debug("Reset teleport manager.");
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
		return pipes.get(type).values();
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
