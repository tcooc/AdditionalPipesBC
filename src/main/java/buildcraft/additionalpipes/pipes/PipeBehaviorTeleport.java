package buildcraft.additionalpipes.pipes;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.additionalpipes.utils.PlayerUtils;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;


public abstract class PipeBehaviorTeleport extends APPipe implements ITeleportPipe 
{
	protected static final Random rand = new Random();

	private int frequency = 0;
	// 0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	public byte state = 1;

	public UUID ownerUUID;
	public String ownerName = "";
	
	public int[] network = new int[0]; // coordinates of connected pipes.  Used as a sort of cache variable by the teleport pipe GUI.
	public boolean isPublic = false;
	
	public final TeleportPipeType type;

	public PipeBehaviorTeleport(IPipe pipe, TeleportPipeType type)
	{
		super(pipe);
		this.type = type;
	}
	
	public PipeBehaviorTeleport(IPipe pipe, NBTTagCompound tagCompound, TeleportPipeType type)
	{
		super(pipe, tagCompound);
		this.type = type;
		
		frequency = tagCompound.getInteger("freq");
		state = tagCompound.getByte("state");
		if(tagCompound.hasKey("ownerUUID"))
		{
			ownerUUID = UUID.fromString(tagCompound.getString("ownerUUID"));
			ownerName = tagCompound.getString("ownerName");
		}
		isPublic = tagCompound.getBoolean("isPublic");
	}
	
	@Override
	public byte getState()
	{
		return state;
	}

	@Override
	public void setState(byte state)
	{
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
				player.sendMessage(new TextComponentTranslation("message.ap.accessdenied", ownerName));
				
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
			
			if(pipe.getType() == getType())
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
			}
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
		
		return super.canConnect(face, other);
	}
	

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbttagcompound = super.writeToNbt();
		nbttagcompound.setInteger("freq", frequency);
		nbttagcompound.setByte("state", state);
		if(ownerUUID != null)
		{
			nbttagcompound.setString("ownerUUID", ownerUUID.toString());
			nbttagcompound.setString("ownerName", ownerName);
		}
		nbttagcompound.setBoolean("isPublic", isPublic);
		
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
		return (state & 0x2) > 0;
	}
	
	@Override
	public boolean canSend()
	{
		return (state & 0x1) > 0;
	}
}
