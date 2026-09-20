package co.bracesoftware.erosion;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorScreen;
import co.bracesoftware.libs.minecraft_text_formatter.ComponentWordWrap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
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
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionClient
{
    public static class ErosionScreenMessage implements LayeredDraw.Layer
    {
        public static enum Color
        {
            BLACK(0x000000), DARK_BLUE(0x0000AA), DARK_GREEN(0x00AA00),
            DARK_AQUA(0x00AAAA), DARK_RED(0xAA0000), DARK_PURPLE(0xAA00AA),
            GOLD(0xFFAA00), GRAY(0xAAAAAA), DARK_GRAY(0x555555),
            BLUE(0x5555FF), GREEN(0x55FF55), AQUA(0x55FFFF),
            RED(0xFF5555), LIGHT_PURPLE(0xFF55FF), YELLOW(0xFFFF55),
            WHITE(0xFFFFFF);

            private final int col;

            Color(int c)
            {
                this.col = c;
            }

            public int getColor()
            {
                return this.col;
            }

            // ==================== STATICZ
            private static final Map<Integer, Color> MAPPING = new HashMap<>();

            static
            {
                for(var p : Color.values())
                {
                    MAPPING.put(p.getColor(), p);
                }
            }

            public static Color getColorObject(int c)
            {
                return MAPPING.getOrDefault(c, WHITE);
            }
        }

        private static class DisplayEntry
        {
            public static final long DISPLAY_TIME_MS = 4000;
            public static final int START_FADE_AT_REMAINING = 500;
            public static final int OFFSET_RANGE = 15;

            final String text;
            final long time;
            final Color color;

            int offset = OFFSET_RANGE;
            boolean fadeOut = false;

            DisplayEntry(String t, Color c)
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

        public static void addMessage(String t, Color col)
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
                    if(now - entry.time > DisplayEntry.DISPLAY_TIME_MS)
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
                    if(age > DisplayEntry.DISPLAY_TIME_MS - DisplayEntry.START_FADE_AT_REMAINING)
                    {
                        a = (DisplayEntry.DISPLAY_TIME_MS - age) / (float) DisplayEntry.START_FADE_AT_REMAINING;
                        msg.fadeOut = true;
                    }
                    a = Mth.clamp(a, 0f, 1f);
                    int col = ((int) (a * 255) << 24) | msg.color.getColor();

                    int w = mc.font.width(msg.text);
                    int x = cx - (w / 2) - msg.offset;
                    int y = start + (i * 11);
                    if(!(msg.offset <= 0)) --msg.offset;
                    if(msg.fadeOut)
                    {
                        long rt = DisplayEntry.DISPLAY_TIME_MS - age;
                        float fop = 1f - ((float) rt / DisplayEntry.START_FADE_AT_REMAINING);
                        fop = Mth.clamp(fop, 0f, 1f);
                        msg.offset = DisplayEntry.OFFSET_RANGE * (int) (1f - fop);
                    }

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