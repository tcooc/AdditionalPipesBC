package buildcraft.additionalpipes.gui;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageTelePipeUpdate;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.lib.BCLibSprites;
import buildcraft.lib.gui.GuiBC8;
import buildcraft.lib.gui.GuiIcon;
import buildcraft.lib.gui.ledger.Ledger_Neptune;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleportPipe extends GuiBC8<ContainerTeleportPipe> {
		
	protected class TeleportPipeLedger extends Ledger_Neptune
	{

		int headerColour = 0xe1c92f;
		int subheaderColour = 0xaaafb8;
		int textColour = 0x000000;
		
		final static int OVERLAY_COLOR = 0xffd46c1f;
		
		public TeleportPipeLedger() {
			super(GuiTeleportPipe.this.mainGui, OVERLAY_COLOR, true);
			this.title = "gui.teleport.ledger.title";
			
			appendText(() -> ((pipe.getState() & 0x1) >= 1) ? I18n.format("gui.teleport.ledger.outputs", container.connectedPipes) : I18n.format("gui.teleport.ledger.inputs", container.connectedPipes), headerColour);
			
			// print up to the first 3 connected pipes, with 3 coords each
			for(int coordIndex = 0; coordIndex < 3; coordIndex += 3)
			{
				final int capturedIdx = coordIndex;
				appendText( () -> 
				{
					StringBuilder text = new StringBuilder();
					if(pipe.getNetwork().length >= capturedIdx + 2)
					{
						text.append("(").append(pipe.getNetwork()[capturedIdx]).append(", ").append(pipe.getNetwork()[capturedIdx + 1]).append(", ").append(pipe.getNetwork()[capturedIdx + 2]).append(")");
					}
					
					return text.toString();
				},  textColour);
			}
			
			
			calculateMaxSize();

		}

		@Override
		protected void drawIcon(double x, double y)
		{
	        GuiIcon.draw(BCLibSprites.ENGINE_ACTIVE, x, y, x + 16, y + 16);
		}
	
	}

	private final PipeBehaviorTeleport pipe;
	private final ContainerTeleportPipe container;
	private final GuiButton[] buttons = new GuiButton[8];
	
	public GuiTeleportPipe(EntityPlayer player, PipeBehaviorTeleport pipe) {
		super(new ContainerTeleportPipe(player, pipe));
		this.pipe = pipe;
		container = (ContainerTeleportPipe) inventorySlots;
		xSize = 228;
		ySize = 117;
		
		mainGui.shownElements.add(new TeleportPipeLedger());
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2;
		int bw = xSize - 24;
		
		final int freqButtonTop = 78;
		
		buttonList.add(buttons[0] = new GuiButton(1, x + 12, guiTop + freqButtonTop, bw / 6, 20, "-100"));
		buttonList.add(buttons[1] = new GuiButton(2, x + 12 + bw / 6, guiTop + freqButtonTop, bw / 6, 20, "-10"));
		buttonList.add(buttons[2] = new GuiButton(3, x + 12 + bw * 2 / 6, guiTop + freqButtonTop, bw / 6, 20, "-1"));
		buttonList.add(buttons[3] = new GuiButton(4, x + 12 + bw * 3 / 6, guiTop + freqButtonTop, bw / 6, 20, "+1"));
		buttonList.add(buttons[4] = new GuiButton(5, x + 12 + bw * 4 / 6, guiTop + freqButtonTop, bw / 6, 20, "+10"));
		buttonList.add(buttons[5] = new GuiButton(6, x + 12 + bw * 5 / 6, guiTop + freqButtonTop, bw / 6, 20, "+100"));

		buttonList.add(buttons[6] = new GuiButton(7, x + 12, guiTop + 35, bw / 2, 20, ""));
		buttonList.add(buttons[7] = new GuiButton(8, x + 12 + bw * 3 / 6, guiTop + 35, bw / 2, 20, ""));
	}

	@Override
	protected void drawForegroundLayer() 
	{
		fontRenderer.drawString(I18n.format(pipe.getUnlocalizedName()), guiLeft + 70, guiTop + 6, 0x0a0c84, false);
		fontRenderer.drawString(I18n.format("gui.teleport.frequency", pipe.getFrequency()), guiLeft + 16, guiTop + 66, 0x404040);
		fontRenderer.drawString(I18n.format("gui.teleport.coordPair", pipe.getPos().getX(), pipe.getPos().getY(), pipe.getPos().getZ()), guiLeft + 110, guiTop + 20, 0x404040);
		
		fontRenderer.drawString(I18n.format("gui.teleport.ledger.owner", pipe.getOwnerName()), guiLeft + 12, guiTop + 20, 0x404040);
		
		switch(pipe.getState()) {
		case 3:
			buttons[6].displayString = I18n.format("gui.teleport.send_and_receive");
			break;
		case 2:
			buttons[6].displayString = I18n.format("gui.teleport.receive_only");
			break;
		case 1:
			buttons[6].displayString = I18n.format("gui.teleport.send_only");
			break;
		default:
			buttons[6].displayString = I18n.format("gui.teleport.disabled");
			break;
		}
		if(pipe.isPublic()) {
			buttons[7].displayString = I18n.format("gui.teleport.public");
		} else {
			buttons[7].displayString = I18n.format("gui.teleport.private");
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int freq = pipe.getFrequency();
		byte state = pipe.getState();
		boolean isPublic = pipe.isPublic();
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
			state = (byte) ((state + 1) % 4);
			break;
		case 8:
			isPublic = !isPublic;
			break;
		}
		if(freq < 0) {
			freq = 0;
		}

		MessageTelePipeUpdate packet = new MessageTelePipeUpdate(pipe.getPos(), freq, isPublic, state);
		PacketHandler.INSTANCE.sendToServer(packet);
	}

	@Override
	protected void drawBackgroundLayer(float partialTicks)
	{
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(Textures.GUI_TELEPORT);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
