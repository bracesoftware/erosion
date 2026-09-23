package co.bracesoftware.erosion.network.client;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionRetrogen;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.libs.minecraft_text_formatter.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionDebugOverlay
{
    public static final String MAIN_STYLE = Text.Format(
        Text.Col.DARK_RED, Text.Style.BOLD
    ) + "{" + Erosion.MODNAME + "} " + Text.Format(
        Text.Col.GRAY
    );
    
    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText e)
    {
        e.getRight().add("");
        e.getRight().add(
            MAIN_STYLE + 
            Text.Format(Text.Col.DARK_GREEN) +
            "Installed: build " + Erosion.BUILD
        );
        e.getRight().add(
            MAIN_STYLE + Text.Format(Text.Col.DARK_AQUA) +
            "Snapshot: " + Erosion.SNAPSHOT
        );

        e.getLeft().add("");
        e.getLeft().add(
            MAIN_STYLE +
            ErosionClientData.CACHED_STATUS_STRING
        );
        e.getLeft().add(
            MAIN_STYLE +
            Text.Format(Text.Col.AQUA) + 
            ErosionConfig.ServerConfig.AGRESSIVE_GEOCHEMICAL_ALTERATION.getName() + ": " + 
            (
                ErosionClientData.ConfigFromServer.AGGRESIVE_GEOCHEMICAL_ALTERATION ? (
                    Text.Format(Text.Col.GREEN) + "true"
                ) : (
                    Text.Format(Text.Col.RED) + "false"
                )
            )
        );
        e.getLeft().add(
            MAIN_STYLE +
            "Retrogen blocks generated: " + 
            Text.Format(Text.Col.GOLD) +
            ErosionRetrogen.RetrogenFeature.RETROGEN_PERFORMED
        );

        e.getLeft().add(
            MAIN_STYLE +
            "Geochemical process wave in: " + 
            Text.Format(Text.Col.DARK_AQUA) +
            ErosionUtils.tickToFormattedTime(ErosionClientData.CLIENT_UNTIL)
        );
        return;
    }
}