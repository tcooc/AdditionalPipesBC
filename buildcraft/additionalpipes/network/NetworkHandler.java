package buildcraft.additionalpipes.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.logic.PipeLogicAdvancedWood;
import buildcraft.additionalpipes.pipes.logic.PipeLogicDistributor;
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class NetworkHandler implements IPacketHandler {
	//server to client
	public static final byte CHUNKLOAD_DATA = 15;
	public static final byte TELE_PIPE_DATA = 16;
	//client to server
	public static final byte ADV_WOOD_DATA = 62;
	public static final byte DIST_PIPE_DATA = 63;
	public static final byte TELE_PIPE_DATA_SET = 64;
	public static final byte CHUNKLOAD_REQUEST = 65;

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(AdditionalPipes.CHANNEL.equals(packet.channel)) {
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
			byte packetID = -1;
			try {
				packetID = data.readByte();
				switch(packetID) {
				case ADV_WOOD_DATA:
					TileEntity te = getTileEntity(player, data);
					if(te instanceof TileGenericPipe) {
						PipeLogic logic = ((TileGenericPipe) te).pipe.logic;
						PipeLogicAdvancedWood advLogic = (PipeLogicAdvancedWood) logic;
						advLogic.exclude = !advLogic.exclude;
					}
					break;
				case DIST_PIPE_DATA:
					handleDistPipeData(player, data);
					break;
				case TELE_PIPE_DATA_SET:
					handleTelePipeData(player, data);
					break;
				case CHUNKLOAD_DATA:
					handleChunkLoadData(data);
					break;
				case CHUNKLOAD_REQUEST:
					AdditionalPipes.instance.chunkLoadViewer
					.sendPersistentChunksToPlayer((EntityPlayerMP) player);
					break;
				}
			} catch (IOException e) {
				AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling packet " + packetID, e);
			}
		} else if(AdditionalPipes.CHANNELNBT.equals(packet.channel)) {
			NBTTagCompound tag = PacketNBTTagData.getNBTFrom(packet);
			switch(tag.getInteger("id")) {
			case TELE_PIPE_DATA:
				TileEntity te = ((EntityPlayer) player).worldObj.getBlockTileEntity(tag.getInteger("xCoord"), tag.getInteger("yCoord"), tag.getInteger("zCoord"));
				if(te instanceof TileGenericPipe) {
					PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
					pipe.logic.owner = tag.getString("owner");
				}
				break;
			}
		}
	}

	private void handleDistPipeData(Player player, DataInputStream data) {
		try {
			TileEntity te = getTileEntity(player, data);
			if(te instanceof TileGenericPipe) {
				int index = data.read();
				int newData = data.readInt();
				PipeLogicDistributor logic = (PipeLogicDistributor) ((TileGenericPipe) te).pipe.logic;

				if(newData >= 0 && index >= 0 && index < logic.distData.length) {
					logic.distData[index] = newData;
					boolean found = newData > 0;
					if(!found) {
						for (int i = 0; i < logic.distData.length; i++) {
							if (logic.distData[i] > 0) {
								found = true;
							}
						}
					}
					if (!found) {
						for (int i = 0; i < logic.distData.length; i++) {
							logic.distData[i] = 1;
						}
					}

				}
			}
		} catch (IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling distribution pipe packet.", e);
		}
	}

	private void handleTelePipeData(Player player, DataInputStream data) {
		try {
			TileEntity te = getTileEntity(player, data);
			if(te instanceof TileGenericPipe) {
				PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
				//only allow the owner to change pipe state
				EntityPlayerMP entityPlayer = (EntityPlayerMP) player;
				if(!PipeLogicTeleport.canPlayerModifyPipe(entityPlayer, pipe.logic)) {
					entityPlayer.sendChatToPlayer("You may not change pipe state.");
					return;
				}
				int frequency = data.readInt();
				if(frequency < 0) {
					frequency = 0;
				}
				pipe.logic.setFrequency(frequency);
				pipe.logic.canReceive = (data.read() == 1);
				pipe.logic.isPublic = (data.read() == 1);
			}
		} catch (IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling teleport pipe packet.", e);
		}
	}

	private void handleChunkLoadData(DataInputStream data) {
		try {
			int size = data.readInt();
			ChunkCoordIntPair[] chunks = new ChunkCoordIntPair[size];
			for(int i = 0; i < size; i++) {
				chunks[i] = new ChunkCoordIntPair(data.readInt(), data.readInt());
			}
			AdditionalPipes.instance.chunkLoadViewer.receivePersistentChunks(chunks);
		} catch (IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling chunk load data.", e);
		}
	}

	public TileEntity getTileEntity(Player player, DataInputStream data) {
		int x, y, z;
		TileEntity te = null;
		try {
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = ((EntityPlayer) player).worldObj.getBlockTileEntity(x, y, z);
		} catch (IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error getting tileentity position from packet.", e);
		}
		return te;
	}
}
