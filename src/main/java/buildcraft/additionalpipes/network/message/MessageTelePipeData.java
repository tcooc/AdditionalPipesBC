package buildcraft.additionalpipes.network.message;

import java.util.UUID;

import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport;
import buildcraft.transport.tile.TilePipeHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTelePipeData implements IMessage, IMessageHandler<MessageTelePipeData, IMessage>
{
	public BlockPos position;
    public int[] locations;
    public String ownerUUID;
    public String ownerName;

    public MessageTelePipeData()
    {
    }

    public MessageTelePipeData(BlockPos position, int[] locations, UUID ownerUUID, String ownerName)
    {
    	this.position = position;
        this.locations = locations;
        this.ownerUUID = ownerUUID.toString();
        this.ownerName = ownerName;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	position = BlockPos.fromLong(buf.readLong());
        int locationsLength = buf.readInt();
        	
        this.locations = new int[locationsLength];
        
        for(int counter = 0; counter < locationsLength; ++counter)
        {
        	locations[counter] = buf.readInt();
        }
        		
        ownerUUID = ByteBufUtils.readUTF8String(buf);
        ownerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeLong(position.toLong());
        buf.writeInt(locations.length);
        for(int location : locations)
        {
        	buf.writeInt(location);
        }
        ByteBufUtils.writeUTF8String(buf, ownerUUID);
        ByteBufUtils.writeUTF8String(buf, ownerName);
    }

    @Override
    public IMessage onMessage(MessageTelePipeData message, MessageContext ctx)
    {
        TileEntity te = FMLClientHandler.instance().getClient().world.getTileEntity(message.position);

        PipeBehaviorTeleport pipe = (PipeBehaviorTeleport) ((TilePipeHolder) te).getPipe().getBehaviour();
		pipe.setOwnerUUID(UUID.fromString(message.ownerUUID));
		pipe.setOwnerName(message.ownerName);
		pipe.setNetwork(message.locations);

        return null;
    }

    @Override
    public String toString()
    {
        return "MessageTelePipeData";
    }
}
