package co.bracesoftware.erosion.blocks;

import co.bracesoftware.erosion.blocks.ErosionSimpleBlocks.GravelBlock;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class ErosionSimpleBlocks
{
    public static class GravelBlock extends FallingBlock
    {
        public static final MapCodec<GravelBlock> CODEC = simpleCodec(GravelBlock::new);

        public GravelBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends FallingBlock> codec() {
            return CODEC;
        }

        public static BlockBehaviour.Properties getDefaultBlockProperties()
        {
            return BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.SNARE)
                    .strength(0.6F)
                    .sound(SoundType.GRAVEL);
        }
    }

    public static class StoneBlock extends Block
    {
        public static BlockBehaviour.Properties getDefaultBlockProperties()
        {
            return BlockBehaviour.Properties.of().
                strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.DEEPSLATE)
                .mapColor(MapColor.STONE);
        }

        public StoneBlock(BlockBehaviour.Properties p)
        {
            super(p);
        }
    }

    public static class DirtBlock extends FallingBlock
    {
        public static final MapCodec<DirtBlock> CODEC = simpleCodec(DirtBlock::new);

        @Override
        protected MapCodec<? extends FallingBlock> codec() {
            return CODEC;
        }

        public static BlockBehaviour.Properties getDefaultBlockProperties()
        {
            return BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .instrument(NoteBlockInstrument.SNARE)
                    .strength(0.6F)
                    .sound(SoundType.GRAVEL);
        }
        public DirtBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }
    }
}