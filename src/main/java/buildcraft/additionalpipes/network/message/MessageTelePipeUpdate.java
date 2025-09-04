package buildcraft.additionalpipes.network.message;

import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport;
import buildcraft.transport.tile.TilePipeHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the properties of a Teleport Pipe from the GUI
 *
 */
public class MessageTelePipeUpdate implements IMessage, IMessageHandler<MessageTelePipeUpdate, IMessage>
{
	public BlockPos position;
	int _freq;
	boolean _isPublic;
	byte _state;
	int _newData;
	
    public MessageTelePipeUpdate()
    {
    }

    public MessageTelePipeUpdate(BlockPos position, int freq, boolean isPublic, byte index)
    {
    	this.position = position;
    	_freq = freq;
    	_isPublic = isPublic;
    	_state = index;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	position = BlockPos.fromLong(buf.readLong());
        _freq = buf.readInt();
        _isPublic = buf.readBoolean();
        _state = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeLong(position.toLong());
        buf.writeInt(_freq);
        buf.writeBoolean(_isPublic);
        buf.writeByte(_state);
    }

    @Override
    public IMessage onMessage(MessageTelePipeUpdate message, MessageContext ctx)
    {
    	TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.position);
    	if(te instanceof TilePipeHolder) {
			PipeBehaviorTeleport pipe = (PipeBehaviorTeleport) ((TilePipeHolder) te).getPipe().getBehaviour();
			// only allow the owner to change pipe state
			EntityPlayerMP entityPlayer = (EntityPlayerMP) ctx.getServerHandler().player;
			if(!PipeBehaviorTeleport.canPlayerModifyPipe(entityPlayer, pipe)) 
			{
				entityPlayer.sendMessage(new TextComponentString("Sorry, You may not change pipe state."));
				return null;
			}
			int frequency = message._freq;
			if(frequency < 0) {
				frequency = 0;
			}
			pipe.setFrequency(frequency);
			pipe.setState(message._state);
			pipe.setPublic(message._isPublic);
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageTelePipeUpdate";
    }
}
