package co.bracesoftware.erosion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionRetrogen
{
    public static class RetrogenFeature
    {
        public static final Integer MAX_REPLACEMENTS_PER_CHUNK = 100;
        public static Integer RETROGEN_PERFORMED = 0;

        private String id;
        private String name;
        protected Supplier<List<Block>> toReplaceSupplier;
        protected Supplier<List<Block>> toPlaceSupplier;

        public List<Block> toReplace;
        public List<Block> toPlace;
        
        public RetrogenFeature(String i, String n, Supplier<List<Block>> a, Supplier<List<Block>> b)
        {
            this.name = n;
            this.toReplaceSupplier = a;
            this.toPlaceSupplier = b;
            this.id = i;
        }

        public void setup()
        {
            ErosionUtils.Log("Setting up retrogen feature: " + this.name);
            
            this.toReplace = this.toReplaceSupplier.get();
            this.toPlace = this.toPlaceSupplier.get();
            return;
        }

        public String getName()
        {
            return this.name;
        }

        public String getId()
        {
            return this.id;
        }

    }

    public static final RetrogenFeature PLACE_ROCKS = new RetrogenFeature(
        "place_rocks", "Place rocks around the chunk",
        () -> List.of(
            Blocks.SHORT_GRASS,
            Blocks.TALL_GRASS
        ),
        () -> List.of(
            ErosionRegistry.Blocks.RAW_HEMATITE.get(),
            ErosionRegistry.Blocks.RAW_LIMONITE.get(),
            ErosionRegistry.Blocks.RAW_MAGNETITE.get(),
            ErosionRegistry.Blocks.RAW_MALACHITE.get(),

            ErosionRegistry.Blocks.NATIVE_SILVER.get(),
            ErosionRegistry.Blocks.NATIVE_GOLD.get(),

            ErosionRegistry.Blocks.RAW_BISMUTHINITE.get(),
            ErosionRegistry.Blocks.RAW_CASSITERITE.get(),
            ErosionRegistry.Blocks.RAW_SPHALERITE.get()
        )
    );

    public static final List<RetrogenFeature> RETROGEN_FEATURES = List.of(
        PLACE_ROCKS
    );

    public static void Setup()
    {
        ErosionUtils.Log(ErosionMod.WELCOME_ASCII);
        ErosionUtils.Log("Setting up Erosion retrogen module...");
        for(var r : RETROGEN_FEATURES)
        {
            r.setup();
        }
        return;
    }

    @SubscribeEvent 
    public static void onChunkLoad(ChunkEvent.Load e)
    {
        if(!(e.getLevel() instanceof ServerLevel level)) return;

        ChunkAccess chunk = e.getChunk();
        Set<String> appliedFeatures = chunk.getData(ErosionRegistry.DataAttachments.RETROGEN_DATA.get());

        boolean chunkModified = false;

        for(RetrogenFeature f : RETROGEN_FEATURES)
        {
            if(appliedFeatures.contains(f.getId())) continue;
            boolean a = applyFeatureToChunk(level, chunk, f);
            if(a)
            {
                appliedFeatures.add(f.getId());
                chunkModified = true;
            }
        }

        if(chunkModified)
        {
            chunk.setUnsaved(true);
        }
        return;
    }

    private static boolean applyFeatureToChunk(ServerLevel l, ChunkAccess c, RetrogenFeature f)
    {
        int minX = c.getPos().getMinBlockX();
        int minZ = c.getPos().getMinBlockZ();

        List<BlockPos> v = new ArrayList<>();

        for(int x = 0; x < 16; x++)
        {
            for(int z = 0; z < 16; z++)
            {
                int worldX = minX + x;
                int worldZ = minZ + z;

                int surfaceY = l.getHeight(Heightmap.Types.WORLD_SURFACE, worldX, worldZ) - 1;
                BlockPos pos = new BlockPos(worldX, surfaceY, worldZ);
                BlockState state = l.getBlockState(pos);

                if(f.toReplace.contains(state.getBlock()))
                {
                    v.add(pos);
                }
            }
        }

        if(v.isEmpty())
        {
            return false;
        }

        Collections.shuffle(v);
        int countToReplace = Math.min(v.size(), RetrogenFeature.MAX_REPLACEMENTS_PER_CHUNK);

        for(int i = 0; i < countToReplace; i++)
        {
            BlockPos pos = v.get(i);
            Block randomRock = f.toPlace.get(ErosionMod.RANDOM.nextInt(f.toPlace.size()));
            l.setBlock(pos, randomRock.defaultBlockState(), 2);
            RetrogenFeature.RETROGEN_PERFORMED++;
        }

        return true;
    }
}
