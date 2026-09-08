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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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

    public static class RockBlock extends Block
    {
        public static final int MIN_XZ = 4;
        public static final int MAX_XZ = 12;

        public static final int INNER_MIN_XZ = 5;
        public static final int INNER_MAX_XZ = 11;

        public static final int Y_MIN = 0;
        public static final int Y_MID = 2;
        public static final int Y_MAX = 3;

        private static final VoxelShape SHAPE = Shapes.or(
            Block.box(MIN_XZ, Y_MIN, MIN_XZ, MAX_XZ, Y_MID, MAX_XZ),               // Donji širi dio
            Block.box(INNER_MIN_XZ, Y_MID, INNER_MIN_XZ, INNER_MAX_XZ, Y_MAX, INNER_MAX_XZ) // Gornji uži dio
        );

        public static BlockBehaviour.Properties getDefaultBlockProperties()
        {
            return BlockBehaviour.Properties.of().
                strength(1.5f, 6.0f)
                .sound(SoundType.DEEPSLATE)
                .mapColor(MapColor.DEEPSLATE);
        }

        public RockBlock(Properties p)
        {
            super(p);
        }

        @Override 
        public VoxelShape getShape(
            BlockState bs,
            BlockGetter bg,
            BlockPos bp,
            CollisionContext c
        )
        {
            return SHAPE;
        }
    }
}