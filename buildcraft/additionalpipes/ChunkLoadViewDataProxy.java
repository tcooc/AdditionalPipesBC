package buildcraft.additionalpipes;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import buildcraft.api.core.LaserKind;
import buildcraft.core.Box;

import com.google.common.collect.SetMultimap;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChunkLoadViewDataProxy implements IScheduledTickHandler, Comparator<ChunkCoordIntPair> {
	public static final int MAX_SIGHT_RANGE = 31;

	//used by server
	private int sightRange;

	//used by client
	private List<Box> lasers;
	private ChunkCoordIntPair[] persistentChunks;
	private boolean active = false;

	public ChunkLoadViewDataProxy(int chunkSightRange) {
		setSightRange(chunkSightRange);
		lasers = new LinkedList<Box>();
		persistentChunks = new ChunkCoordIntPair[0];
		active = false;
	}

	//laser methods

	@SideOnly(Side.CLIENT)
	public void toggleLasers(){
		if(lasersActive()){
			deactivateLasers();
		} else {
			activateLasers();
		}
	}

	@SideOnly(Side.CLIENT)
	public void activateLasers(){
		deactivateLasers();
		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		int playerY = (int) player.posY - 1;
		for (ChunkCoordIntPair coords : persistentChunks) {
			int xCoord = coords.chunkXPos * 16;
			int zCoord = coords.chunkZPos * 16;

			Box outsideLaser = new Box();
			outsideLaser.initialize(xCoord, playerY, zCoord, xCoord + 16, playerY, zCoord + 16);
			outsideLaser.createLasers(player.worldObj, LaserKind.Blue);
			lasers.add(outsideLaser);
			outsideLaser = new Box();
			outsideLaser.initialize(xCoord, playerY - 20, zCoord, xCoord + 16, playerY - 20, zCoord + 16);
			outsideLaser.createLasers(player.worldObj, LaserKind.Blue);
			lasers.add(outsideLaser);
			outsideLaser = new Box();
			outsideLaser.initialize(xCoord, playerY + 20, zCoord, xCoord + 16, playerY + 20, zCoord + 16);
			outsideLaser.createLasers(player.worldObj, LaserKind.Blue);
			lasers.add(outsideLaser);


			Box insideLaser = new Box();
			insideLaser.initialize(xCoord + 7, playerY, zCoord + 7, xCoord + 9, playerY, zCoord + 9);
			insideLaser.createLasers(player.worldObj, LaserKind.Red);
			lasers.add(insideLaser);
			insideLaser = new Box();
			insideLaser.initialize(xCoord + 7, playerY - 20, zCoord + 7, xCoord + 9, playerY - 20, zCoord + 9);
			insideLaser.createLasers(player.worldObj, LaserKind.Red);
			lasers.add(insideLaser);
			insideLaser = new Box();
			insideLaser.initialize(xCoord + 7, playerY + 20, zCoord + 7, xCoord + 9, playerY + 20, zCoord + 9);
			insideLaser.createLasers(player.worldObj, LaserKind.Red);
			lasers.add(insideLaser);

		}
		active = true;
	}

	@SideOnly(Side.CLIENT)
	public void deactivateLasers(){
		for (Box laser : lasers) {
			laser.deleteLasers();
		}
		lasers.clear();
		active = false;
	}

	@SideOnly(Side.CLIENT)
	public boolean lasersActive(){
		return active;
	}

	//packet methods

	@SideOnly(Side.CLIENT)
	public void requestPersistentChunks() {
		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.CHUNKLOAD_REQUEST, false);
		PacketDispatcher.sendPacketToServer(packet.makePacket());
	}

	@SideOnly(Side.CLIENT)
	public void receivePersistentChunks(ChunkCoordIntPair[] chunks) {
		boolean changed = true;
		//check if arrays have equal contents
		//do this on the client since it's only rendering, and it reduces server load
		if(persistentChunks.length == chunks.length) {
			changed = false;
			Arrays.sort(chunks, this);
			Arrays.sort(persistentChunks, this);
			for(int i = 0; i < chunks.length; i++) {
				if(!chunks[i].equals(persistentChunks[i])) {
					changed = true;
					break;
				}
			}
		}

		if(changed) {
			persistentChunks = chunks;
			if(active) {
				activateLasers();
			}
		}
	}

	//sets how far the server will search for chunkloaded chunks
	//when sending data to the player
	public void setSightRange(int range) {
		sightRange = range;
		if(sightRange > MAX_SIGHT_RANGE)
			sightRange = MAX_SIGHT_RANGE;

	}

	public void sendPersistentChunksToPlayer(EntityPlayerMP player) {
		if(!AdditionalPipes.instance.chunkSight) { return;}
		if(sightRange > MAX_SIGHT_RANGE) sightRange = MAX_SIGHT_RANGE;

		SetMultimap<ChunkCoordIntPair, Ticket> persistentChunks =
				ForgeChunkManager.getPersistentChunksFor(player.worldObj);
		List<ChunkCoordIntPair> chunksInRange = new LinkedList<ChunkCoordIntPair>();
		int playerX = (((int) player.posX) >> 4) - sightRange / 2,
				playerZ = (((int) player.posZ) >> 4) - sightRange / 2;

		for(int i = -sightRange; i  <= sightRange; i++) {
			for(int j = -sightRange; j <= sightRange; j++) {
				ChunkCoordIntPair coords = new ChunkCoordIntPair(playerX + i, playerZ + j);
				if(persistentChunks.containsKey(coords)) {
					chunksInRange.add(coords);
				}
			}
		}

		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.CHUNKLOAD_DATA, false);
		packet.writeInt(chunksInRange.size());
		for(ChunkCoordIntPair coords : chunksInRange) {
			packet.writeInt(coords.chunkXPos);
			packet.writeInt(coords.chunkZPos);
		}
		player.playerNetServerHandler.sendPacketToPlayer(packet.makePacket());
		AdditionalPipes.instance.logger.info("[ChunkLoadViewDataProxy] Sent chunks within " + sightRange + " of player.");
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}

	@Override
	@SideOnly(Side.CLIENT)
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(AdditionalPipes.instance.chunkSightAutorefresh && lasersActive()) {
			requestPersistentChunks();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return getClass().getSimpleName();
	}

	@Override
	public int nextTickSpacing() {
		return 20 * 5;
	}

	//Comparator

	//first - other
	//assume non-null
	@Override
	public int compare(ChunkCoordIntPair first, ChunkCoordIntPair other) {
		int dx = first.chunkXPos - other.chunkXPos;
		return dx != 0 ? dx : first.chunkZPos - other.chunkZPos;
	}
}
