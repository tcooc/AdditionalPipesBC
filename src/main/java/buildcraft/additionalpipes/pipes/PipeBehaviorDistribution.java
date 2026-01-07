/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipeHolder.PipeMessageReceiver;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.api.transport.pipe.PipeEventItem.ItemEntry;
import buildcraft.lib.misc.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PipeBehaviorDistribution extends APPipe {

	public int distData[] = { 1, 1, 1, 1, 1, 1 };
	public EnumFacing distSide = EnumFacing.UP; // will get initialized on first toNextOpenSide() call
	public int itemsThisSide = Integer.MAX_VALUE; // normally ranges from 0 to distData[distSide] - 1

	public PipeBehaviorDistribution(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
		
		itemsThisSide = nbt.getInteger("itemsThisSide");
		distSide = EnumFacing.VALUES[nbt.getInteger("distSide")];
		for(int i = 0; i < distData.length; i++) {
			distData[i] = nbt.getInteger("distData" + i);
		}
		sanityCheck();
	}

	public PipeBehaviorDistribution(IPipe pipe)
	{
		super(pipe);
	}

	@Override
	public int getTextureIndex(EnumFacing connection)
	{
		if(connection == null)
		{
			return EnumFacing.EAST.ordinal();
		}
		
		return connection.ordinal();
	}
	
	/**
	 * @brief Event handler for the SideCheck event, which is called before the Split event.
	 */
	@PipeEventHandler
	public void sideCheck(PipeEventItem.SideCheck sideCheckEvent) 
	{
		// Disallow all sides that have a distData value of 0 (all items disallowed).
		// This is important to do so that if all distData values for connected sides are 0, we will just drop the item as it has nowhere to go.
		// Note that we could also "bounce" the item back out of the distribution pipe by handling the TryBounce event, but dropping them
		// seems simpler for now.
		for(int o = 0; o < distData.length; ++o) 
		{
			if(distData[o] == 0)
			{
				sideCheckEvent.disallow(EnumFacing.VALUES[o]);
			}
		}
	}
	
	@PipeEventHandler
	public void splitStacks(PipeEventItem.Split splitEvent) 
	{
		ArrayList<ItemEntry>  newDistribution = new ArrayList<>();
		
		
		for(ItemEntry entry : splitEvent.items)
		{
			Log.debug("[PipeDistribution] current side: " + distSide.getName() + " items left this side: " + getItemsLeftThisSide());
			
			if(entry.to == null)
			{
				entry.to = new ArrayList<EnumFacing>();
			}


			if(getItemsLeftThisSide() > 0 && entry.stack.getCount() <= getItemsLeftThisSide())
			{
				// easy
				entry.to.clear(); 
				entry.to.add(distSide);
				itemsThisSide += entry.stack.getCount();
				
				newDistribution.add(entry);
			}
			else // if there are more items in this stack than there are left for this side, we will have to split the stack		
			{
				while(entry.stack.getCount() > 0)
				{
					if(getItemsLeftThisSide() <= 0)
					{
						if(!toNextOpenSide())
						{
							// *shouldn't* be possible to get here due to the sideCheck() event handler, but keeping this logic just in case as
							// otherwise we would hit an infinite hang.
							Log.error("Failed to distribute itemstack. Allowing it to be routed randomly.");
							entry.to.clear();
							newDistribution.add(entry);
							break;
						}
					}
					
					ItemEntry stackPartThisSide = new ItemEntry(null, entry.stack.copy(), entry.from);
					stackPartThisSide.to = new ArrayList<>();
					
					stackPartThisSide.stack.setCount(Math.min(getItemsLeftThisSide(), entry.stack.getCount())); // take as many items as the distribution will allow
					entry.stack.setCount(entry.stack.getCount() - stackPartThisSide.stack.getCount()); // and leave event.stack with the remainder
					
					newDistribution.add(stackPartThisSide);
					itemsThisSide += stackPartThisSide.stack.getCount();
					stackPartThisSide.to.add(distSide);
				}
			}
			
		}
		
		splitEvent.items.clear();
		splitEvent.items.addAll(newDistribution);
	}
	

	/**
	 * Moves the pipe to the next open side.
	 * 
	 * @return true if there is another open side that can accept an item stack, false otherwise.
	 */
	private boolean toNextOpenSide() 
	{		
		itemsThisSide = 0;
		for(int o = 0; o < distData.length; ++o) 
		{
			distSide = EnumFacing.VALUES[(distSide.ordinal() + 1) % distData.length];
			if(distData[distSide.ordinal()] > 0 && pipe.isConnected(distSide))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return the number of items left that this side can output before switching to the next one
	 */
	private int getItemsLeftThisSide()
	{
		if(!pipe.isConnected(distSide))
		{
			return 0;
		}
		
		return distData[distSide.ordinal()] - itemsThisSide;
	}

	@Override
    public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part) 
	{
        if (EntityUtil.getWrenchHand(player) != null) 
        {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        
        if(!player.world.isRemote) 
        {
        	// fire off an update packet to the client
        	pipe.getHolder().scheduleNetworkUpdate(PipeMessageReceiver.BEHAVIOUR);
        	
        	BlockPos pipePos = pipe.getHolder().getPipePos();
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_DIST, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
    }

	private void sanityCheck()
	{
		for(int d : distData) {
			if(d > 0) {
				return;
			}
		}
		for(int i = 0; i < distData.length; i++) {
			Arrays.fill(distData, 1);
		}
	}

	@Override
	public NBTTagCompound writeToNbt() {
		NBTTagCompound nbt = super.writeToNbt();

		nbt.setInteger("itemsThisSide", itemsThisSide);
		nbt.setInteger("distSide", distSide.ordinal());
		for(int i = 0; i < distData.length; i++) 
		{
			nbt.setInteger("distData" + i, distData[i]);
		}
		
		return nbt;
	}
	
	
    @Override
    public void writePayload(PacketBuffer buffer, Side side) 
    {
        super.writePayload(buffer, side);
        if (side == Side.SERVER) 
        {
        	buffer.writeInt(itemsThisSide);
        	buffer.writeInt(distSide.ordinal());
        	
        	for(int i = 0; i < distData.length; i++) 
    		{
        		buffer.writeInt(distData[i]);
    		}
        }
    }
    
    @Override
    public void readPayload(PacketBuffer buffer, Side side, MessageContext ctx) throws IOException
    {
        super.readPayload(buffer, side, ctx);
        if (side == Side.CLIENT) 
        {
            itemsThisSide = buffer.readInt();
            distSide = EnumFacing.values()[buffer.readInt()];
            
            for(int i = 0; i < distData.length; i++) 
    		{
            	distData[i] = buffer.readInt();
    		}
        }
    }



}
