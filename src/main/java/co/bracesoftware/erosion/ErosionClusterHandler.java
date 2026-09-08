package co.bracesoftware.erosion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionClusterHandler
{
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server == null) return;

        ServerLevel overworld = server.overworld();
        
        int simDistanceChunks = server.getPlayerList().getSimulationDistance();
        int maxRadius = simDistanceChunks * ErosionConfig.CHUNK_SIZE;

        for(ServerPlayer player : overworld.players())
        {
            selectVeinErosionAroundPlayer(overworld, player.blockPosition(), maxRadius);
        }
    }

    private static void selectVeinErosionAroundPlayer(ServerLevel level, BlockPos playerPos, int maxRadius)
    {
        int minRadiusSq = ErosionConfig.Clusters.MIN_SPAWN_DISTANCE * ErosionConfig.Clusters.MIN_SPAWN_DISTANCE;
        int maxRadiusSq = maxRadius * maxRadius;
        int clusterRadius = ErosionConfig.Clusters.SIZE / 2;
        int clusterRadiusSq = clusterRadius * clusterRadius;

        for(int c = 0; c < ErosionConfig.Clusters.COUNT_PER_TICK; c++)
        {
            int baseOffsetX = 0, baseOffsetZ = 0, distanceSq = 0;
            int attempts = 0;

            do {
                baseOffsetX = ErosionMod.RANDOM.nextInt(maxRadius * 2 + 1) - maxRadius;
                baseOffsetZ = ErosionMod.RANDOM.nextInt(maxRadius * 2 + 1) - maxRadius;
                distanceSq = baseOffsetX * baseOffsetX + baseOffsetZ * baseOffsetZ;
                attempts++;
            } while ((distanceSq < minRadiusSq || distanceSq > maxRadiusSq) && attempts < 15);

            if(attempts >= 15) continue;

            int baseOffsetY = ErosionMod.RANDOM.nextInt(ErosionConfig.CHUNK_SIZE * 2) - ErosionConfig.CHUNK_SIZE;
            BlockPos clusterCenter = playerPos.offset(baseOffsetX, baseOffsetY, baseOffsetZ);

            for(int x = -clusterRadius; x <= clusterRadius; x++)
            {
                for(int y = -clusterRadius; y <= clusterRadius; y++)
                {
                    for(int z = -clusterRadius; z <= clusterRadius; z++)
                    {
                        if(x * x + y * y + z * z <= clusterRadiusSq)
                        {
                            BlockPos targetPos = clusterCenter.offset(x, y, z);
                            for(var f : ErosionRetrogen.RETROGEN_FEATURES)
                            {
                                ErosionRetrogen.applyFeatureToChunk(level, targetPos, f);
                            }
                            ErosionCore.addCandidatePriority(level, targetPos);
                        }
                    }
                }
            }
        }
        return;
    }
}