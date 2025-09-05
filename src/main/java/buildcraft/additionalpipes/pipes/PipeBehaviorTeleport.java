package buildcraft.additionalpipes.pipes;

import java.util.Random;
import java.util.UUID;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.additionalpipes.utils.PlayerUtils;
import buildcraft.additionalpipes.utils.TagStrings;
import buildcraft.additionalpipes.utils.TranslationKeys;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventTileState;
import buildcraft.lib.misc.EntityUtil;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;


public abstract class PipeBehaviorTeleport extends APPipe implements ITeleportPipe 
{
	protected static final Random rand = new Random();

	private int frequency = 0;
	public enum States {
		NONE,
		SEND,
		RECEIVE,
		SEND_AND_RECEIVE;

		public static final States[] VALUES = new States[3];

		public static States byIndex(int index)
		{
			return VALUES[MathHelper.abs(index % VALUES.length)];
		}
	}
	// 0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	protected States state = States.SEND;
	protected UUID ownerUUID;
	protected String ownerName = "";

	protected int[] network = new int[0]; // coordinates of connected pipes.  Used as a sort of cache variable by the teleport pipe GUI.
	protected boolean isPublic = false;
	protected UUID pipeUUID;
	protected EnumFacing teleportSide = null;

	public final TeleportPipeType type;

	public PipeBehaviorTeleport(IPipe pipe, TeleportPipeType type)
	{
		super(pipe);
		this.type = type;
		this.pipeUUID = UUID.randomUUID();

/*		if(isServer())
		{
			TeleportManager.instance.add(this, frequency);
		}*/

	}
	
	public PipeBehaviorTeleport(IPipe pipe, NBTTagCompound tagCompound, TeleportPipeType type)
	{
		super(pipe, tagCompound);
		this.type = type;
		
		frequency = tagCompound.getInteger(TagStrings.FREQ);
		state = States.values()[tagCompound.getByte(TagStrings.STATE)];
		if(tagCompound.hasKey(TagStrings.OWNER_UUID))
		{
			ownerUUID = UUID.fromString(tagCompound.getString(TagStrings.OWNER_UUID));
			ownerName = tagCompound.getString(TagStrings.OWNER_NAME);
		}
		isPublic = tagCompound.getBoolean(TagStrings.IS_PUBLIC);

		if (tagCompound.hasKey(TagStrings.PIPE_UUID)){
			this.pipeUUID = UUID.fromString(tagCompound.getString(TagStrings.PIPE_UUID));
		}
		else{
			pipeUUID = UUID.randomUUID();
		}

		teleportSide = EnumFacing.VALUES[tagCompound.getByte(TagStrings.TELEPORT_SIDE)];

/*		if(isServer())
		{
			TeleportManager.instance.add(this, frequency);
		}*/

	}
	
	@Override
	public byte getState()
	{
		return (byte) state.ordinal();
	}

	@Override
	public void setState(byte state)
	{
		this.state = States.values()[state];
	}

	public void setState(States state) {
		this.state = state;
	}

	@Override
	public UUID getOwnerUUID()
	{
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID)
	{
		this.ownerUUID = ownerUUID;
	}

	@Override
	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	public int[] getNetwork() {
		return network;
	}

	public void setNetwork(int[] network) {
		this.network = network;
	}

	@Override
	public boolean isPublic()
	{
		return isPublic;
	}

	@Override
	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}

	public UUID getPipeUUID() {
		return pipeUUID;
	}

	public void setPipeUUID(UUID pipeUUID){
		this.pipeUUID = pipeUUID;
	}

	public EnumFacing getTeleportSide() {
		return teleportSide;
	}

	public void setTeleportSide(EnumFacing teleportSide) {
		this.teleportSide = teleportSide;
	}

	@Override
	public TeleportPipeType getType()
	{
		return type;
	}
	
	@PipeEventHandler
	public void onInvalidate(PipeEventTileState.Invalidate event)
	{
		if(isServer())
		{
			Log.debug("Teleport pipe at " + getPos() + " invalidated");
			TeleportManager.instance.remove(this, frequency);
		}
	}
	
	@PipeEventHandler
	public void onChunkUnload(PipeEventTileState.ChunkUnload event)
	{
		if(isServer())
		{
			Log.debug("Teleport pipe at " + getPos() + " unloaded");
			TeleportManager.instance.remove(this, frequency);
		}
	}
	
	@PipeEventHandler
	public void onValidate(PipeEventTileState.Validate event)
	{
		if(isServer())
		{
			Log.debug("Teleport pipe at " + getPos() + " validated");
			TeleportManager.instance.add(this, frequency);
		}
	}

	/*@Override
	public void initialize() {
		super.initialize();
		TeleportManager.instance.add(this, frequency);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		TeleportManager.instance.remove(this, frequency);
	}*/
	
	
	
	@Override
	public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part)  
	{
		if(player.world.isRemote)
		{
			return true;
		}
		
		if(ownerUUID == null)
		{
			// set owner of pipe
			ownerUUID = PlayerUtils.getUUID(player);
			ownerName = player.getName();
		}
		
		if(!isPublic)
		{
			//test for player name change
			if(PlayerUtils.getUUID(player).equals(ownerUUID))
			{
				if(!player.getName().equals(ownerName))
				{
					ownerName = player.getName();
				}
			}
			else
			{
				//access denied
				player.sendMessage(new TextComponentTranslation(TranslationKeys.ACCESS_DENIED, ownerName));
				
				//if we return false, this method can get called again with a different side, and it will show the message again
				return true;
			}
		}
		
		if (EntityUtil.getWrenchHand(player) != null) 
        {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        
        if (!player.world.isRemote) 
        {
			Log.debug("[TeleportPipe] PipeUUID: " + pipeUUID);
        	BlockPos pipePos = pipe.getHolder().getPipePos();
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_TP, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
	}

	/**
	 * Checks two teleport pipes for equality.  The teleport manager will remove entries that are equal according to this method when adding a new pipe.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ITeleportPipe)
		{
			ITeleportPipe pipe = (ITeleportPipe)obj;
			return pipe.getPipeUUID() == pipeUUID;
			
/*			if(pipe.getType() == getType())
			{
				if(pipe.getState() == getState())
				{
					if(pipe.isPublic() == isPublic())
					{
						if(Objects.equals(pipe.getOwnerUUID(), getOwnerUUID()))
						{
							if(Objects.equals(pipe.getPosition(), getPosition()))
							{
								if(pipe.getFrequency() == getFrequency())
								{
									return true;
								}
							}
						}
					}
				}
			}*/
		}
		
		return false;
	}

	public void setFrequency(int freq) {
		frequency = freq;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}
	
	@Override
	public TilePipeHolder getContainer()
	{
		// if unit tests are being run, pipe will be null
		if(pipe != null)
		{
			return (TilePipeHolder) pipe.getHolder();
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean canConnect(EnumFacing face, PipeBehaviour other)
	{
		if(other instanceof PipeBehaviorTeleport)
		{
			return false;
		}

		if (face == EnumFacing.VALUES[teleportSide.ordinal()]){
			return false;
		}
		
		return super.canConnect(face, other);
	}
	

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbttagcompound = super.writeToNbt();
		nbttagcompound.setInteger(TagStrings.FREQ, frequency);
		nbttagcompound.setByte(TagStrings.STATE, (byte) state.ordinal());
		if(ownerUUID != null)
		{
			nbttagcompound.setString(TagStrings.OWNER_UUID, ownerUUID.toString());
			nbttagcompound.setString(TagStrings.OWNER_NAME, ownerName);
		}
		nbttagcompound.setBoolean(TagStrings.IS_PUBLIC, isPublic);

		if (pipeUUID != null){
			nbttagcompound.setString(TagStrings.PIPE_UUID, pipeUUID.toString());
		}

		if(teleportSide != null)
		{
			nbttagcompound.setByte(TagStrings.TELEPORT_SIDE, (byte) teleportSide.ordinal());
		}
		return nbttagcompound;
	}

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeBehaviorTeleport pipe)
	{
		if(pipe.isPublic || pipe.ownerUUID.equals(PlayerUtils.getUUID(player)) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

	public BlockPos getPosition() {
		return getPos();
	}
	
	@Override
	public boolean canReceive()
	{
		return (state.ordinal() & States.RECEIVE.ordinal()) > 0;
	}
	
	@Override
	public boolean canSend()
	{
		return (state.ordinal() & States.SEND.ordinal()) > 0;
	}
}
