package co.bracesoftware.erosion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.google.gson.reflect.TypeToken;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.io.Writer;
import java.io.Reader;

import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionRetrogen
{
    public static class RetrogenFeature
    {
        public static final Integer MAX_REPLACEMENTS_PER_CHUNK = 10;
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
            Blocks.FERN
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

    private static List<BlockPos> getRandomSurfacePositionsAround(Level level, BlockPos center, int radius, int count, RandomSource random)
    {
        List<BlockPos> positions = new ArrayList<>();

        int minX = (center.getX() >> 4) << 4;
        int maxX = minX + 15;
        int minZ = (center.getZ() >> 4) << 4;
        int maxZ = minZ + 15;

        for (int i = 0; i < count; i++)
        {
            int dx = random.nextInt(-radius, radius + 1);
            int dz = random.nextInt(-radius, radius + 1);

            int targetX = Math.max(minX, Math.min(maxX, center.getX() + dx));
            int targetZ = Math.max(minZ, Math.min(maxZ, center.getZ() + dz));

            // Pronalazi visinu gornjeg bloka terena na ovim X, Z koordinatama
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, targetX, targetZ) - 1;

            positions.add(new BlockPos(targetX, surfaceY, targetZ));
        }

        return positions;
    }

    public static void applyFeatureToChunk(ServerLevel l, BlockPos p, RetrogenFeature f)
    {
        long cp = ChunkPos.asLong(p.getX() >> 4, p.getZ() >> 4);
        var m = ErosionRegistry.DataAttachments.RETROGEN_DATA;

        if(m.containsKey(cp))
        {
            var k = m.get(cp);
            if(k.contains(f.getId()))
            {
                return;
            }
        }
        List<BlockPos> v = new ArrayList<>();
        List<BlockPos> v2 = getRandomSurfacePositionsAround(
            l, p, 4, RetrogenFeature.MAX_REPLACEMENTS_PER_CHUNK, ErosionMod.RANDOM
        );

        for(var pos : v2)
        {
            BlockState state = l.getBlockState(pos);
            if(f.toReplace.contains(state.getBlock()))
            {
                v.add(pos);
            }
        }

        if(v.isEmpty())
        {
            return;
        }

        for(int i = 0; i < v.size(); i++)
        {
            BlockPos pos = v.get(i);
            Block randomRock = f.toPlace.get(ErosionMod.RANDOM.nextInt(f.toPlace.size()));
            l.setBlock(pos, randomRock.defaultBlockState(), 2);
            RetrogenFeature.RETROGEN_PERFORMED++;
        }

        m.computeIfAbsent(
            cp, k -> new ArrayList<>()
        ).add(f.getId());
        return;
    }
    
    public static class RetrogenDataManager
    {
        public static void saveRetrogenData(MinecraftServer server, String filename, Long2ObjectMap<List<String>> data)
        {
            Path dataDir = server.getWorldPath(LevelResource.ROOT).resolve("data");
            File file = dataDir.resolve(filename + ".json").toFile();

            try(Writer writer = new FileWriter(file))
            {
                ErosionMod.GSON.toJson(data, writer);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return;
        }

        public static Long2ObjectMap<List<String>> loadRetrogenData(MinecraftServer server, String filename)
        {
            Long2ObjectMap<List<String>> result = new Long2ObjectOpenHashMap<>();

            Path dataDir = server.getWorldPath(LevelResource.ROOT).resolve("data");
            File file = dataDir.resolve(filename + ".json").toFile();

            if(!file.exists())
            {
                return result;
            }

            try(Reader reader = new FileReader(file))
            {
                var type = new TypeToken<Map<Long, List<String>>>() {}.getType();
                Map<Long, List<String>> rawMap = ErosionMod.GSON.fromJson(reader, type);

                if(rawMap != null)
                {
                    result.putAll(rawMap);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }
    }
}
