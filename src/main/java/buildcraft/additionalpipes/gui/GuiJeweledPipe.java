package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.gui.components.GuiButtonOnOff;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageJeweledPipeOptionsServer;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.SideFilterData;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.additionalpipes.utils.Log;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiJeweledPipe extends GuiContainer
{
	//1-indexed, the first number is the start of the tab row
	final public static int NUM_TABS = 6;
	
	//note: the first two ff's are the alpha value.
	final static int[] tabColorsMain = {0xFFd0d0d0, 0xff3fc2ff, 0xff3e3ee0, 0xff44be4e, 0xfff0db00, 0xffd20000};
	
	final static int[] tabColorsOutline = {0xFFFFFFFF, 0xff20a2d0, 0xff1111c0, 0xff259f2f, 0xffe0cb00, 0xffc20000};

	
	final int tabY = 14;
	final int tabHeight = 11;
	final int totalSpaceBetweenTabs = 7; //total space between tab strings
    final int halfSpaceBetweenTabs = MathHelper.floor_double(totalSpaceBetweenTabs / 2.0);
    final int tabOutlineWidth = 2;
	
	int[] tabEndX = new int[NUM_TABS + 1];
	
	final static int BUTTON_ID_NBT = 1;
	final static int BUTTON_ID_UNSORTED = 2;
	final static int BUTTON_ID_METADATA = 3;
	
	GuiButtonOnOff buttonAcceptUnsorted;
	GuiButtonOnOff buttonMatchNBT;
	GuiButtonOnOff buttonMatchMetadata;
	
    int xStart;
    int yStart;
    
    //this is here so that we don't have to cast it
    ContainerJeweledPipe container;
    
    public GuiJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
        super(new ContainerJeweledPipe(inventoryPlayer, pipe));
        xSize = 202;
        ySize = 211;
        
        container = (ContainerJeweledPipe) inventorySlots;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String containerName = StatCollector.translateToLocal("gui.jeweled_pipe");
        fontRendererObj.drawString(containerName, xSize / 2 - fontRendererObj.getStringWidth(containerName) / 2, 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 93, 4210752);
        
        String match = StatCollector.translateToLocal("gui.match");
        fontRendererObj.drawString(match, 105, 94, 4210752);
		
        //draw the actual tabs
        for(int tabNumber = 1; tabNumber <= NUM_TABS; ++tabNumber)
        {
        	//make the selected tab taller
        	int startY = container.currentSide == tabNumber ? tabY - 3 : tabY;
        	
        	//draw outer rectangle
        	drawRect(tabEndX[tabNumber- 1], startY, tabEndX[tabNumber] - 1, tabY + tabHeight, tabColorsOutline[tabNumber - 1]);
        	//draw inner rectangle
        	drawRect(tabEndX[tabNumber- 1] + tabOutlineWidth, startY + tabOutlineWidth, tabEndX[tabNumber] - tabOutlineWidth - 1, tabY + tabHeight, tabColorsMain[tabNumber - 1]);
        }
        
		//add the tab labels
		for(int tabNumber = 1; tabNumber <= NUM_TABS; ++tabNumber)
		{
			String tabName = StatCollector.translateToLocal("gui.tab." + ForgeDirection.VALID_DIRECTIONS[tabNumber - 1].toString().toLowerCase());
						
			fontRendererObj.drawString(tabName, tabEndX[tabNumber- 1] + halfSpaceBetweenTabs, tabY + 3, 4210752);
		}
	
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Textures.GUI_JEWELED);

        this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

        this.mc.getTextureManager().bindTexture(Textures.GUI_OUTLINE_JEWELED[container.currentSide - 1]);
        this.drawTexturedModalRect(xStart + 5, yStart + 25, 0, 0, 192, 86);
        
    }
    
	@SuppressWarnings("unchecked")
	public void initGui() 
	{
		super.initGui();
		
		xStart = (width - xSize) / 2;
		yStart = (height - ySize) / 2;
		
		tabEndX[0] = 15;
		
		//calculate the tab widths
		for(int tabNumber = 1; tabNumber <= NUM_TABS; ++tabNumber)
		{
			String tabName = StatCollector.translateToLocal("gui.tab." + ForgeDirection.VALID_DIRECTIONS[tabNumber - 1].toString().toLowerCase());
						
			//record the width of this tab
			tabEndX[tabNumber] = tabEndX[tabNumber- 1] + fontRendererObj.getStringWidth(tabName) - 1 + totalSpaceBetweenTabs;
		}
		
		//add the buttons
		buttonAcceptUnsorted = new GuiButtonOnOff(BUTTON_ID_UNSORTED, xStart + 8, yStart + 88, 95,
				container.pipeItemsJeweled.filterData[container.currentSide - 1].acceptsUnsortedItems(),
				StatCollector.translateToLocal("gui.acceptUnsorted"));
		buttonMatchNBT = new GuiButtonOnOff(BUTTON_ID_NBT, xStart + 135, yStart + 88, 30, 
				container.pipeItemsJeweled.filterData[container.currentSide - 1].matchNBT(), StatCollector.translateToLocal("gui.NBT"));
		buttonMatchMetadata = new GuiButtonOnOff(BUTTON_ID_METADATA, xStart + 165, yStart + 88, 30,
				container.pipeItemsJeweled.filterData[container.currentSide - 1].matchMetadata(), StatCollector.translateToLocal("gui.metadata"));
		
		buttonList.add(buttonAcceptUnsorted);
		buttonList.add(buttonMatchNBT);
		buttonList.add(buttonMatchMetadata);
	}
	
    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int x, int y, int type)
    {
        if (type == 0)
        {
            int xDistance = x - (this.guiLeft + tabEndX[0]);
            int yDistance = y - (this.guiTop + tabY);
            
            //check if click was on a tab box
            if(xDistance >= 0 && xDistance <= tabEndX[tabEndX.length - 1])
            {
            	if(yDistance >= 0 && yDistance <= tabHeight)
            	{
            		//find the tab the user clicked on
            		for(byte tabNumber = 1; tabNumber <= NUM_TABS; ++tabNumber)
            		{
            			if(tabEndX[tabNumber] > x - guiLeft)
            			{
            				container.setFilterTab(tabNumber);
            				updateButtonsForTab();
            				break;
            			}
            		}
            	}
            }
        }

        super.mouseMovedOrUp(x, y, type);
    }
    
    /**
     * Change the buttons' pressed states to match the options of the side currently set in the container.
     * 
     * tabNumber is 1-indexed.
     */
    public void updateButtonsForTab()
	{
    	SideFilterData newFilter = container.pipeItemsJeweled.filterData[container.currentSide - 1];
    	buttonAcceptUnsorted.setPressed(newFilter.acceptsUnsortedItems());
    	buttonMatchNBT.setPressed(newFilter.matchNBT());
    	buttonMatchMetadata.setPressed(newFilter.matchMetadata());
	}

	@Override
	protected void actionPerformed(GuiButton button) 
    {
    	SideFilterData currentSideFilter = container.pipeItemsJeweled.filterData[container.currentSide - 1];
		if(button.id == BUTTON_ID_NBT)
		{
			currentSideFilter.setMatchNBT(!currentSideFilter.matchNBT());
		}
		else if(button.id == BUTTON_ID_METADATA)
		{
			currentSideFilter.setMatchMetadata(!currentSideFilter.matchMetadata());
		}
		else if(button.id == BUTTON_ID_UNSORTED)
		{
			currentSideFilter.setAcceptUnsortedItems(!currentSideFilter.acceptsUnsortedItems());
		}
		else
		{
			Log.error("...What?  Got an unknown button index in GuiJeweledPipe.actionPerfomed()");
			return;
		}
		((GuiButtonOnOff)button).togglePressed();

		//send the current filter data to the server
		MessageJeweledPipeOptionsServer message = new MessageJeweledPipeOptionsServer(container.pipeItemsJeweled.container.xCoord,
				container.pipeItemsJeweled.container.yCoord, container.pipeItemsJeweled.container.zCoord, (byte) container.currentSide, currentSideFilter);
		PacketHandler.INSTANCE.sendToServer(message);	
	}

}
