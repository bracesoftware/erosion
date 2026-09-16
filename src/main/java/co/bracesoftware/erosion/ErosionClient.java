package co.bracesoftware.erosion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionClient
{
    @SubscribeEvent 
    public static void GG(ScreenEvent.Render.Post e)
    {
        if(e.getScreen() instanceof TitleScreen sc)
        {
            GuiGraphics g = e.getGuiGraphics();
            var f = sc.getMinecraft().font;
            
            g.drawString(
                f, Erosion.MOD_BRANDING,
                10,10,0xFFFFFF, true
            );
        }
        return;
    }
}