package co.bracesoftware.erosion;

import java.util.List;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorScreen;
import co.bracesoftware.libs.minecraft_text_formatter.ComponentWordWrap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
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

    @SubscribeEvent 
    public static void onTooltip(ItemTooltipEvent e)
    {
        Item currentItem = e.getItemStack().getItem();
        var tooltip = e.getToolTip();

        if(ErosionCore.ITEM_DESCRIPTIONS.containsKey(currentItem))
        {
            var a = ErosionCore.ITEM_DESCRIPTIONS.get(currentItem);
            for(int i = 0; i < a.size(); i++)
            {
                Component c = a.get(i).copy().withStyle(s -> s.withFont(ErosionConfig.MINI_FONT));
                List<Component> f = ErosionConfig.Libs.COMPONENT_WORD_WRAP ? ComponentWordWrap.Format(
                    c, ErosionConfig.Libs.MAX_WORDS_PER_COMPONENT_LINE
                ) : List.of(c);
                for(var p : f)
                {
                    tooltip.add(p);
                }
            }
            return;
        }

        var b = ErosionCore.setupItemDescription(currentItem);
        for(var c : b)
        {
            Component f = c.copy().withStyle(s -> s.withFont(ErosionConfig.MINI_FONT));
            tooltip.add(f);
        }
        return;
    }

    @SubscribeEvent
    public static void regScreen(RegisterMenuScreensEvent e)
    {
        e.register(ErosionRegistry.Menus.CHEMICAL_REACTOR.get(), ChemicalReactorScreen::new);
        return;
    }
}