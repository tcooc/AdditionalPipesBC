/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.gui;

import buildcraft.lib.gui.GuiBC8;
import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageAdvWoodPipe;
import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.additionalpipes.textures.Textures;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedWoodPipe extends GuiBC8<ContainerAdvancedWoodPipe> {

	int inventoryRows = 1;
	IInventory playerInventory;
	PipeBehaviorAdvWood pipe;
	private GuiButton[] buttons = new GuiButton[1];

	int guiX, guiY; 
	
	public GuiAdvancedWoodPipe(EntityPlayer player, PipeBehaviorAdvWood pipe)
	{
		super(new ContainerAdvancedWoodPipe(player, pipe));
		this.playerInventory = player.inventory;
		this.pipe = pipe;
		// container = theContainer;
		xSize = 176;
		ySize = 155;

	}

	@Override
	protected boolean shouldAddHelpLedger() {
		return false;
	}

	@Override
	public void initGui() 
	{
		super.initGui();
		guiX = (width - xSize) / 2;
		guiY = (height - ySize) / 2;
		buttons[0] = new GuiButton(1, guiLeft + 7, guiTop + 38, 140, 20, "");
		buttonList.add(buttons[0]);
	}
	
    public void drawScreen(int mouseX, int mouseY, float partialTicks) 
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

	@Override
	protected void drawForegroundLayer()
	{
		fontRenderer.drawString(I18n.format("gui.advwood_pipe.title"), guiLeft + 8, guiTop + 6, 4210752);

		if(pipe.getExclude()) 
		{
			buttons[0].displayString = I18n.format("gui.advwood_pipe.blacklist");
		}
		else
		{
			buttons[0].displayString = I18n.format("gui.advwood_pipe.whitelist");
		}

		double invY = mainGui.rootElement.getY() + ySize - 94;
		fontRenderer.drawString(I18n.format("gui.inventory"), (int) mainGui.rootElement.getX() + 8, (int) invY, 0x404040);

	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(guibutton.id == 1) 
		{
			pipe.setExclude(!pipe.getExclude());
			MessageAdvWoodPipe packet = new MessageAdvWoodPipe(pipe.pipe.getHolder().getPipePos(), pipe.getExclude());
			PacketHandler.INSTANCE.sendToServer(packet);
		}
	}

	@Override
	protected void drawBackgroundLayer(float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Textures.GUI_ADVANCEDWOOD);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
