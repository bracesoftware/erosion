package co.bracesoftware.erosion;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorScreen;
import co.bracesoftware.libs.minecraft_text_formatter.ComponentWordWrap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionClient
{
    public static class ErosionScreenMessage implements LayeredDraw.Layer
    {
        public static class Colors
        {
            public static final int BLACK = 0x000000;
            public static final int DARK_BLUE = 0x0000AA;
            public static final int DARK_GREEN = 0x00AA00;
            public static final int DARK_AQUA = 0x00AAAA;
            public static final int DARK_RED = 0xAA0000;
            public static final int DARK_PURPLE = 0xAA00AA;
            public static final int GOLD = 0xFFAA00;
            public static final int GRAY = 0xAAAAAA;
            public static final int DARK_GRAY = 0x555555;
            public static final int BLUE = 0x5555FF;
            public static final int GREEN = 0x55FF55;
            public static final int AQUA = 0x55FFFF;
            public static final int RED = 0xFF5555;
            public static final int LIGHT_PURPLE = 0xFF55FF;
            public static final int YELLOW = 0xFFFF55;
            public static final int WHITE = 0xFFFFFF;
        }
        private static class DisplayEntry
        {
            final String text;
            final long time;
            final int color;

            DisplayEntry(String t, int c)
            {
                this.color = c;
                this.text = t;
                this.time = System.currentTimeMillis();
            }

            @Override 
            public boolean equals(Object o)
            {
                if(this == o) return true;
                if(o == null || this.getClass() != o.getClass()) return false;
                var dat = (DisplayEntry) o;
                return Objects.equals(this.text, dat.text);
            }

            @Override 
            public int hashCode()
            {
                return Objects.hash(this.text);
            }
        }

        private static final List<DisplayEntry> MESSAGES = new ArrayList<>();
        public static final long DISPLAY_TIME_MS = 4000;
        public static final int START_FADE_AT_REMAINING = 500;

        public static void addMessage(String t, int col)
        {
            synchronized(MESSAGES)
            {
                if(MESSAGES.contains(new DisplayEntry(t, col)))
                {
                    return;
                }
                if(MESSAGES.size() >= 10)
                {
                    MESSAGES.remove(0);
                }
                MESSAGES.add(new DisplayEntry(t, col));
            }
            return;
        }

        @Override 
        public void render(GuiGraphics gg, DeltaTracker dt)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player == null || mc.options.hideGui) return;

            long now = System.currentTimeMillis();
            synchronized(MESSAGES)
            {
                var it = MESSAGES.iterator();
                while(it.hasNext())
                {
                    var entry = it.next();
                    if(now - entry.time > DISPLAY_TIME_MS)
                    {
                        it.remove();
                    }
                }

                if(MESSAGES.isEmpty()) return;
                int cx = gg.guiWidth() / 2;
                int start = (gg.guiHeight() / 2) + 80;

                for(int i = 0; i < MESSAGES.size(); i++)
                {
                    var msg = MESSAGES.get(i);
                    long age = now - msg.time;
                    float a = 1.0f;
                    if(age > DISPLAY_TIME_MS - START_FADE_AT_REMAINING)
                    {
                        a = (DISPLAY_TIME_MS - age) / (float) START_FADE_AT_REMAINING;
                    }
                    a = Mth.clamp(a, 0f, 1f);
                    int col = ((int) (a * 255) << 24) | msg.color;

                    int w = mc.font.width(msg.text);
                    int x = cx - (w / 2);
                    int y = start + (i * 11);

                    gg.drawString(mc.font, msg.text, x, y, col, true);
                }
            }
            return;
        }
    }

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

    @SubscribeEvent 
    public static void regGuiLayer(RegisterGuiLayersEvent e)
    {
        e.registerAbove(
            VanillaGuiLayers.CROSSHAIR,
            ErosionRegistry.ErosionRenderingElements.SCREEN_MESSAGE,
            new ErosionScreenMessage()
        );
        return;
    }
}