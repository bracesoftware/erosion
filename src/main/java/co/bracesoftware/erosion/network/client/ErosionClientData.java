package co.bracesoftware.erosion.network.client;

import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionRetrogen;

public class ErosionClientData
{
    public static String CACHED_STATUS_STRING = "No data yet!";
    public static class ConfigFromServer
    {
        public static Boolean AGGRESIVE_GEOCHEMICAL_ALTERATION = false;
    }

    public static void update(
        int pending, long performed, int pendingfast, long performed2,
        boolean agal,
        int retrogen
    )
    {
        CACHED_STATUS_STRING = formatModStatusString(pending, performed, pendingfast, performed2);
        ConfigFromServer.AGGRESIVE_GEOCHEMICAL_ALTERATION = agal;
        ErosionRetrogen.RetrogenFeature.RETROGEN_PERFORMED = retrogen;
    }

    public static String formatModStatusString(int pending, long performed, int pendingfast, long performed2)
    {
        int max = ErosionConfig.MAX_PENDING_SIZE;
        int maxfast = ErosionConfig.MAX_PENDING_FAST_SIZE;
        double consumption = (
            ErosionCore.PENDING.getUsedMemory() + ErosionCore.PENDING_FAST.getUsedMemory()
        ) / 1024.0;
        double maxAllocated = (
            ErosionCore.PENDING.getMaxAllocatedMemory() + ErosionCore.PENDING_FAST.getMaxAllocatedMemory()
        ) / 1024.0;

        return String.format(
            "Pending: %d/%d, +%d/%d with high priority (%.2f/%.2f KiB) | Performed: %d, +%d with high priority",
            pending, max, pendingfast, maxfast, consumption, maxAllocated, performed, performed2
        );
    }
}