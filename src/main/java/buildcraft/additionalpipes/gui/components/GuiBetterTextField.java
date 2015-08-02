/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package additionalpipes.client.gui.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBetterTextField extends GuiTextField {

	private int xPos;
	private int yPos;
	private int width;
	private int height;

	private IGuiIndirectButtons parentScreen;
	private GuiButton buttonReturn;
	private String allowedChars;
	private String defaultText = "";

	public int unmodifiedColor = 0xe0e0e0;
	public int modifiedColor = 0xac00d5;
	public int disabledColor = 0x707070;

	public GuiBetterTextField(FontRenderer fontRenderer, int xPos, int yPos, int width, int height) {
		super(fontRenderer, xPos, yPos, width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		defaultText = getText();
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	@Override
	public void drawTextBox() {
		if (getText().equals(defaultText)) {
			setTextColor(unmodifiedColor);
		} else {
			setTextColor(modifiedColor);
		}
		setDisabledTextColour(disabledColor);
		super.drawTextBox();
	}

	@Override
	public boolean textboxKeyTyped(char c, int keycode) {
		boolean result = super.textboxKeyTyped(c, keycode);
		if (!result && isFocused() && keycode == Keyboard.KEY_RETURN) {
			if (parentScreen != null && buttonReturn != null && buttonReturn.enabled && buttonReturn.drawButton) {
				parentScreen.buttonPressed(buttonReturn);
			}
			return true;
		}
		return result;
	}

	@Override
	public void writeText(String text) {
		if (allowedChars != null) {
			StringBuilder sb = new StringBuilder(text.length());
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (allowedChars.indexOf(c) >= 0) {
					sb.append(c);
				}
			}

			text = sb.toString();
		}
		super.writeText(text);
	}

	public boolean contains(int posx, int posy) {
		return posx >= xPos && posx < xPos + width && posy >= yPos && posy < yPos + height;
	}

	public void setAllowedCharacters(String s) {
		allowedChars = s;
	}

	public void setReturnButton(IGuiIndirectButtons screen, GuiButton button) {
		parentScreen = screen;
		buttonReturn = button;
	}

}
