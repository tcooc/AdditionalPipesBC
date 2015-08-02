/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import buildcraft.BuildCraftCore;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.access.PropertyIntArray;
import buildcraft.additionalpipes.access.PropertyInteger;
import buildcraft.additionalpipes.gui.components.GuiRestrictedTile;
import buildcraft.additionalpipes.tileentity.TileTeleportManager;
import buildcraft.api.transport.IPipeTile.PipeType;
import buildcraft.core.CoreIconProvider;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleportManager extends GuiRestrictedTile<ContainerTeleportManager> {

	private static final ResourceLocation CREATIVE_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private static final ResourceLocation MAP_TEXTURE = new ResourceLocation("textures/map/map_background.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation("additionalpipes", APDefaultProps.TEXTURE_PATH_GUI + "/teleportmanager_map.png");

	class EnergyLedger extends Ledger {

		public EnergyLedger() {
			maxHeight = 72;
			maxWidth = 96;
			overlayColor = 0xd46c1f;
			headerColor = 0xe1c92f;
			subheaderColor = 0xaaafb8;
			headerTitle = "gui.energy";
			icon = BuildCraftCore.iconProvider.getIcon(CoreIconProvider.ENERGY);
		}

		@Override
		protected void drawLedger(int x, int y) {
			super.drawLedger(x, y);
			drawSubheader(x + 8, y + 20, "gui.capacity", ":");
			drawText(x + 8, y + 32, null, String.format("%d LP", AdditionalPipes.managerCapacity));
			drawSubheader(x + 8, y + 44, "gui.stored", ":");
			drawText(x + 8, y + 56, null, String.format("%d LP", clientProps.propLP.value));
		}

		@Override
		public String getTooltip() {
			return String.format("%d LP", clientProps.propLP.value);
		}

	}

	class ConfigLedger extends Ledger {

		public ConfigLedger() {
			super();
			maxWidth = 96;
			maxHeight = 52;
			overlayColor = 0x18a855;
			addButton(1, 16, 30);
		}

		@Override
		protected void drawLedger(int x, int y) {
			super.drawLedger(x, y);

			drawSubheader(x + 8, y + 20, "gui.isPublic", ":");
			boolean isPublic = clientProps.propIsPublic.value;
			drawIcon(1, GuiIconProvider.INSTANCE.getIcon(isPublic ? GuiIconProvider.ON : GuiIconProvider.OFF), x, y);
		}

		@Override
		protected void buttonPressed(int id) {
			if (id == 1) {
				clientProps.pushProperty(clientProps.propIsPublic.index, !clientProps.propIsPublic.value);
			}
			else {
				super.buttonPressed(id);
			}
		}

	}

	private static final RenderItem itemRenderer = new RenderItem();
	private MapItemRenderer mapRenderer;

	private int theMap = -1;
	private int currentMap = -1;

	private int scrollLine = 0;
	private boolean isScrolling = false;
	private boolean wasClicking = false;

	private static final int MAPS_ROW = 4;
	private static final int MAPS_COL = 3;

	public GuiTeleportManager(IInventory playerInventory, TileTeleportManager manager) {
		super(new ContainerTeleportManager(playerInventory, manager), null);
		xSize = 176;
		ySize = 222;
	}

	@Override
	public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3) {
		super.setWorldAndResolution(par1Minecraft, par2, par3);
		mapRenderer = new MapItemRenderer(mc.gameSettings, mc.renderEngine);
	}

	@Override
	protected void initLedgers(IInventory inventory) {
		super.initLedgers(inventory);
		if (!AdditionalPipes.disableEnergyUsage) {
			ledgerManager.add(new EnergyLedger());
		}
		ledgerManager.add(new ConfigLedger());
	}

	private boolean needsScrollBars() {
		return clientProps.propMaps.value.length > MAPS_ROW * (MAPS_COL - 1);
	}

	private void renderPipeMap(int map, MapData mapData) {
		RenderHelper.disableStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		mc.renderEngine.func_110577_a(MAP_TEXTURE);

		GL11.glPushMatrix();
		Tessellator var4 = Tessellator.instance;
		var4.startDrawingQuads();
		byte var5 = 7;
		var4.addVertexWithUV(0 - var5, 128 + var5, 0.0D, 0.0D, 1.0D);
		var4.addVertexWithUV(128 + var5, 128 + var5, 0.0D, 1.0D, 1.0D);
		var4.addVertexWithUV(128 + var5, 0 - var5, 0.0D, 1.0D, 0.0D);
		var4.addVertexWithUV(0 - var5, 0 - var5, 0.0D, 0.0D, 0.0D);
		var4.draw();

		mapRenderer.renderMap(mc.thePlayer, mc.renderEngine, mapData);

		PropertyIntArray pipeArr = (PropertyIntArray) clientProps.propPipes.value.get(PropertyInteger.create(map));
		int[] pipes = pipeArr != null ? pipeArr.value : ArrayUtils.EMPTY_INT_ARRAY;
		for (int p = 0; p < pipes.length / 3; p++) {
			int centerX = pipes[p * 3 + 1];
			int centerZ = pipes[p * 3 + 2];
			GL11.glPushMatrix();
			GL11.glTranslatef(centerX / 2.0F + 64.0F - 4, centerZ / 2.0F + 64.0F - 4, -0.02F);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, new ItemStack(getTeleportPipe(PipeType.values()[pipes[p * 3]]), 1), 0, 0);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

	private Item getTeleportPipe(PipeType type) {
		switch (type) {
			case ITEM:
				return AdditionalPipes.pipeItemsTeleport;
			case FLUID:
				return AdditionalPipes.pipeFluidsTeleport;
			case POWER:
				return AdditionalPipes.pipePowerTeleport;
			case STRUCTURE:
				return AdditionalPipes.pipeStructureTeleport;
		}
		return null;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.func_110577_a(TEXTURE);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if (clientProps.propMaps.value.length == 0) {
			drawTexturedModalRect(guiLeft + 8 + (36 * MAPS_ROW - 16) / 2, guiTop + 18 + (36 * MAPS_COL - 16) / 2, xSize, 0, 16, 16);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		float scrollPercent = scrollLine / (float) getScrollMax();
		int scrollBarLeft = guiLeft + xSize - 20;
		int scrollBarTop = guiTop + 18;
		int scrollBarBottom = scrollBarTop + 113;
		boolean isClicking = Mouse.isButtonDown(0);
		if (!wasClicking && isClicking && mouseX >= scrollBarLeft && mouseX < scrollBarLeft + 20 && mouseY >= scrollBarTop && mouseY < scrollBarBottom) {
			isScrolling = needsScrollBars();
		}
		if (!isClicking) {
			isScrolling = false;
		}
		wasClicking = isClicking;
		if (isScrolling) {
			scrollPercent = (mouseY - scrollBarTop - 7.5F) / (scrollBarBottom - scrollBarTop - 20.0F);
			if (scrollPercent < 0.0F) {
				scrollPercent = 0.0F;
			} else if (scrollPercent > 1.0F) {
				scrollPercent = 1.0F;
			}
			scrollLine = (int) (scrollPercent * getScrollMax() + 0.5D);
			validateScrollLine();
		}

		fontRenderer.drawString(StringUtils.localize("container.teleportManager"), 8, 6, clientProps.propIsPublic.value ? 0x0000FF : 0x404040);
		fontRenderer.drawString(StringUtils.localize("gui.inventory"), 8, 130, 0x404040);

		RenderHelper.disableStandardItemLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.func_110577_a(CREATIVE_TEXTURE);
		drawTexturedModalRect(xSize - 20, 18 + (int) (93 * scrollPercent), 232 + (needsScrollBars() ? 0 : 12), 0, 12, 15);

		theMap = -1;
		int[] maps = clientProps.propMaps.value;
		if (maps.length == 0) {
			if (mouseX >= guiLeft + 8 && mouseX < guiLeft + 8 + 36 * MAPS_ROW && mouseY >= guiTop + 18 && mouseY < guiTop + 18 + 36 * MAPS_COL) {
				String text = StringUtils.localize(AdditionalPipes.disableLinkingUsage ? "gui.map.emptyHint.position" : "gui.map.emptyHint.link");
				int width = fontRenderer.getStringWidth(text);
				GL11.glPushMatrix();
				GL11.glTranslatef(-guiLeft, -guiTop, 0);
				drawCreativeTabHoveringText(text, mouseX - 12 - width / 2, mouseY - 4);
				GL11.glPopMatrix();
			}
		} else if (currentMap == -1) {
			ItemStack mapStack = new ItemStack(Item.map, 1, 0);
			for (int i = 0; i < MAPS_COL; i++) {
				for (int j = 0; j < MAPS_ROW; j++) {
					int index = j + (scrollLine + i) * MAPS_ROW;
					if (index < maps.length) {
						mapStack.setItemDamage(maps[index]);
						MapData mapData = Item.map.getMapData(mapStack, mc.theWorld);
						if (mapData != null) {
							int x = 10 + j * 36;
							int y = 20 + i * 36;
							GL11.glPushMatrix();
							GL11.glTranslatef(x, y, 0.0F);
							GL11.glScalef(0.25F, 0.25F, 0.25F);
							renderPipeMap(maps[index], mapData);
							GL11.glPopMatrix();
						}
					}
				}
			}
			int index = getMapIndexAtPos(mouseX, mouseY);
			if (index >= 0 && index < maps.length) {
				theMap = index;
				mapStack.setItemDamage(maps[index]);
				drawItemStackTooltip(mapStack, mouseX - guiLeft + 8, mouseY - guiTop + 8);
			}
		} else {
			MapData mapData = Item.map.getMapData(new ItemStack(Item.map, 1, maps[currentMap]), mc.theWorld);
			if (mapData != null) {
				GL11.glPushMatrix();
				GL11.glTranslatef(7, guiTop, 0.0F);
				GL11.glScalef(1.25F, 1.25F, 1.25F);
				renderPipeMap(maps[currentMap], mapData);
				GL11.glPopMatrix();
			}
		}
		RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3) {
		if (!(currentMap != -1 && par2 >= -2 && par2 < 178 && par3 >= 5 && par3 < 185)) {
			super.drawItemStackTooltip(par1ItemStack, par2, par3);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int wheel = Mouse.getEventDWheel();

		if (wheel != 0 && needsScrollBars()) {
			if (wheel > 0) {
				scrollLine--;
			} else if (wheel < 0) {
				scrollLine++;
			}

			validateScrollLine();
		}
	}

	private int getScrollMax() {
		return needsScrollBars() ? (clientProps.propMaps.value.length - 1) / MAPS_ROW - 1 : 0;
	}

	private void validateScrollLine() {
		int scrollMax = getScrollMax();
		if (scrollLine < 0) {
			scrollLine = 0;
		} else if (scrollLine > scrollMax) {
			scrollLine = scrollMax;
		}
	}

	private int getMapIndexAtPos(int mouseX, int mouseY) {
		mouseX -= guiLeft;
		mouseY -= guiTop;
		// 144x108
		if (mouseX >= 8 && mouseX < 154 && mouseY >= 18 && mouseY < 126) {
			int x = (mouseX - 8) / 36;
			int y = (mouseY - 18) / 36;
			if (x < MAPS_ROW && y < MAPS_COL) {
				return x + (scrollLine + y) * MAPS_ROW;
			}
		}
		return -1;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		int index = getMapIndexAtPos(mouseX, mouseY);
		if (currentMap != -1 && mouseX >= guiLeft - 2 && mouseX < guiLeft + 178 && mouseY >= guiTop && mouseY < guiTop + 180) {
			currentMap = -1;
			return;
		}
		if (mouseButton == 1 && currentMap == -1 && index >= 0 && index < clientProps.propMaps.value.length) {
			currentMap = index;
			return;
		}
		if (index >= 0) {
			if (mouseButton == mc.gameSettings.keyBindPickBlock.keyCode + 100) {
				handleMouseClick(index, mouseButton, 3);
			} else {
				boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
				handleMouseClick(index, mouseButton, shift ? 1 : 0);
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char c, int key) {
		super.keyTyped(c, key);

		if (theMap != -1) {
			if (key == mc.gameSettings.keyBindPickBlock.keyCode) {
				handleMouseClick(theMap, 0, 3);
			} else if (key == mc.gameSettings.keyBindDrop.keyCode) {
				handleMouseClick(theMap, 0, 4);
			}
		}
	}

	private void handleMouseClick(int index, int mouseButton, int data) {
		clientProps.pushProperty(clientProps.propViewClick.index, new int[] { index, mouseButton << 16 | data });
	}

	@Override
	public String getPropOwner() {
		String owner = super.getPropOwner();
		if (mc.isSingleplayer() && !mc.getIntegratedServer().getPublic() && !Strings.isNullOrEmpty(AdditionalPipes.fakedUserName)) {
			owner = AdditionalPipes.fakedUserName;
		}
		return owner;
	}

}
