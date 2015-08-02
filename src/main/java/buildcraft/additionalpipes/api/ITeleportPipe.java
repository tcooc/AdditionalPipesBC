package buildcraft.additionalpipes.api;

import buildcraft.api.core.Position;

/**
 * Interface that describes the functions of a Teleport Pipe.
 * 
 * Mostly just getters and setters for the various properties.
 * @author Jamie
 *
 */
public interface ITeleportPipe extends IRestrictedTile
{
	public void setFrequency(int freq);
	
	public int getFrequency();
	
	/**
	 * Gets the send-receive state of the teleport pipe.
	 * 
	 *  0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	 * @param state
	 */
	public byte getState();

	public void setState(byte state);


	public boolean isPublic();

	public void setPublic(boolean isPublic);

	public PipeType getType();
	
	public Position getPosition();
}
