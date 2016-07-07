package simplyhorses.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonSH extends GuiButton{
	protected int field_52008_a;
    protected int field_52007_b;

    /** The x position of this control. */
    public int xPosition;

    /** The y position of this control. */
    public int yPosition;

    /** The string displayed on this control. */
    public String displayString;

    /** ID for this control. */
    public int id;

    /** True if this control is enabled, false to disable. */
    public boolean enabled;

    /** Hides the button completely if false. */
    public boolean drawButton;

    public GuiButtonSH(int par1, int par2, int par3, String par4Str)
    {
        this(par1, par2, par3, 200, 20, par4Str);
    }

    public GuiButtonSH(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
    	super(par1, par2, par3, par4, par5, par6Str);
        field_52008_a = 200;
        field_52007_b = 20;
        enabled = true;
        drawButton = true;
        id = par1;
        xPosition = par2;
        yPosition = par3;
        field_52008_a = par4;
        field_52007_b = par5;
        displayString = par6Str;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean par1)
    {
        byte byte0 = 1;

        if (!enabled)
        {
            byte0 = 0;
        }
        else if (par1)
        {
            byte0 = 2;
        }

        return byte0;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        mouseDragged(par1Minecraft, par2, par3);
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft minecraft, int i, int j)
    {
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int i, int j)
    {
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        return enabled && drawButton && par2 >= xPosition && par3 >= yPosition && par2 < xPosition + field_52008_a && par3 < yPosition + field_52007_b;
    }
}
