package buildcraft.additionalpipes.gui.components;

import net.minecraft.client.gui.GuiButton;

/**
 * GuiButton which can be set to a "permanently pressed" mode to indicate that the option it controls is enabled.
 * 
 * Sort of like a poor man's checkbox
 * @author Jamie
 *
 */
public class GuiButtonOnOff extends GuiButton
{
	boolean pressed;
	
	public boolean isPressed()
	{
		return pressed;
	}
	
	public void togglePressed()
	{
		pressed = !pressed;
	}

	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
	}

	public GuiButtonOnOff(int id, int x, int y, int width, boolean pressed, String text)
	{
		super(id, x, y, width, 20, text);
		
		this.pressed = pressed;
	}
	
    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
	@Override
    public int getHoverState(boolean isHoveredOver)
    {
        byte retval = 1;

        if (!this.enabled)
        {
            retval = 0;
        }
        else if(isHoveredOver || pressed)
        {
            retval = 2;
        }

        return retval;
    }

}
