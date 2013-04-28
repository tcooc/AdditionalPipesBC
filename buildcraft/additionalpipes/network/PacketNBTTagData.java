package buildcraft.additionalpipes.network;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;

//general purpose NBT class
//send these packets using a unique channel so all packets can be decoded with getNBTFrom
public class PacketNBTTagData {

	public final Packet250CustomPayload packet;
	public final NBTTagCompound nbt;

	public PacketNBTTagData(String channel, int id, boolean chunkPacket,
			NBTTagCompound nbttagcompound) {
		packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.isChunkDataPacket = chunkPacket;
		nbt = nbttagcompound;
	}

	public Packet250CustomPayload getPacket() {
		try {
			byte[] byteData = CompressedStreamTools.compress(nbt);
			packet.length = byteData.length;
			packet.data = byteData;
		} catch (IOException e) {
			//impossible?
		}
		return packet;
	}

	public static NBTTagCompound getNBTFrom(Packet250CustomPayload packet) {
		NBTTagCompound tag = new NBTTagCompound();
		try {
			tag = CompressedStreamTools.decompress(packet.data);
		} catch (IOException e) {
			//severe error reading packet data
		}
		return tag;
	}
}
