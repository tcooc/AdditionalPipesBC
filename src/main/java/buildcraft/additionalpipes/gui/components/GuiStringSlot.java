/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package additionalpipes.client.gui.components;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStringSlot extends GuiSlotEx {

	public FontRenderer fontRenderer;

	protected List<String> strings;
	public int lastSelected = -1;
	protected GuiButton buttonReturn;

	public GuiStringSlot(Minecraft mc, IGuiIndirectSlots screen, int x, int y, int width, int height) {
		super(mc, screen, x, y, width, height);
		fontRenderer = mc.fontRenderer;
		slotHeight = 11;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
		//lastSelected = -1;
		bindAmountScrolled();
	}

	@Override
	protected int getSize() {
		return strings == null ? 0 : strings.size();
	}

	@Override
	protected void elementClicked(int index, int mouseX, boolean doubleClick) {
		lastSelected = index;
		if (doubleClick && buttonReturn != null && buttonReturn.enabled && buttonReturn.drawButton) {
			parentScreen.buttonPressed(buttonReturn);
		}
	}

	@Override
	protected boolean isSelected(int index) {
		return lastSelected == index;
	}

	@Override
	protected void drawBackground() {
	}

	@Override
	protected void drawOverlay() {
		super.drawOverlay();
		Gui.drawRect(xPos, yPos - 1, xPos + width, yPos, 0xffa0a0a0);
		Gui.drawRect(xPos, yPos + height, xPos + width, yPos + height + 1, 0xffa0a0a0);
		Gui.drawRect(xPos - 1, yPos - 1, xPos, yPos + height + 1, 0xffa0a0a0);
		Gui.drawRect(xPos + width, yPos - 1, xPos + width + 1, yPos + height + 1, 0xffa0a0a0);
	}

	@Override
	protected void drawSlot(int index, int left, int top, int height, Tessellator tessellator) {
		fontRenderer.drawString(strings.get(index), left, top + (slotHeight - 12) / 2, 0xa0a0a0);
	}

	@Override
	public int getHiddenHeight() {
		// Do not show element at center
		int result = super.getHiddenHeight();
		if (result < 0) {
			result = 0;
		}
		return result;
	}

	public void registerReturnButton(GuiButton button) {
		buttonReturn = button;
	}

}
