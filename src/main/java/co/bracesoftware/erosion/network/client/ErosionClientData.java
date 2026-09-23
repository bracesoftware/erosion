package co.bracesoftware.erosion.network.client;

import co.bracesoftware.erosion.ErosionClient;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionRetrogen;
import net.neoforged.api.distmarker.Dist;

public class ErosionClientData
{
    public static int CLIENT_UNTIL = 0; 
    public static String CACHED_STATUS_STRING = "No data yet!";
    public static class ConfigFromServer
    {
        public static boolean AGGRESIVE_GEOCHEMICAL_ALTERATION = false;
    }

    public static void sendMessage(String text, int col)
    {
        ErosionClient.ErosionScreenMessage.addMessage(text, ErosionScreenMessage.Color.getColorObject(col));
    }

    public static void updateModStatus(
        int pending, long performed, int pendingfast, long performed2,
        boolean agal,
        int retrogen, int pendingagain,int until
    )
    {
        ErosionClientData.CACHED_STATUS_STRING = formatModStatusString(pending, performed, pendingfast, performed2,pendingagain);
        ConfigFromServer.AGGRESIVE_GEOCHEMICAL_ALTERATION = agal;
        ErosionRetrogen.RetrogenFeature.RETROGEN_PERFORMED = retrogen;
        ErosionClientData.CLIENT_UNTIL = until;
    }

    public static String formatModStatusString(int pending, long performed, int pendingfast, long performed2, int pendingagain)
    {
        int max = ErosionConfig.MAX_PENDING_SIZE;
        int maxfast = ErosionConfig.MAX_PENDING_FAST_SIZE + ErosionConfig.MAX_PENDING_AGAIN_SIZE;
        double consumption = (
            ErosionCore.PENDING.getUsedMemory() +
            ErosionCore.PENDING_FAST.getUsedMemory() +
            ErosionCore.PENDING_AGAIN.getUsedMemory()
        ) / 1024.0;
        double maxAllocated = (
            ErosionCore.PENDING.getMaxAllocatedMemory() +
            ErosionCore.PENDING_FAST.getMaxAllocatedMemory() +
            ErosionCore.PENDING_AGAIN.getMaxAllocatedMemory()
        ) / 1024.0;

        return String.format(
            "Pending: %d/%d, +%d/%d with high priority (%.2f/%.2f KiB) | Performed: %d, +%d with high priority",
            pending, max, pendingfast + pendingagain, maxfast, consumption, maxAllocated, performed, performed2
        );
    }
}