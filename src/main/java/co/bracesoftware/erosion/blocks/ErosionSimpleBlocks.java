package co.bracesoftware.erosion.blocks;

import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.blocks.ErosionSimpleBlocks.GravelBlock;

import java.util.EnumMap;
import java.util.Map;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;

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

        @Override 
        protected ItemInteractionResult useItemOn(
            ItemStack is, BlockState bs,
            Level l, BlockPos bp, Player p,
            InteractionHand ih, BlockHitResult bhr
        )
        {
            if(is.canPerformAction(ItemAbilities.HOE_TILL))
            {
                if(!l.isClientSide())
                {
                    l.setBlockAndUpdate(bp, Blocks.FARMLAND.defaultBlockState());
                    var s = (ih == InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    is.hurtAndBreak(1, p, s);
                    l.playSound(null, bp, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return ItemInteractionResult.sidedSuccess(l.isClientSide());
            }

            return super.useItemOn(is, bs, l, bp, p, ih, bhr);
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
        public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

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

        private static final VoxelShape OLD_SHAPE_IF_SOMETHING_GOES_WRONG = Shapes.or(
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
                SHAPE_THIRDDIM_X2, SHAPE_THIRDDIM_Y2, SHAPE_THIRDDIM_Z2
            )
        );

        private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(SHAPE_FIRSTDIM_X1, SHAPE_FIRSTDIM_Y1, SHAPE_FIRSTDIM_Z1, SHAPE_FIRSTDIM_X2, SHAPE_FIRSTDIM_Y2, SHAPE_FIRSTDIM_Z2),
            Block.box(SHAPE_SECONDDIM_X1, SHAPE_SECONDDIM_Y1, SHAPE_SECONDDIM_Z1, SHAPE_SECONDDIM_X2, SHAPE_SECONDDIM_Y2, SHAPE_SECONDDIM_Z2),
            Block.box(SHAPE_THIRDDIM_X1, SHAPE_THIRDDIM_Y1, SHAPE_THIRDDIM_Z1, SHAPE_THIRDDIM_X2, SHAPE_THIRDDIM_Y2, SHAPE_THIRDDIM_Z2)
        );

        private static final Map<Direction, VoxelShape> SHAPES = Util.make(
            new EnumMap<>(Direction.class), map -> {
                map.put(Direction.NORTH, NORTH_SHAPE);
                map.put(Direction.SOUTH, rotateShape(NORTH_SHAPE, 2));
                map.put(Direction.WEST,  rotateShape(NORTH_SHAPE, 3));
                map.put(Direction.EAST,  rotateShape(NORTH_SHAPE, 1));
            }
        );

        private static VoxelShape rotateShape(VoxelShape shape, int times)
        {
            VoxelShape[] b = new VoxelShape[]{shape, Shapes.empty()};
            for(int i = 0; i < times; i++)
            {
                b[0].forAllBoxes(
                    (minX, minY, minZ, maxX, maxY, maxZ) -> b[1] = Shapes.or(
                        b[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
                    )
                );
                b[0] = b[1];
                b[1] = Shapes.empty();
            }
            return b[0];
        }

        public static BlockBehaviour.Properties getDefaultBlockProperties()
        {
            return BlockBehaviour.Properties.of().
                strength(0.1f, 3.0f)
                .sound(SoundType.DEEPSLATE)
                .mapColor(MapColor.DEEPSLATE);
        }

        public static BlockBehaviour.Properties getProperties()
        {
            return null;
        }

        public RockBlock(Properties p)
        {
            super(p);
            this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        }
        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
        {
            b.add(FACING);
            return;
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext c)
        {
            Direction r = Direction.Plane.HORIZONTAL.getRandomDirection(c.getLevel().getRandom());
            return this.defaultBlockState().setValue(FACING, r);
        }
        @Override 
        public VoxelShape getShape(
            BlockState bs,
            BlockGetter bg,
            BlockPos bp,
            CollisionContext c
        )
        {
            if(ErosionConfig.SOMETHING_WENT_WRONG) return OLD_SHAPE_IF_SOMETHING_GOES_WRONG;
            return SHAPES.getOrDefault(bs.getValue(FACING), NORTH_SHAPE);
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

        @Override
        public void onPlace(
            BlockState s, Level l, BlockPos p, 
            BlockState o, boolean m
        )
        {
            if(!l.isClientSide && s.getBlock() == o.getBlock())
            {
                return;
            }

            if(!l.isClientSide && s.getValue(FACING) == Direction.NORTH && l.random.nextFloat() < 0.75f)
            { 
                Direction r = Direction.Plane.HORIZONTAL.getRandomDirection(l.random);
                l.setBlock(p, s.setValue(FACING, r), 2);
            }
        }
    }
}