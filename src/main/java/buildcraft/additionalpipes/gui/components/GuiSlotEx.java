/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package additionalpipes.client.gui.components;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Based on net.minecraft.client.gui.GuiSlot
@SideOnly(Side.CLIENT)
public abstract class GuiSlotEx {

	protected final Minecraft mc;
	protected final IGuiIndirectSlots parentScreen;

	/** The left of the slot container. Affects the overlays and scrolling. */
	protected int xPos;
	protected int yPos;
	protected int width;
	protected int height;

	/** The height of a slot. */
	public int slotHeight;
	public int scrollBarWidth = 6;
	public byte gradientHeight = 4;
	public boolean drawGradient;

	/** button id of the button used to scroll up/down */
	private int scrollUpButtonID;
	private int scrollDownButtonID;

	/** X/Y axis position of the mouse */
	protected int mouseX;
	protected int mouseY;

	/** where the mouse was in the window when you first clicked to scroll */
	private float initialClickY = -2.0F;

	/**
	 * what to multiply the amount you moved your mouse by(used for slowing down scrolling when over the items and no on
	 * scroll bar)
	 */
	private float scrollMultiplier;

	/** how far down this slot has been scrolled */
	private float amountScrolled;

	/** the element in the list that was selected */
	private int selectedElement = -1;

	/** the time when this button was last clicked. */
	private long lastClicked = 0L;

	/** true if a selected element in this gui will show an outline box */
	private boolean showSelectionBox = true;
	private boolean drawMarginTop;
	private int marginTop;
	public int marginLeft;

	public GuiSlotEx(Minecraft mc, IGuiIndirectSlots screen, int left, int top, int width, int height) {
		this.mc = mc;
		parentScreen = screen;
		setBounds(left, top, width, height);
	}

	public void setBounds(int left, int top, int width, int height) {
		xPos = left;
		yPos = top;
		this.width = width;
		this.height = height;
	}

	public void setShowSelectionBox(boolean par1) {
		showSelectionBox = par1;
	}

	protected void setMarginTop(boolean par1, int par2) {
		drawMarginTop = par1;
		marginTop = par1 ? par2 : 0;
	}

	/**
	 * Gets the size of the current slot list.
	 */
	protected abstract int getSize();

	/**
	 * the element in the slot that was clicked, boolean for wether it was double clicked or not
	 */
	protected abstract void elementClicked(int index, int mouseX, boolean doubleClick);

	/**
	 * returns true if the element passed in is currently selected
	 */
	protected abstract boolean isSelected(int index);

	/**
	 * return the height of the content being scrolled
	 */
	protected int getContentHeight() {
		return getSize() * slotHeight + marginTop;
	}

	protected abstract void drawBackground();

	protected abstract void drawSlot(int index, int left, int top, int height, Tessellator tessellator);

	protected void drawMarginTop(int par1, int par2, Tessellator tessellator) {
	}

	protected void marginClicked(int par1, int par2) {
	}

	protected void drawFinish(int mouseX, int mouseY) {
	}

	public int getHoveredItem(int x, int y) {
		int slotLeft = xPos;
		int slotRight = xPos + width;
		int mouseSlotY = y - yPos - marginTop + (int) amountScrolled - 4;
		int hovered = mouseSlotY / slotHeight;
		return x >= slotLeft && x <= slotRight && hovered >= 0 && mouseSlotY >= 0 && hovered < getSize() ? hovered : -1;
	}

	/**
	 * Registers the IDs that can be used for the scrollbar's buttons.
	 */
	public void registerScrollButtons(List<GuiButton> par1List, int par2, int par3) {
		scrollUpButtonID = par2;
		scrollDownButtonID = par3;
	}

	/**
	 * stop the thing from scrolling out of bounds
	 */
	protected void bindAmountScrolled() {
		int var1 = getHiddenHeight();

		if (var1 < 0) {
			var1 /= 2;
		}

		if (amountScrolled < 0.0F) {
			amountScrolled = 0.0F;
		}

		if (amountScrolled > var1) {
			amountScrolled = var1;
		}
	}

	public int getHiddenHeight() {
		return getContentHeight() - (height - getGradientHeight());
	}

	public void scroll(int par1) {
		amountScrolled += par1;
		bindAmountScrolled();
		initialClickY = -2.0F;
	}

	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == scrollUpButtonID) {
				amountScrolled -= slotHeight * 2 / 3;
				initialClickY = -2.0F;
				bindAmountScrolled();
			} else if (button.id == scrollDownButtonID) {
				amountScrolled += slotHeight * 2 / 3;
				initialClickY = -2.0F;
				bindAmountScrolled();
			}
		}
	}

	/**
	 * draws the slot to the screen, pass in mouse's current x and y and partial ticks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		drawBackground();
		int size = getSize();
		int slotLeft = xPos + marginLeft + 2;
		int slotRight = xPos + width - 2;
		if (getHiddenHeight() > 0) {
			slotRight = getScrollBarX() - 2;
		}

		if (Mouse.isButtonDown(0)) {
			if (initialClickY == -1.0F) {
				boolean slotClicked = true;

				if (mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height) {
					int mouseSlotY = mouseY - yPos - marginTop + (int) amountScrolled - getGradientHeight();
					int hovered = mouseSlotY / slotHeight;
					if (/*mouseX < slotLeft || mouseX > slotRight || */hovered < -1 || hovered >= size) {
						hovered = -1;
					}

					boolean doubleClick = hovered == selectedElement && Minecraft.getSystemTime() - lastClicked < 250L;
					lastClicked = Minecraft.getSystemTime();
					elementClicked(hovered, mouseX - xPos, doubleClick);

					if (hovered >= 0) {
						selectedElement = hovered;
					} else if (mouseX >= slotLeft && mouseX <= slotRight && mouseSlotY < 0) {
						marginClicked(mouseX - slotLeft, mouseY - yPos + (int) amountScrolled - getGradientHeight());
						slotClicked = false;
					}

					int scrollBarX = getScrollBarX();
					if (mouseX >= scrollBarX && mouseX < scrollBarX + scrollBarWidth) {
						scrollMultiplier = -1.0F;
						int hiddenHeight = getHiddenHeight();

						if (hiddenHeight < 1) {
							hiddenHeight = 1;
						}

						int slotBottom = height * height / getContentHeight();

						if (slotBottom < 32) {
							slotBottom = 32;
						}

						if (slotBottom > height - getGradientHeight() * 2) {
							slotBottom = height - getGradientHeight() * 2;
						}

						scrollMultiplier /= (float) (height - slotBottom) / (float) hiddenHeight;
					} else {
						scrollMultiplier = 1.0F;
					}

					if (slotClicked) {
						initialClickY = mouseY;
					} else {
						initialClickY = -2.0F;
					}
				} else {
					initialClickY = -2.0F;
				}
			} else if (initialClickY >= 0.0F) {
				float scroll = amountScrolled;
				amountScrolled -= (mouseY - initialClickY) * scrollMultiplier;
				bindAmountScrolled();
				if (scroll != amountScrolled) {
					initialClickY = mouseY;
				}
			}
		} else if (mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height) {
			while (!mc.gameSettings.touchscreen && Mouse.next()) {
				int wheel = Mouse.getEventDWheel();

				if (wheel != 0) {
					if (wheel > 0) {
						wheel = -1;
					} else if (wheel < 0) {
						wheel = 1;
					}

					amountScrolled += wheel * slotHeight / 2;
					bindAmountScrolled();
				}
			}

			initialClickY = -1.0F;
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tessellator tessellator = Tessellator.instance;
		drawContainerBackground(tessellator);
		int scrolledTop = yPos + getGradientHeight() - (int) amountScrolled;
		if (!drawGradient) {
			scrolledTop += 2;
		}

		if (drawMarginTop) {
			drawMarginTop(slotLeft, scrolledTop, tessellator);
		}

		for (int i = 0; i < size; ++i) {
			int slotTop = scrolledTop + i * slotHeight + marginTop;
			int slotHeight = this.slotHeight - 4;

			if (slotTop <= yPos + height && slotTop + slotHeight >= yPos) {
				if (showSelectionBox && isSelected(i)) {
					int outlineLeft = slotLeft - 2;
					int outlineRight = slotRight + 2;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator.startDrawingQuads();
					tessellator.setColorOpaque_I(0x808080);
					tessellator.addVertexWithUV(outlineLeft, slotTop + slotHeight + 2, 0.0D, 0.0D, 1.0D);
					tessellator.addVertexWithUV(outlineRight, slotTop + slotHeight + 2, 0.0D, 1.0D, 1.0D);
					tessellator.addVertexWithUV(outlineRight, slotTop - 2, 0.0D, 1.0D, 0.0D);
					tessellator.addVertexWithUV(outlineLeft, slotTop - 2, 0.0D, 0.0D, 0.0D);
					tessellator.setColorOpaque_I(0x000000);
					tessellator.addVertexWithUV(outlineLeft + 1, slotTop + slotHeight + 1, 0.0D, 0.0D, 1.0D);
					tessellator.addVertexWithUV(outlineRight - 1, slotTop + slotHeight + 1, 0.0D, 1.0D, 1.0D);
					tessellator.addVertexWithUV(outlineRight - 1, slotTop - 1, 0.0D, 1.0D, 0.0D);
					tessellator.addVertexWithUV(outlineLeft + 1, slotTop - 1, 0.0D, 0.0D, 0.0D);
					tessellator.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				drawSlot(i, slotLeft, slotTop, slotHeight, tessellator);
			}
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		drawOverlay();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		drawGradient(tessellator);
		drawScrollBar(tessellator);

		drawFinish(mouseX, mouseY);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	protected int getScrollBarX() {
		return xPos + width - scrollBarWidth + 1;
	}

	protected byte getGradientHeight() {
		return drawGradient ? gradientHeight : 0;
	}

	protected void drawContainerBackground(Tessellator tessellator) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawRect(xPos, yPos, xPos + width, yPos + height, 0xff000000);
	}

	protected void drawOverlay() {
		parentScreen.overlayBackground(xPos, yPos - slotHeight, width, slotHeight);
		parentScreen.overlayBackground(xPos, yPos + height, width, slotHeight);
	}

	protected void drawGradient(Tessellator tessellator) {
		if (drawGradient) {
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 0);
			tessellator.addVertexWithUV(xPos, yPos + gradientHeight, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(xPos + width, yPos + gradientHeight, 0.0D, 1.0D, 1.0D);
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(xPos + width, yPos, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(xPos, yPos, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(xPos, yPos + height, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(xPos + width, yPos + height, 0.0D, 1.0D, 1.0D);
			tessellator.setColorRGBA_I(0, 0);
			tessellator.addVertexWithUV(xPos + width, yPos + height - gradientHeight, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(xPos, yPos + height - gradientHeight, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
		}
	}

	protected void drawScrollBar(Tessellator tessellator) {
		int hiddenHeight = getHiddenHeight();
		if (hiddenHeight > 0) {
			int slotBottom = height * height / getContentHeight();

			if (slotBottom < 32) {
				slotBottom = 32;
			}

			if (slotBottom > height - getGradientHeight() * 2) {
				slotBottom = height - getGradientHeight() * 2;
			}

			int scrollBarX = getScrollBarX();
			int scrollBarRight = scrollBarX + scrollBarWidth - 1;
			int scrollBarY = (int) amountScrolled * (height - slotBottom) / hiddenHeight + yPos;
			if (scrollBarY < yPos) {
				scrollBarY = yPos;
			}

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(scrollBarX, yPos + height, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(scrollBarRight, yPos + height, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(scrollBarRight, yPos, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(scrollBarX, yPos, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0x808080, 255);
			tessellator.addVertexWithUV(scrollBarX, scrollBarY + slotBottom, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(scrollBarRight, scrollBarY + slotBottom, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(scrollBarRight, scrollBarY, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(scrollBarX, scrollBarY, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0xc0c0c0, 255);
			tessellator.addVertexWithUV(scrollBarX, scrollBarY + slotBottom - 1, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(scrollBarRight - 1, scrollBarY + slotBottom - 1, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(scrollBarRight - 1, scrollBarY, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(scrollBarX, scrollBarY, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
		}
	}

}
