package simplyhorses.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import simplyhorses.common.entities.IInventoryEntitySH;
import simplyhorses.common.entities.vehicles.EntityVardoSH;
import simplyhorses.common.inventory.ContainerSH;

public class GuiVardoSH extends GuiContainer {
	
	private GuiButtonSH sleep;
	private EntityVardoSH vardo;
	private EntityPlayer player;

	public GuiVardoSH(EntityPlayer entityplayer, InventoryPlayer inventoryplayer, IInventoryEntitySH entityvardo) {
		super(new ContainerSH(inventoryplayer, entityvardo));
		
        allowUserInput = false;
        xSize = 195;
        ySize = 222;
        vardo = (EntityVardoSH) entityvardo;
        player = entityplayer;
	}
	
	public void initGui()
    {
    	super.initGui();
    	buttonList.clear();
    	buttonList.add(sleep = new GuiButtonSH(1, width / 2 + 74, height / 2 - 94, 18, 34, "")); 
    }

	@Override
    public void drawScreen(int i, int j, float f)
    {
    	super.drawScreen(i, j, f);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2)
    {
    	fontRenderer.drawString("Vardo", 8, 7, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 3, 0x404040);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/SimplyHorses/textures/gui/vardo.png");
        int j2 = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j2, k, 0, 0, xSize, ySize);
	}
	
	protected void actionPerformed(GuiButton guibutton)
    {
		if(guibutton.id == 1){
			if (vardo.setPlayerSleeping(player)){
				mc.thePlayer.closeScreen();
			}
		}
    }


}
