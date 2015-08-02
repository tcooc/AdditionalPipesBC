package buildcraft.additionalpipes.api;

import java.util.ArrayList;

/**
 * This class defines the interface for the teleport pipe manager.
 * 
 * It keeps track of all teleport pipes, and has functions to get all of them or certain ones connected to a pipe. 
 * 
 */
public abstract class TeleportManagerBase
{
	//set at runtime when the mod initializes
	public static TeleportManagerBase INSTANCE = null;
	
	/**
	 * Add a pipe to to the manager on the given frequency.
	 * 
	 * Automatically called by the teleport pipe constructor.
	 * @param pipe
	 * @param frequency
	 */
	public abstract void add(ITeleportPipe pipe, int frequency);
	
	/**
	 * Remove a pipe from the manager on the given frequency.
	 * @param pipe
	 * @param frequency
	 */
	public abstract void remove(ITeleportPipe pipe, int frequency);
	
	/**
	 * Remove the knowledge of all pipes from the teleport manager.  If called, pipes will stay in the world, but
	 * not send items to each other until the world is reloaded.
	 * 
	 * This function is useful for testing the teleport manager.
	 */
	public abstract void reset();
	
	/**
	 * Get pipes connected to (aka linked with) the provided one.
	 * @param pipe
	 * @param includeSend whether or not to return connected pipes that send stuff.
	 * @param includeReceive whether or not to return connected pipes that receive stuff.
	 * @return
	 */
	public abstract ArrayList<PipeTeleport<?>> getConnectedPipes(ITeleportPipe pipe, boolean includeSend, boolean includeReceive);
	
	/**
	 * Get the name of the provided frequency.
	 * 
	 * NOTE: frequency naming is not supported in the UI as of v4.6.0
	 * @param frequency
	 * @return the name of the frequency, or an empty string if it has no name.
	 */
	public abstract String getFrequencyName(int frequency);
	
	/**
	 * Set the name of a frequency.
	 * @param frequency
	 * @param name
	 */
	public abstract void setFrequencyName(int frequency, String name);
}
