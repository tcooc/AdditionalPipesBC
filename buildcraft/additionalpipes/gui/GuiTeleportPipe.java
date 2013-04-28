package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleportPipe extends GuiContainer {

	private PipeTeleport pipe;
	private ContainerTeleportPipe container;
	private GuiButton[] buttons = new GuiButton[8];

	public GuiTeleportPipe(EntityPlayer player, TileGenericPipe tile) {
		super(new ContainerTeleportPipe(player, tile));

		container = (ContainerTeleportPipe) inventorySlots;
		pipe = (PipeTeleport) tile.pipe;

		xSize = 228;
		ySize = 117;
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		int bw = xSize - 22;
		buttonList.add(buttons[0] = new GuiButton(1, x + 10,              y + 52, bw / 6, 20, "-100"));
		buttonList.add(buttons[1] = new GuiButton(2, x + 12 + bw / 6,     y + 52, bw / 6, 20, "-10"));
		buttonList.add(buttons[2] = new GuiButton(3, x + 12 + bw * 2 / 6, y + 52, bw / 6, 20, "-1"));
		buttonList.add(buttons[3] = new GuiButton(4, x + 12 + bw * 3 / 6, y + 52, bw / 6, 20, "+1"));
		buttonList.add(buttons[4] = new GuiButton(5, x + 12 + bw * 4 / 6, y + 52, bw / 6, 20, "+10"));
		buttonList.add(buttons[5] = new GuiButton(6, x + 16 + bw * 5 / 6, y + 52, bw / 6, 20, "+100"));
		buttonList.add(buttons[6] = new GuiButton(7, x + 10,              y + 15, bw / 2, 20, "Send Only"));
		buttonList.add(buttons[7] = new GuiButton(8, x + 10 + bw * 3 / 6, y + 15, bw / 2, 20, "Private"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		fontRenderer.drawString("Frequency: " + pipe.logic.getFrequency(), 8, 40, 0x404040);
		fontRenderer.drawString("Number of Outputs: " + container.connectedPipes, 100, 40, 0x404040);
		fontRenderer.drawString("Owner: " + pipe.logic.owner, 8, 100, 0x404040);
		if(pipe.logic.canReceive) {
			buttons[6].displayString = "Send & Receive";
		} else {
			buttons[6].displayString = "Send Only";
		}
		if(pipe.logic.isPublic) {
			buttons[7].displayString = "Public";
		} else {
			buttons[7].displayString = "Private";
		}
		//fontRenderer.drawString("Owner: " + pipe.logic.owner, 8, 75, 0x404040);
	}
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int freq = pipe.logic.getFrequency();
		boolean canReceive = pipe.logic.canReceive;
		boolean isPublic = pipe.logic.isPublic;
		switch(guibutton.id) {
		case 1:
			freq -= 100;
			break;
		case 2:
			freq -= 10;
			break;
		case 3:
			freq -= 1;
			break;
		case 4:
			freq += 1;
			break;
		case 5:
			freq += 10;
			break;
		case 6:
			freq += 100;
			break;
		case 7:
			canReceive = !canReceive;
			break;
		case 8:
			isPublic = !isPublic;
			break;
		}
		if (freq < 0) {
			freq = 0;
		}

		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.TELE_PIPE_DATA_SET, false);
		packet.writeInt(pipe.xCoord);
		packet.writeInt(pipe.yCoord);
		packet.writeInt(pipe.zCoord);
		packet.writeInt(freq);
		packet.write((byte) (canReceive ? 1 : 0));
		packet.write((byte) (isPublic ? 1 : 0));
		PacketDispatcher.sendPacketToServer(packet.makePacket());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(AdditionalPipes.TEXTURE_GUI_TELEPORT);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

	}

}
