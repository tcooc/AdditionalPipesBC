package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketNBTTagData;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ContainerTeleportPipe extends Container {

	public int connectedPipes = 0;
	public String owner = "";

	private int ticks = 0;
	private PipeTeleport pipe;
	private int freq;
	private boolean canReceive, isPublic;

	public ContainerTeleportPipe(EntityPlayer player, TileGenericPipe tile) {
		pipe = (PipeTeleport) tile.pipe;
		canReceive = !pipe.logic.canReceive;
		isPublic = !pipe.logic.isPublic;
		freq = -1;

		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", NetworkHandler.TELE_PIPE_DATA);
		tag.setInteger("xCoord", pipe.xCoord);
		tag.setInteger("yCoord", pipe.yCoord);
		tag.setInteger("zCoord", pipe.zCoord);
		tag.setString("owner", pipe.logic.owner);
		PacketNBTTagData packet = new PacketNBTTagData(AdditionalPipes.CHANNELNBT, NetworkHandler.TELE_PIPE_DATA, false, tag);
		PacketDispatcher.sendPacketToPlayer(packet.getPacket(), (Player) player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int connectedPipesNew = connectedPipes;
		if(ticks % 40 == 0) { //reduce lag
			ticks = 0;
			AdditionalPipes.instance.logger.info("Old connected:" + connectedPipesNew);
			connectedPipesNew = TeleportManager.instance.getConnectedPipes(pipe, false).size();
			AdditionalPipes.instance.logger.info("New connected:" + connectedPipesNew);
		}
		ticks++;
		for (Object crafter : crafters) {
			if(freq != pipe.logic.getFrequency()) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 0, pipe.logic.getFrequency());
			}
			if(canReceive != pipe.logic.canReceive) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 1, pipe.logic.canReceive ? 1 : 0);
			}
			if(connectedPipesNew != connectedPipes) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 2, connectedPipesNew);
			}
			if(isPublic != pipe.logic.isPublic) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 3, pipe.logic.isPublic ? 1 : 0);
			}
		}
		canReceive = pipe.logic.canReceive;
		freq = pipe.logic.getFrequency();
		isPublic = pipe.logic.isPublic;
		connectedPipes = connectedPipesNew;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			pipe.logic.setFrequency(j);
			break;
		case 1:
			pipe.logic.canReceive = (j == 1);
			break;
		case 2:
			connectedPipes = j;
			break;
		case 3:
			pipe.logic.isPublic = (j == 1);
			break;
		}
	}

}
