package co.bracesoftware.erosion.blocks.crucible;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionCore.CrucibleCatalyst;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import com.mojang.serialization.MapCodec;

public class CrucibleBlock extends BaseEntityBlock
{
    public static final int MIN_XZ = 4;
    public static final int MAX_XZ = 12;
    
    public static final int INNER_MIN_XZ = 5;
    public static final int INNER_MAX_XZ = 11;

    public static final int RIM_MIN_XZ = 6;
    public static final int RIM_MAX_XZ = 10;

    public static final int Y_BOTTOM_FROM = 0;
    public static final int Y_BOTTOM_TO = 2;

    public static final int Y_WALLS_FROM = 2;
    public static final int Y_WALLS_TO = 14;

    public static final int Y_RIM_FROM = 14;
    public static final int Y_RIM_TO = 16;

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(MIN_XZ, Y_BOTTOM_FROM, MIN_XZ, MAX_XZ, Y_BOTTOM_TO, MAX_XZ), 
        Block.box(MIN_XZ, Y_WALLS_FROM, MIN_XZ, MAX_XZ, Y_WALLS_TO, INNER_MIN_XZ),
        Block.box(MIN_XZ, Y_WALLS_FROM, INNER_MAX_XZ, MAX_XZ, Y_WALLS_TO, MAX_XZ),       
        Block.box(MIN_XZ, Y_WALLS_FROM, INNER_MIN_XZ, INNER_MIN_XZ, Y_WALLS_TO, INNER_MAX_XZ),
        Block.box(INNER_MAX_XZ, Y_WALLS_FROM, INNER_MIN_XZ, MAX_XZ, Y_WALLS_TO, INNER_MAX_XZ),
        Block.box(MIN_XZ, Y_RIM_FROM, MIN_XZ, MAX_XZ, Y_RIM_TO, RIM_MIN_XZ),              
        Block.box(MIN_XZ, Y_RIM_FROM, RIM_MAX_XZ, MAX_XZ, Y_RIM_TO, MAX_XZ),           
        Block.box(MIN_XZ, Y_RIM_FROM, RIM_MIN_XZ, RIM_MIN_XZ, Y_RIM_TO, RIM_MAX_XZ), 
        Block.box(RIM_MAX_XZ, Y_RIM_FROM, RIM_MIN_XZ, MAX_XZ, Y_RIM_TO, RIM_MAX_XZ)
    );

    public static final IntegerProperty HEAT = IntegerProperty.create(
        CrucibleBlockEntity.DataRawName.HEAT, 0, ErosionConfig.CRUCIBLE_SECONDS
    );
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final BooleanProperty FINISHED = BooleanProperty.create(
        CrucibleBlockEntity.DataRawName.FINISHED.toLowerCase()
    );
    public static final BooleanProperty WORKING = BooleanProperty.create(
        CrucibleBlockEntity.DataRawName.WORKING.toLowerCase()
    );

    public CrucibleBlock(Properties p)
    {
        super(p.lightLevel(s -> s.getValue(WORKING) ? 10 : 0));
        this.registerDefaultState(
            this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(FINISHED, true)
            .setValue(WORKING, false)
            .setValue(HEAT, 0)
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        boolean f = state.getValue(FINISHED);
        boolean w = state.getValue(WORKING);
        
        if(!f && w)
        {
            if(random.nextDouble() < 0.3D)
            {
                double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
                double y = pos.getY() + 1.05D;
                double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;

                double xSpeed = 0.0D;
                double ySpeed = 0.04D;
                double zSpeed = 0.0D;

                level.addParticle(
                    ParticleTypes.LAVA, 
                    x, y, z, 
                    xSpeed, ySpeed, zSpeed
                );
            }
        }
        return;
    }

    @Override 
    public RenderShape getRenderShape(BlockState s)
    {
        return RenderShape.MODEL;
    }

    @Override 
    public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c)
    {
        return SHAPE;
    }

    @Nullable 
    @Override 
    public BlockEntity newBlockEntity(BlockPos p, BlockState s)
    {
        return new CrucibleBlockEntity(p,s);
    }

    @Nullable 
    @Override 
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState s, BlockEntityType<T> type)
    {
        return level.isClientSide() ? null : createTickerHelper(
            type, ErosionRegistry.BlockEntities.CRUCIBLE.get(), CrucibleBlockEntity::tick
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
    {
        //super.createBlockStateDefinition(b);
        b.add(FACING, FINISHED, WORKING, HEAT);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c)
    {
        return this.defaultBlockState().setValue(FACING, c.getHorizontalDirection().getOpposite());
    }
    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack, BlockState state, Level level,
        BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult hitResult
    )
    {
        if(level.isClientSide())
        {
            return ItemInteractionResult.SUCCESS;
        }

        if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity be)
        {
            //if player is holding a catalyst item
            if(CrucibleCatalyst.isItemCrucibleCatalyst(stack.getItem()))
            {
                //if clickin with catalyst on a crucible with an item,error msg
                if(!be.storedItem.isEmpty())
                {
                    ErosionUtils.displayMessage(player, "Crucible must be empty before applying a catalyst");
                    return ItemInteractionResult.CONSUME;
                }
                if(!be.catalyst.isEmpty() && be.catalyst.getItem() != stack.getItem())
                {
                    ItemStack fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                    be.catalyst = ItemStack.EMPTY;
                }

                if(be.catalyst.isEmpty())
                {
                    be.catalyst = stack.copyWithCount(1);
                    stack.shrink(1);
                    
                    if(!be.finished && !be.working)
                    {
                        be.working = true;
                    }
                    
                    be.setChanged();
                    level.getLightEngine().checkBlock(pos);
                    level.setBlock(pos, state.setValue(FINISHED, be.finished).setValue(WORKING, be.working), Block.UPDATE_ALL);
                    ErosionUtils.displayMessage(player, "Applied " + be.catalyst.getItem().getName(be.catalyst).getString());
                    return ItemInteractionResult.CONSUME;
                }
            }

            //if empty hand ...
            if(stack.isEmpty())
            {
                //if crucible is done, get the product
                if(!be.working && be.finished && !be.storedItem.isEmpty())
                {
                    player.getInventory().placeItemBackInInventory(be.storedItem);
                    var f = ErosionCore.BlockEntityRecipes.Crucible.COPRODUCTS;
                    if(f.containsKey(be.storedItem.getItem()))
                    {
                        for(var it : f.get(be.storedItem.getItem()))
                        {
                            if(ErosionUtils.Misc.randomWithChanceToBe(true, be.lastChance))
                            {
                                ItemStack s = new ItemStack(it, 1);
                                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), s);
                                ErosionUtils.displayMessage(player, "Crucible dropped coproduct(s)");
                            }
                        }
                    }
                    be.storedItem = ItemStack.EMPTY;
                    be.setChanged();
                    level.getLightEngine().checkBlock(pos);
                    level.setBlock(pos, state.setValue(FINISHED, be.finished).setValue(WORKING, be.working), Block.UPDATE_ALL);
                    return ItemInteractionResult.CONSUME;
                }
                //if crucible isn't working and is finished then take the catalyst out
                if(!be.working && be.finished && be.storedItem.isEmpty() && !be.catalyst.isEmpty())
                {
                    ItemStack fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                    be.catalyst = ItemStack.EMPTY;
                    ErosionUtils.displayMessage(player, "Catalyst taken out");
                    return ItemInteractionResult.CONSUME;
                }
            }
            
            //if holding a meltable item
            if(ErosionCore.BlockEntityRecipes.Crucible.RECIPES.containsKey(stack.getItem()))
            {
                if(be.catalyst.isEmpty())
                {
                    ErosionUtils.displayMessage(player, "A catalyst has to be applied first.");
                    return ItemInteractionResult.CONSUME;
                }
                var m = ErosionCore.BlockEntityRecipes.Crucible.CATALYSTS;
                if(m.containsKey(stack.getItem()))
                {
                    List<Item> c = m.get(stack.getItem());
                    if(!c.contains(be.catalyst.getItem()))
                    {
                        ErosionUtils.displayMessage(player, "The material isn't eligible for the applied catalyst");
                        return ItemInteractionResult.CONSUME;
                    }
                }
                if(!be.working && be.finished && be.storedItem.isEmpty())
                {
                    be.finished = false;
                    be.working = true;

                    be.storedItem = stack.copyWithCount(1);
                    stack.shrink(1);

                    be.setChanged();
                    level.getLightEngine().checkBlock(pos);
                    level.setBlock(
                        pos,
                        state.setValue(FINISHED, be.finished).
                        setValue(WORKING, be.working),
                        Block.UPDATE_ALL
                    );
                    ErosionUtils.displayMessage(player, "Melting " + be.storedItem.getItem().getName(be.storedItem).getString());
                    return ItemInteractionResult.CONSUME;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrucibleBlock::new);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        
        boolean isCampfire = belowState.is(BlockTags.CAMPFIRES);
        boolean isLava = belowState.getFluidState().is(FluidTags.LAVA) || belowState.is(Blocks.LAVA);
        
        return isCampfire || isLava;
    }

    @Override
    public BlockState updateShape(
        BlockState state, Direction direction, BlockState neighborState,
        LevelAccessor level, BlockPos currentPos, BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN && !this.canSurvive(state, level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(!state.is(newState.getBlock()))
        {
            if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity be)
            {    
                if(!be.storedItem.isEmpty())
                {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.storedItem);
                }
                if(!be.catalyst.isEmpty())
                {
                    ItemStack fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                }
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
        return;
    }
}