package buildcraft.additionalpipes.pipes.logic;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicTeleport extends PipeLogic
{

	public boolean canReceive = false;
	//public boolean canSend = true;
	public String owner = "";
	public boolean isPublic = false;
	private Pipe pipe;
	private boolean[] phasedBroadcastSignal = new boolean[4];
	private int frequency = 0;

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeLogicTeleport logic)
	{
		if (logic.isPublic || logic.owner.equals(player.username) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

	@Override
	public void setTile(TileGenericPipe tile)
	{
		super.setTile(tile);
		pipe = container.pipe;
	}

	@Override
	public boolean blockActivated(EntityPlayer player)
	{
		if (!AdditionalPipes.proxy.isServer(player.worldObj)) return true;
		if (owner == null || "".equalsIgnoreCase(owner)) {
			owner = player.username;
		}
		ItemStack equippedItem = player.getCurrentEquippedItem();
		/*if (equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem()))  {
			return false;
		}*/
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_TP, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		updatePhasedSignals();
	}

	public int getFrequency()
	{
		return frequency;
	}

	public void setFrequency(int freq)
	{
		removePhasedSignals();
		frequency = freq;
		phasedBroadcastSignal = new boolean[4];
		updatePhasedSignals();
	}

	public void removePhasedSignals()
	{
		for (int i = 0; i < phasedBroadcastSignal.length; i++) {
			if (phasedBroadcastSignal[i]) {
				TeleportManager.instance.phasedSignals.get(frequency)[i]--;
				AdditionalPipes.instance.logger.info("Removing signal " + frequency + " : " + i);
			}
		}
	}

	public void updatePhasedSignals()
	{
		if (!TeleportManager.instance.phasedSignals.containsKey(frequency)) {
			Integer[] signals = new Integer[4];
			Arrays.fill(signals, 0);
			TeleportManager.instance.phasedSignals.put(frequency, signals);
		}
		for (int i = 0; i < pipe.broadcastSignal.length; i++) {
			if (phasedBroadcastSignal[i] != pipe.broadcastSignal[i]) {
				TeleportManager.instance.phasedSignals.get(frequency)[i] += (pipe.broadcastSignal[i] ? 1 : -1);
				AdditionalPipes.instance.logger.info((pipe.broadcastSignal[i] ? "Adding signal " : "Removing signal ") + frequency + " : " + i);
				phasedBroadcastSignal[i] = pipe.broadcastSignal[i];
			}
		}
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side)
	{

		if (tile instanceof TileGenericPipe) {
			Pipe pipe = ((TileGenericPipe) tile).pipe;
			if (pipe != null && this.getClass().equals(pipe.logic.getClass())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean outputOpen(ForgeDirection to)
	{
		return canPipeConnect(container.getTile(to), to);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("freq", frequency);
		nbttagcompound.setBoolean("canReceive", canReceive);
		nbttagcompound.setString("owner", owner);
		nbttagcompound.setBoolean("isPublic", isPublic);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);

		frequency = nbttagcompound.getInteger("freq");
		canReceive = nbttagcompound.getBoolean("canReceive");
		owner = nbttagcompound.getString("owner");
		isPublic = nbttagcompound.getBoolean("isPublic");
	}

}
