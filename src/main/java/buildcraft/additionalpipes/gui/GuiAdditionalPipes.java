/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.gui;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import buildcraft.BuildCraftCore;
import buildcraft.core.lib.gui.GuiAdvancedInterface;
import buildcraft.core.lib.gui.GuiBuildCraft;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiAdditionalPipes<T extends ContainerAP> extends GuiAdvancedInterface {

	private static final ResourceLocation ITEMS_TEXTURE = TextureMap.locationItemsTexture;

	protected static class ButtonInfo {

		public int x, y;
		public boolean drawButton = true;

		public ButtonInfo(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	protected abstract class Ledger extends GuiBuildCraft.Ledger {
		protected int headerColor = 0xe1c92f;
		protected int subheaderColor = 0xaaafb8;
		protected int textColor = 0x000000;
		protected String headerTitle = "gui.configuration";
		protected Map<Integer, ButtonInfo> buttons = Maps.newHashMap();
		protected IIcon icon = BuildCraftCore.wrenchItem.getIconFromDamage(0);
		
		protected ISound clickSound;

		public Ledger() {
			overlayColor = 0x18a855;
			
			clickSound = PositionedSoundRecord.func_147673_a(new ResourceLocation("random.click"));
		}

		public void addButton(int id, int x, int y) {
			buttons.put(id, new ButtonInfo(x, y));
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			Minecraft.getMinecraft().renderEngine.bindTexture(ITEMS_TEXTURE);
			drawIcon(icon, x + 3, y + 3);

			if (!isFullyOpened()) {
				return;
			}

			drawLedger(x, y);
		}

		private String localize(String[] keys) {
			StringBuilder result = new StringBuilder();
			if (keys[0] != null) {
				result.append(StatCollector.translateToLocal(keys[0]));
			}
			for (int i = 1; i < keys.length; i++) {
				result.append(keys[i]);
			}
			return result.toString();
		}

		public void drawHeader(int x, int y, String... s) {
			fontRendererObj.drawStringWithShadow(localize(s), x, y, headerColor);
		}

		public void drawSubheader(int x, int y, String... s) {
			fontRendererObj.drawStringWithShadow(localize(s), x, y, subheaderColor);
		}

		public void drawText(int x, int y, String... s) {
			fontRendererObj.drawString(localize(s), x, y, textColor);
		}

		protected void drawLedger(int x, int y) {
			drawHeader(x + 22, y + 8, headerTitle);
		}

		protected void drawIcon(int id, IIcon icon, int x, int y) {
			ButtonInfo info = buttons.get(id);
			if (info != null && info.drawButton) {
				Minecraft.getMinecraft().renderEngine.bindTexture(ITEMS_TEXTURE);
				drawIcon(icon, x + info.x, y + info.y);
			}
		}

		@Override
		public boolean handleMouseClicked(int x, int y, int mouseButton) {
			int /*mouseX = x - currentShiftX, */mouseY = y - currentShiftY;
			for (Map.Entry<Integer, ButtonInfo> e : buttons.entrySet()) {
				ButtonInfo info = e.getValue();
				if (info.drawButton && /*mouseX >= info.x && mouseX <= info.x + 16 && */mouseY >= info.y && mouseY <= info.y + 16) {
					mc.getSoundHandler().playSound(clickSound);
					buttonPressed(e.getKey());
					return true;
				}
			}
			return super.handleMouseClicked(x, y, mouseButton);
		}

		protected void buttonPressed(int id) {
		}

		@Override
		public String getTooltip() {
			return StatCollector.translateToLocal(headerTitle);
		}

	}

	public T clientProps;

	public GuiAdditionalPipes(T container, IInventory inventory, ResourceLocation texture) {
		super(container, inventory, texture);
		clientProps = container;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		updateInformation();
		super.drawScreen(mouseX, mouseY, par3);
	}

	protected void updateInformation() {
	}

}
