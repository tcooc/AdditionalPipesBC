package buildcraft.additionalpipes.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.pipes.APPipe;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.utils.PlayerUtils;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeWire;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Gate;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public abstract class PipeTeleport<pipeType extends PipeTransport> extends APPipe<pipeType> implements ITeleportPipe {
	protected static final Random rand = new Random();

	private int frequency = 0;
	// 0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	public byte state = 1;

	public UUID ownerUUID;
	public String ownerName = "";
	
	public int[] network = new int[0];
	public boolean isPublic = false;
	
	public final PipeType type;

	public PipeTeleport(pipeType transport, Item item, PipeType type)
	{
		super(transport, item);
		this.type = type;
		
		
		//initialize the static reflection fields
		if(receiveSignal == null)
		{	
			try
			{				
				//go up from PipeTeleportX to Pipe
				Class<?> pipeClass = getClass().getSuperclass().getSuperclass().getSuperclass();
				
				for(Field field : pipeClass.getDeclaredFields())
				{
					if(field.getName().equals("internalUpdateScheduled"))
					{
						field.setAccessible(true);
						internalUpdateScheduled = field;
					}
				}
	
				receiveSignal = pipeClass.getDeclaredMethod("receiveSignal", new Class<?>[]{int.class, PipeWire.class});
				
				receiveSignal.setAccessible(true);
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public byte getState()
	{
		return state;
	}

	public void setState(byte state)
	{
		this.state = state;
	}

	public UUID getOwnerUUID()
	{
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID)
	{
		this.ownerUUID = ownerUUID;
	}

	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	public boolean isPublic()
	{
		return isPublic;
	}

	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}

	public PipeType getType()
	{
		return type;
	}

	@Override
	public void initialize() {
		super.initialize();
		TeleportManager.instance.add(this, frequency);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		TeleportManager.instance.remove(this, frequency);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		TeleportManager.instance.remove(this, frequency);
	}
	
	@Override
	public boolean blockActivated(EntityPlayer player) {
		if(!AdditionalPipes.proxy.isServer(player.worldObj))
			return true;
		if(ownerUUID == null)
		{
			//                   getUUIDFromProfile()
			ownerUUID = PlayerUtils.getUUID(player);
			ownerName = player.getCommandSenderName();
		}
		
		//test for player name change
		if(PlayerUtils.getUUID(player).equals(ownerUUID))
		{
			if(!player.getCommandSenderName().equals(ownerName))
			{
				ownerName = player.getCommandSenderName();
			}
		}
		
		if(APConfiguration.filterRightclicks)
		{
			ItemStack equippedItem = player.getCurrentEquippedItem();
			
			if (equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem()))
			{
				return false;
			}
		}
		
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_TP, getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	public void setFrequency(int freq) {
		frequency = freq;
	}

	public int getFrequency() {
		return frequency;
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		Pipe<?> pipe = null;
		if(tile instanceof TileGenericPipe) {
			pipe = ((TileGenericPipe) tile).pipe;
		}
		if(pipe != null && pipe instanceof PipeTeleport)
			return false;
		return pipe != null;
	}

	@Override
	public boolean outputOpen(ForgeDirection to) {
		return container.isPipeConnected(to);
	}
	
	// Teleport Gates stuff
	// ---------------------------------------------
	
	//we need to access stuff private to Pipe, so we use reflection to do it.
	static Field internalUpdateScheduled;
	
	static Method receiveSignal;

	@Override
	public void updateSignalState() 
	{
		ArrayList<PipeTeleport<?>> otherTeleportPipes = TeleportManager.instance.getConnectedPipes(this, true, true);
		
		for (PipeWire c : PipeWire.values()) 
		{
			myUpdateSignalStateForColor(c, otherTeleportPipes);
		}
	}

	//we cannot override this because it is private
	//so we override the function that calls it and invoke our version.
	private void myUpdateSignalStateForColor(PipeWire wire, ArrayList<PipeTeleport<?>> otherTeleportPipes)
	{
		if (!wireSet[wire.ordinal()]) {
			return;
		}
		try
		{
			
			// STEP 1: compute internal signal strength

			boolean readNearbySignal = true;
			for (Gate gate : gates) 
			{
				if (gate != null && gate.broadcastSignal.get(wire.ordinal()))
				{
					receiveSignal.invoke(this, 255, wire);
					readNearbySignal = false;
				}
			}
			
			if (readNearbySignal)
			{
				myReadNearbyPipesSignal(wire, otherTeleportPipes);
			}

			// STEP 2: transmit signal in nearby blocks

			if (signalStrength[wire.ordinal()] > 1)
			{
				for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
					TileEntity tile = container.getTile(o);

					if (tile instanceof IPipeTile) {
						IPipeTile tilePipe = (IPipeTile) tile;
						Pipe<?> pipe = (Pipe<?>) tilePipe.getPipe();

						if (BlockGenericPipe.isFullyDefined(pipe) && pipe.wireSet[wire.ordinal()]) {
							if (isWireConnectedTo(tile, wire, o)) {
								receiveSignal.invoke(pipe, signalStrength[wire.ordinal()] - 1, wire);
							}
						}
					}
				}
				
				for(PipeTeleport<?> pipe : otherTeleportPipes)
				{
					if (BlockGenericPipe.isFullyDefined(pipe) && pipe.wireSet[wire.ordinal()])
					{
						receiveSignal.invoke(pipe, signalStrength[wire.ordinal()] - 1, wire);
					}
				}
			}
		}
		
		//Man, I really want to be able to use Multi-Catch here
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{	
			e.printStackTrace();
		}
	}
	
	private void myReadNearbyPipesSignal(PipeWire color, ArrayList<PipeTeleport<?>> otherTeleportPipes) 
	{
		boolean foundBiggerSignal = false;

		try
		{
			
			for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = container.getTile(o);
	
				if (tile instanceof IPipeTile) {
					IPipeTile tilePipe = (IPipeTile) tile;
					Pipe<?> pipe = (Pipe<?>) tilePipe.getPipe();
	
					if (BlockGenericPipe.isFullyDefined(pipe))
					{
						if (isWireConnectedTo(tile, color, o)) {
							foundBiggerSignal |= ((Boolean)receiveSignal.invoke(this, pipe.signalStrength[color.ordinal()] - 1, color)).booleanValue();
						}
					}
				}
			}
			
			//receive ACROSS THE BOUNDS OF SPACE AND TIME!!!!!
			for(PipeTeleport<?> pipe : otherTeleportPipes)
			{
				if (BlockGenericPipe.isFullyDefined(pipe))
				{
					foundBiggerSignal |= ((Boolean)receiveSignal.invoke(this, pipe.signalStrength[color.ordinal()] - 1, color));
				}
			}
	
			if (!foundBiggerSignal && signalStrength[color.ordinal()] != 0) {
				signalStrength[color.ordinal()] = 0;
				// worldObj.markBlockNeedsUpdate(container.xCoord, container.yCoord, zCoord);
				container.scheduleRenderUpdate();
	
				for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
					TileEntity tile = container.getTile(o);
	
					if (tile instanceof IPipeTile) {
						IPipeTile tilePipe = (IPipeTile) tile;
						Pipe<?> pipe = (Pipe<?>) tilePipe.getPipe();
	
						if (BlockGenericPipe.isFullyDefined(pipe)) {
							
								internalUpdateScheduled.set(pipe, Boolean.TRUE);
	
						}
					}
				}
				
				for(PipeTeleport<?> pipe : otherTeleportPipes)
				{
					if (BlockGenericPipe.isFullyDefined(pipe))
					{
						internalUpdateScheduled.set(pipe, Boolean.TRUE);
					}
				}
			}
		
		} 
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("freq", frequency);
		nbttagcompound.setByte("state", state);
		if(ownerUUID != null)
		{
			nbttagcompound.setString("ownerUUID", ownerUUID.toString());
			nbttagcompound.setString("ownerName", ownerName);
		}
		nbttagcompound.setBoolean("isPublic", isPublic);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		frequency = nbttagcompound.getInteger("freq");
		state = nbttagcompound.getByte("state");
		if(nbttagcompound.hasKey("ownerUUID"))
		{
			ownerUUID = UUID.fromString(nbttagcompound.getString("ownerUUID"));
			ownerName = nbttagcompound.getString("ownerName");
		}
		isPublic = nbttagcompound.getBoolean("isPublic");
	}

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeTeleport<?> pipe) {
		if(pipe.isPublic || pipe.ownerUUID.equals(PlayerUtils.getUUID(player)) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

	public Position getPosition() {
		return new Position(container.xCoord, container.yCoord, container.zCoord);
	}
}
