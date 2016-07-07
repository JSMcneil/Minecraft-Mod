package simplyhorses.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import simplyhorses.common.entities.IInventoryEntitySH;
import simplyhorses.common.inventory.ContainerSH;
import simplyhorses.common.inventory.ContainerWagonSH;

public class GuiWagonSH extends GuiContainer {

	private static ContainerWagonSH container;

    public GuiWagonSH(InventoryPlayer inventoryplayer, IInventoryEntitySH entitywagon, int i)
    {
        super(container = new ContainerWagonSH(inventoryplayer, entitywagon, i));
        allowUserInput = false;
        xSize = 176;
        ySize = 222;
    }

    public void drawScreen(int i, int j, float f)
    {
    	super.drawScreen(i, j, f);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2)
    {
    	fontRenderer.drawString("Cart", 8, 7, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 3, 0x404040);
    }

    @Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/SimplyHorses/textures/gui/cart.png");
        int j2 = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j2, k, 0, 0, xSize, ySize);
	}

}
