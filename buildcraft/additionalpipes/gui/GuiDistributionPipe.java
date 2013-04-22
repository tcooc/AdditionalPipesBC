package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.logic.PipeLogicDistributor;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDistributionPipe extends GuiContainer {

	protected int xSize;
	protected int ySize;
	private GuiButton[] buttons = new GuiButton[18];
	public int guiX = 0;
	public int guiY = 0;
	TileGenericPipe a;
	PipeItemsDistributor pipe;

	public GuiDistributionPipe(TileGenericPipe container) {
		super(new ContainerDistributionPipe(container));

		a = container;
		pipe = (PipeItemsDistributor) container.pipe;
		xSize = 175;
		ySize = 130;

	}

	@Override
	public void initGui() {
		super.initGui();
		//int bw = this.xSize - 20;
		int guiX = (width - xSize) / 2 + 30;
		int guiY = (height - ySize) / 2 - 10;

		buttonList.add(buttons[0]  =  new GuiButton(1, guiX + 1,       guiY + 24, 20, 17, "-"));
		buttonList.add(buttons[1]  =  new GuiButton(2, guiX + 3 + 20,  guiY + 24, 30, 17, "0"));
		buttonList.add(buttons[2]  =  new GuiButton(3, guiX + 5 + 50,  guiY + 24, 20, 17, "+"));

		buttonList.add(buttons[3]  =  new GuiButton(4, guiX + 1,       guiY + 25 + 17, 20, 17, "-"));
		buttonList.add(buttons[4]  =  new GuiButton(5, guiX + 3 + 20,  guiY + 25 + 17, 30, 17, "0"));
		buttonList.add(buttons[5]  =  new GuiButton(6, guiX + 5 + 50,  guiY + 25 + 17, 20, 17, "+"));

		buttonList.add(buttons[6]  =  new GuiButton(7, guiX + 1,       guiY + 26 + 17 * 2, 20, 17, "-"));
		buttonList.add(buttons[7]  =  new GuiButton(8, guiX + 3 + 20,  guiY + 26 + 17 * 2, 30, 17, "0"));
		buttonList.add(buttons[8]  =  new GuiButton(9, guiX + 5 + 50,  guiY + 26 + 17 * 2, 20, 17, "+"));

		buttonList.add(buttons[9]  =  new GuiButton(10, guiX + 1,      guiY + 27 + 17 * 3, 20, 17, "-"));
		buttonList.add(buttons[10] =  new GuiButton(11, guiX + 3 + 20, guiY + 27 + 17 * 3, 30, 17, "0"));
		buttonList.add(buttons[11] =  new GuiButton(12, guiX + 5 + 50, guiY + 27 + 17 * 3, 20, 17, "+"));

		buttonList.add(buttons[12] =  new GuiButton(13, guiX + 1,      guiY + 28 + 17 * 4, 20, 17, "-"));
		buttonList.add(buttons[13] =  new GuiButton(14, guiX + 3 + 20, guiY + 28 + 17 * 4, 30, 17, "0"));
		buttonList.add(buttons[14] =  new GuiButton(15, guiX + 5 + 50, guiY + 28 + 17 * 4, 20, 17, "+"));

		buttonList.add(buttons[15] =  new GuiButton(16, guiX + 1,      guiY + 29 + 17 * 5, 20, 17, "-"));
		buttonList.add(buttons[16] =  new GuiButton(17, guiX + 3 + 20, guiY + 29 + 17 * 5, 30, 17, "0"));
		buttonList.add(buttons[17] =  new GuiButton(18, guiX + 5 + 50, guiY + 29 + 17 * 5, 20, 17, "+"));

	}
	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		PipeLogicDistributor pipeLogic = pipe.logic;
		buttons[1].displayString  = "" + pipeLogic.distData[0];
		buttons[4].displayString  = "" + pipeLogic.distData[1];
		buttons[7].displayString  = "" + pipeLogic.distData[2];
		buttons[10].displayString = "" + pipeLogic.distData[3];
		buttons[13].displayString = "" + pipeLogic.distData[4];
		buttons[16].displayString = "" + pipeLogic.distData[5];
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		PipeLogicDistributor pipeLogic = pipe.logic;
		int index = (guibutton.id - 1) / 3;
		int newData = pipeLogic.distData[index];
		if((guibutton.id - 1) % 3 == 0) {
			newData--;
		} else {
			newData++;
		}
		/* //Old code
		switch (guibutton.id) {
		case 1:
			index = 0;
			pipeLogic.distData[0] -= 1;
			break;
		case 3:
			index = 0;
			pipeLogic.distData[0] += 1;
			break;
		case 4:
			pipeLogic.distData[1] -= 1;
			break;
		case 6:
			pipeLogic.distData[1] += 1;
			break;
		case 7:
			pipeLogic.distData[2] -= 1;
			break;
		case 9:
			pipeLogic.distData[2] += 1;
			break;
		case 10:
			pipeLogic.distData[3] -= 1;
			break;
		case 12:
			pipeLogic.distData[3] += 1;
			break;
		case 13:
			pipeLogic.distData[4] -= 1;
			break;
		case 15:
			pipeLogic.distData[4] += 1;
			break;
		case 16:
			pipeLogic.distData[5] -= 1;
			break;
		case 18:
			pipeLogic.distData[5] += 1;
			break;
		}
		 */

		if(newData < 0) return;

		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.DIST_PIPE_DATA, true);
		packet.writeInt(pipe.xCoord);
		packet.writeInt(pipe.yCoord);
		packet.writeInt(pipe.zCoord);
		packet.write((byte) index);
		packet.writeInt(newData);
		PacketDispatcher.sendPacketToServer(packet.makePacket());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(AdditionalPipes.TEXTURE_GUI_DISTRIBUTION);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

	}

}
