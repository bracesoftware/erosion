package co.bracesoftware.erosion.network.client;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.libs.MinecraftTextFormat;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionDebugOverlay
{
    public static final String MAIN_STYLE = MinecraftTextFormat.Format(
        MinecraftTextFormat.Col.DARK_RED, MinecraftTextFormat.Style.BOLD
    ) + "[Erosion] " + MinecraftTextFormat.Format(
        MinecraftTextFormat.Col.GRAY
    );
    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event)
    {
        event.getLeft().add("");
        event.getLeft().add(
            MAIN_STYLE +
            ErosionClientData.CACHED_STATUS_STRING
        );
        event.getLeft().add(
            MAIN_STYLE +
            MinecraftTextFormat.Format(MinecraftTextFormat.Col.AQUA) + 
            ErosionConfig.ServerConfig.AGRESSIVE_GEOCHEMICAL_ALTERATION.getName() + ": " + 
            (
                ErosionClientData.ConfigFromServer.AGGRESIVE_GEOCHEMICAL_ALTERATION ? (
                    MinecraftTextFormat.Format(MinecraftTextFormat.Col.GREEN) + "true"
                ) : (
                    MinecraftTextFormat.Format(MinecraftTextFormat.Col.RED) + "false"
                )
            )
        );
    }
}