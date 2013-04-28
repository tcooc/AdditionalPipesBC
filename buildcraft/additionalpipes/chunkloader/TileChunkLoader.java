package buildcraft.additionalpipes.chunkloader;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import buildcraft.additionalpipes.AdditionalPipes;

public class TileChunkLoader extends TileEntity {

	private Ticket chunkTicket;
	private int loadDistance = 1;

	public List<ChunkCoordIntPair> getLoadArea() {
		List<ChunkCoordIntPair> loadArea = new LinkedList<ChunkCoordIntPair>();

		for (int x = -loadDistance; x < loadDistance + 1; x++) {
			for (int z = -loadDistance; z < loadDistance + 1; z++) {
				ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(
						(xCoord >> 4) + x, (zCoord >> 4) + z);

				loadArea.add(chunkCoords);
			}
		}

		return loadArea;
	}

	@Override
	public void validate() {
		super.validate();
		if (!worldObj.isRemote && chunkTicket == null) {
			Ticket ticket = ForgeChunkManager.requestTicket(
					AdditionalPipes.instance, worldObj, Type.NORMAL);
			if (ticket != null) {
				forceChunkLoading(ticket);
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		stopChunkLoading();
	}

	public void setLoadDistance(int dist) {
		loadDistance = dist;
		forceChunkLoading(chunkTicket);
	}

	public void forceChunkLoading(Ticket ticket) {
		stopChunkLoading();
		chunkTicket = ticket;
		for (ChunkCoordIntPair coord : getLoadArea()) {
			AdditionalPipes.instance.logger.info(
					String.format("Force loading chunk %s in %s",
							coord, worldObj.provider.getClass()));
			ForgeChunkManager.forceChunk(chunkTicket, coord);
		}
	}

	public void unforceChunkLoading() {
		for (Object obj : chunkTicket.getChunkList()) {
			ChunkCoordIntPair coord = (ChunkCoordIntPair) obj;
			ForgeChunkManager.unforceChunk(chunkTicket, coord);
		}
	}

	public void stopChunkLoading() {
		if (chunkTicket != null) {
			ForgeChunkManager.releaseTicket(chunkTicket);
			chunkTicket = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
	}
}
