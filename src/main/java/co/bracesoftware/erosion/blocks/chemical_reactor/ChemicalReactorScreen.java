package co.bracesoftware.erosion.blocks.chemical_reactor;

import co.bracesoftware.erosion.Erosion;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChemicalReactorScreen extends AbstractContainerScreen<ChemicalReactorMenu>
{
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation
        .withDefaultNamespace("container/furnace/burn_progress");
    private static final ResourceLocation WINDOW_BG = ResourceLocation
        .fromNamespaceAndPath(Erosion.MODID, "textures/gui/container/chemical_reactor.png");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation
        .withDefaultNamespace("container/slot");

    public ChemicalReactorScreen(ChemicalReactorMenu m, Inventory pinv, Component t)
    {
        super(m,pinv,t);
        this.imageWidth = 176;
        this.imageHeight = 190;
    }

    @Override
    protected void renderBg(GuiGraphics g, float p, int xx, int yy)
    {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        g.blit(WINDOW_BG, x, y, 0, 0, this.imageWidth, this.imageHeight);
        for(int row = 0; row < ChemicalReactorMenu.ROWS; row++)
        {
            for(int col = 0; col < ChemicalReactorMenu.COL; col++)
            {
                g.blitSprite(SLOT_SPRITE, x + 30 + col * 18 - 1, y + 17 + row * 18 - 1, 18, 18);
            }
        }

        for(int row = 0; row < ChemicalReactorMenu.ROWS; row++)
        {
            for(int col = 0; col < ChemicalReactorMenu.COL; col++)
            {
                g.blitSprite(SLOT_SPRITE, x + 124 + col * 18 - 1, y + 17 + row * 18 - 1, 18, 18);
            }
        }
        g.blitSprite(BURN_PROGRESS_SPRITE, x + 82, y + 35, 24, 16);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int x, int y)
    {
        super.renderLabels(g, x, y);
        
        g.drawString(this.font, Component.literal("Reactants"), 22, 73, 0x404040, false);
        g.drawString(this.font, Component.literal("Products"), 120, 73, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics g, int x, int y, float t)
    {
        super.render(g, x, y, t);
        this.renderTooltip(g, x, y);
        return;
    }
}