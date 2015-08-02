/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package additionalpipes.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonShorter extends GuiButton {

	public GuiButtonShorter(int i, int j, int k, int l, int i1, String s) {
		super(i, j, k, l, i1, s);
	}

	@Override
	public void drawButton(Minecraft minecraft, int i, int j) {
		if (drawButton) {
			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.renderEngine.func_110577_a(field_110332_a);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_82253_i = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
			int k = getHoverState(field_82253_i);
			int width2 = width - width / 2;
			int height2 = height - height / 2;
			drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height / 2);// top left
			drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width2, 46 + k * 20, width2, height / 2);// top right
			drawTexturedModalRect(xPosition, yPosition + height / 2, 0, 46 + k * 20 + 20 - height2, width / 2, height2);// bottom left
			drawTexturedModalRect(xPosition + width / 2, yPosition + height / 2, 200 - width2, 46 + k * 20 + 20 - height2, width2, height2);// bottom right
			mouseDragged(minecraft, i, j);

			int color = 0xe0e0e0;
			if (!enabled) {
				color = 0xffa0a0a0;
			} else if (field_82253_i) {
				color = 0xffffa0;
			}
			drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, color);
		}
	}
}
