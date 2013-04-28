/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicAdvancedWood;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedWoodPipe extends GuiContainer {

	int inventoryRows = 1;
	IInventory playerInventory;
	IInventory filterInventory;
	TileGenericPipe container;
	private GuiButton[] buttons = new GuiButton[1];

	public GuiAdvancedWoodPipe(IInventory playerInventorys, TileGenericPipe container) {

		super(new ContainerAdvancedWoodPipe(playerInventorys, (PipeLogicAdvancedWood) container.pipe.logic));
		playerInventory = playerInventorys;
		filterInventory = (PipeLogicAdvancedWood) container.pipe.logic;
		this.container = container;
		//container = theContainer;
		xSize = 175;
		ySize = 156;

	}

	@Override
	public void initGui() {
		super.initGui();
		int guiX = (width - xSize) / 2;
		int guiY = (height - ySize) / 2;
		buttons[0] =  new GuiButton(1, guiX + 8, guiY + 40, 140, 20, "These items are required");
		buttonList.add(buttons[0]);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		if (((PipeLogicAdvancedWood)container.pipe.logic).exclude) {
			buttons[0].displayString = "These items are excluded";
		}
		else {
			buttons[0].displayString = "These items are required";
		}

		fontRenderer.drawString(StatCollector.translateToLocal(filterInventory.getInvName()), 8, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal(playerInventory.getInvName()), 8, 66, 0x404040);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton.id == 1) {
			PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.ADV_WOOD_DATA, false);
			packet.writeInt(container.xCoord);
			packet.writeInt(container.yCoord);
			packet.writeInt(container.zCoord);
			PacketDispatcher.sendPacketToServer(packet.makePacket());
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(AdditionalPipes.TEXTURE_GUI_ADVANCEDWOOD);
		int j1 = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j1, k, 0, 0, xSize, ySize);
	}

}
