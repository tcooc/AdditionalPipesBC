package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.utils.TranslationKeys;
import net.minecraft.util.EnumFacing;
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
			this.title = TranslationKeys.TELEPORT_LEDGER_TITLE;
			
			appendText(() -> ((pipe.getState() & 0x1) >= 1) ? I18n.format(TranslationKeys.TELEPORT_LEDGER_OUTPUT, container.connectedPipes) : I18n.format(TranslationKeys.TELEPORT_LEDGER_INPUT, container.connectedPipes), headerColour);
			
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

	protected enum BtnIndex{
		FreqNeg100, FreqNeg10, FreqNeg1, FreqPos1, FreqPos10, FreqPos100, Mode, IsPublic, TP_Side
	}

	private final PipeBehaviorTeleport pipe;
	private final ContainerTeleportPipe container;
	private final GuiButton[] buttons = new GuiButton[BtnIndex.values().length];
	
	public GuiTeleportPipe(EntityPlayer player, PipeBehaviorTeleport pipe) {
		super(new ContainerTeleportPipe(player, pipe));
		this.pipe = pipe;
		container = (ContainerTeleportPipe) inventorySlots;
		xSize = 228;
		ySize = 127;
		
		mainGui.shownElements.add(new TeleportPipeLedger());
	}

	//Removes the help button top left of the gui, as it does nothing but take up space
	@Override
	protected boolean shouldAddHelpLedger() {
		return false;
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2;
		int bw = xSize - 24;
		int btnHeight = 20;
		int ID = 0;
		
		final int freqButtonTop = 78;
		
		buttonList.add(buttons[BtnIndex.FreqNeg100.ordinal()] = new GuiButton(++ID, x + 12, guiTop + freqButtonTop, bw / 6, btnHeight, "-100"));
		buttonList.add(buttons[BtnIndex.FreqNeg10.ordinal()] = new GuiButton(++ID, x + 12 + bw / 6, guiTop + freqButtonTop, bw / 6, btnHeight, "-10"));
		buttonList.add(buttons[BtnIndex.FreqNeg1.ordinal()] = new GuiButton(++ID, x + 12 + bw * 2 / 6, guiTop + freqButtonTop, bw / 6, btnHeight, "-1"));
		buttonList.add(buttons[BtnIndex.FreqPos1.ordinal()] = new GuiButton(++ID, x + 12 + bw * 3 / 6, guiTop + freqButtonTop, bw / 6, btnHeight, "+1"));
		buttonList.add(buttons[BtnIndex.FreqPos10.ordinal()] = new GuiButton(++ID, x + 12 + bw * 4 / 6, guiTop + freqButtonTop, bw / 6, btnHeight, "+10"));
		buttonList.add(buttons[BtnIndex.FreqPos100.ordinal()] = new GuiButton(++ID, x + 12 + bw * 5 / 6, guiTop + freqButtonTop, bw / 6, btnHeight, "+100"));

		buttonList.add(buttons[BtnIndex.Mode.ordinal()] = new GuiButton(++ID, x + 12, guiTop + 35, bw / 2, 20, ""));
		buttonList.add(buttons[BtnIndex.IsPublic.ordinal()] = new GuiButton(++ID, x + 12 + bw * 3 / 6, guiTop + 35, bw / 2, 20, ""));
		buttonList.add(buttons[BtnIndex.TP_Side.ordinal()] = new GuiButton(++ID, x + 12, guiTop + 100, (xSize - 24), btnHeight, ""));
	}

	@Override
	protected void drawForegroundLayer() 
	{
		fontRenderer.drawString(I18n.format(pipe.getUnlocalizedName()), guiLeft + 70, guiTop + 6, 0x0a0c84, false);
		fontRenderer.drawString(I18n.format(TranslationKeys.TELEPORT_FREQ, pipe.getFrequency()), guiLeft + 16, guiTop + 66, 0x404040);
		fontRenderer.drawString(I18n.format(TranslationKeys.TELEPORT_COORDS, pipe.getPos().getX(), pipe.getPos().getY(), pipe.getPos().getZ()), guiLeft + 110, guiTop + 20, 0x404040);
		
		fontRenderer.drawString(I18n.format(TranslationKeys.TELEPORT_LEDGER_OWNER, pipe.getOwnerName()), guiLeft + 12, guiTop + 20, 0x404040);
		
		switch(pipe.getState()) {
		case 3:
			buttons[BtnIndex.Mode.ordinal()].displayString = I18n.format(TranslationKeys.TELEPORT_SEND_RECEIVE);
			break;
		case 2:
			buttons[BtnIndex.Mode.ordinal()].displayString = I18n.format(TranslationKeys.TELEPORT_RECEIVE_ONLY);
			break;
		case 1:
			buttons[BtnIndex.Mode.ordinal()].displayString = I18n.format(TranslationKeys.TELEPORT_SEND_ONLY);
			break;
		default:
			buttons[BtnIndex.Mode.ordinal()].displayString = I18n.format(TranslationKeys.TELEPORT_DISABLED);
			break;
		}

		if(pipe.isPublic()) {
			buttons[BtnIndex.IsPublic.ordinal()].displayString = I18n.format(TranslationKeys.TELEPORT_PUBLIC);
		} else {
			buttons[BtnIndex.IsPublic.ordinal()].displayString = I18n.format(TranslationKeys.TELEPORT_PRIVATE);
		}

		if (pipe.getTeleportSide() == EnumFacing.DOWN){
			buttons[BtnIndex.TP_Side.ordinal()].displayString = I18n.format(TranslationKeys.SIDE_DOWN);}
		else if (pipe.getTeleportSide() == EnumFacing.UP) {
			buttons[BtnIndex.TP_Side.ordinal()].displayString = I18n.format(TranslationKeys.SIDE_UP);
		}
		else if (pipe.getTeleportSide() == EnumFacing.NORTH) {
			buttons[BtnIndex.TP_Side.ordinal()].displayString = I18n.format(TranslationKeys.SIDE_NORTH);
		}
		else if (pipe.getTeleportSide() == EnumFacing.SOUTH) {
			buttons[BtnIndex.TP_Side.ordinal()].displayString = I18n.format(TranslationKeys.SIDE_SOUTH);
		}
		else if (pipe.getTeleportSide() == EnumFacing.WEST) {
			buttons[BtnIndex.TP_Side.ordinal()].displayString = I18n.format(TranslationKeys.SIDE_WEST);
		}
		else if (pipe.getTeleportSide() == EnumFacing.EAST) {
			buttons[BtnIndex.TP_Side.ordinal()].displayString = I18n.format(TranslationKeys.SIDE_EAST);
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int freq = pipe.getFrequency();
		byte state = pipe.getState();
		boolean isPublic = pipe.isPublic();
		byte tpSide = (byte) pipe.getTeleportSide().ordinal();

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
		case 9:
			tpSide += 1;
		}
		if(freq < 0) {
			freq = 0;
		} else if (freq >= Integer.MAX_VALUE - 101) {
			freq = Integer.MAX_VALUE -101;
		}

		if (tpSide >= EnumFacing.values().length){
			tpSide = 0;}

		MessageTelePipeUpdate packet = new MessageTelePipeUpdate(pipe.getPos(), freq, isPublic, state, EnumFacing.values()[tpSide]);
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
