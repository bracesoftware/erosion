package co.bracesoftware.erosion.blocks;

import co.bracesoftware.erosion.blocks.ErosionSimpleBlocks.GravelBlock;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
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
        public static final Integer SHAPE_FIRSTDIM_X1 = 5;
        public static final Integer SHAPE_FIRSTDIM_Y1 = 0;
        public static final Integer SHAPE_FIRSTDIM_Z1 = 4;
        public static final Integer SHAPE_FIRSTDIM_X2 = 11;
        public static final Integer SHAPE_FIRSTDIM_Y2 = 2;
        public static final Integer SHAPE_FIRSTDIM_Z2 = 10;

        public static final Integer SHAPE_SECONDDIM_X1 = 8;
        public static final Integer SHAPE_SECONDDIM_Y1 = 0;
        public static final Integer SHAPE_SECONDDIM_Z1 = 9;
        public static final Integer SHAPE_SECONDDIM_X2 = 12;
        public static final Integer SHAPE_SECONDDIM_Y2 = 1;
        public static final Integer SHAPE_SECONDDIM_Z2 = 12;

        public static final Integer SHAPE_THIRDDIM_X1 = 6;
        public static final Integer SHAPE_THIRDDIM_Y1 = 2;
        public static final Integer SHAPE_THIRDDIM_Z1 = 5;
        public static final Integer SHAPE_THIRDDIM_X2 = 9;
        public static final Integer SHAPE_THIRDDIM_Y2 = 3;
        public static final Integer SHAPE_THIRDDIM_Z2 = 8;

        private static final VoxelShape SHAPE = Shapes.or(
            Block.box(
                SHAPE_FIRSTDIM_X1, SHAPE_FIRSTDIM_Y1, SHAPE_FIRSTDIM_Z1,
                SHAPE_FIRSTDIM_X2, SHAPE_FIRSTDIM_Y2, SHAPE_FIRSTDIM_Z2
            ),
            Block.box(
                SHAPE_SECONDDIM_X1, SHAPE_SECONDDIM_Y1, SHAPE_SECONDDIM_Z1,
                SHAPE_SECONDDIM_X2, SHAPE_SECONDDIM_Y2, SHAPE_SECONDDIM_Z2
            ),
            Block.box(
                SHAPE_THIRDDIM_X1, SHAPE_THIRDDIM_Y1, SHAPE_THIRDDIM_Z1,
                SHAPE_THIRDDIM_X1, SHAPE_THIRDDIM_Y2, SHAPE_THIRDDIM_Z2
            )
        );

        public static BlockBehaviour.Properties getDefaultBlockProperties()
        {
            return BlockBehaviour.Properties.of().
                strength(0.1f, 3.0f)
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
        @Override
        protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hitResult
        ) 
        {
            if(!level.isClientSide())
            {
                ItemStack rockStack = new ItemStack(this.asItem());
                boolean a = player.getInventory().add(rockStack);
                if(!a) Block.popResource(level, pos, rockStack);

                level.playSound(
                    null, 
                    pos, 
                    SoundEvents.ITEM_PICKUP, 
                    SoundSource.PLAYERS, 
                    0.2F, 
                    (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F
                );
                level.removeBlock(pos, false);
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        @Override
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
        {
            BlockPos posBelow = pos.below();
            BlockState stateBelow = level.getBlockState(posBelow);

            if(stateBelow.getBlock() instanceof RockBlock) return false;
            return stateBelow.isFaceSturdy(level, posBelow, Direction.UP);
        }

        @Override
        public BlockState updateShape(
            BlockState state, Direction facing, BlockState facingState,
            LevelAccessor level, BlockPos currentPos, BlockPos facingPos
        )
        {
            if(facing == Direction.DOWN && !state.canSurvive(level, currentPos))
            {
                return Blocks.AIR.defaultBlockState();
            }
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }
}