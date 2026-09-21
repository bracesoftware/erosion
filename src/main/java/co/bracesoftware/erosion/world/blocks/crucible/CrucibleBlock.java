package co.bracesoftware.erosion.world.blocks.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.ErosionCore.CrucibleCatalyst;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBaseEntityBlock;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;
import com.mojang.serialization.MapCodec;

public class CrucibleBlock extends ErosionNetworkSafeBaseEntityBlock
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
        super(p.lightLevel(s -> s.getValue(WORKING) ? 10 : 0), ErosionRegistry.BlockEntities.CRUCIBLE.get());
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
    public boolean serverUseItemOn(
        ItemStack stack, BlockState state, ServerLevel level,
        BlockPos pos, ServerPlayer player, InteractionHand hand,
        BlockHitResult hitResult
    )
    {
        if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity be)
        {
            //if player is holding a catalyst item
            if(be.working)
            {
                ErosionUtils.displayMessage(
                    player, "This is hot!",
                    ErosionScreenMessage.Color.DARK_RED
                );
                player.hurt(player.damageSources().generic(), 1f);
            
                return true;
            }
            if(CrucibleCatalyst.isItemCrucibleCatalyst(stack.getItem()))
            {
                //if clickin with catalyst on a crucible with an item,error msg
                if(!be.storedItem.isEmpty())
                {
                    ErosionUtils.displayMessage(player, "Crucible must be empty before applying a catalyst");
                    return true;
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
                        level.playSound(
                            null,pos,
                            ErosionRegistry.SoundEvents.CRUCIBLE_MELTING.get(),
                            SoundSource.BLOCKS
                        );
                    }
                    
                    be.setChanged();
                    level.getLightEngine().checkBlock(pos);
                    level.setBlock(pos, state.setValue(FINISHED, be.finished).setValue(WORKING, be.working), Block.UPDATE_ALL);
                    ErosionUtils.displayMessage(player, "Applied " + be.catalyst.getItem().getName(be.catalyst).getString());
                }
                return true;
            }

            //if empty hand ...
            if(stack.isEmpty())
            {
                //if crucible is done, get the product
                if(!be.working && be.finished && !be.storedItem.isEmpty())
                {
                    ErosionUtils.displayMessage(
                        player, "You got " + be.storedItem.getItem().getDescription().getString(),
                        ErosionScreenMessage.Color.DARK_AQUA
                    );
                    player.getInventory().placeItemBackInInventory(be.storedItem);
                    be.storedItem = ItemStack.EMPTY;
                    be.setChanged();
                    level.getLightEngine().checkBlock(pos);
                    level.setBlock(pos, state.setValue(FINISHED, be.finished).setValue(WORKING, be.working), Block.UPDATE_ALL);

                    if(be.coproducts != null && !be.coproducts.isEmpty())
                    {
                        ErosionUtils.displayMessage(player, "Crucible dropped coproduct(s)");
                        for(var it : be.coproducts)
                        {
                            Containers.dropItemStack(
                                level, pos.getX(), pos.getY(), pos.getZ(), it.copy()
                            );
                        }
                        be.coproducts = null;
                    }
                    return true; //i want the hand anim bruv
                }
                //if crucible isn't working and is finished then take the catalyst out
                if(!be.working && be.finished && be.storedItem.isEmpty() && !be.catalyst.isEmpty())
                {
                    ItemStack fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                    be.catalyst = ItemStack.EMPTY;
                    ErosionUtils.displayMessage(player, "Catalyst taken out");
                
                    return true;
                }
            }
            
            //if holding a meltable item
            if(ErosionCore.BlockEntityRecipes.Crucible.getRecipes().containsKey(stack.getItem()))
            {
                if(be.catalyst.isEmpty())
                {
                    ErosionUtils.displayMessage(player, "A catalyst has to be applied first.");
                    return true;
                }
                var m = ErosionCore.BlockEntityRecipes.Crucible.getCatalysts();
                if(m.containsKey(stack.getItem()))
                {
                    List<Item> c = m.get(stack.getItem());
                    if(!c.contains(be.catalyst.getItem()))
                    {
                        ErosionUtils.displayMessage(player, "The material isn't eligible for the applied catalyst");
                        return true;
                    }
                }
                if(!be.working && be.finished && be.storedItem.isEmpty())
                {
                    be.finished = false;
                    be.working = true;
                    level.playSound(
                        null,pos,
                        ErosionRegistry.SoundEvents.CRUCIBLE_MELTING.get(),
                        SoundSource.BLOCKS
                    );

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
                
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected MapCodec<? extends ErosionNetworkSafeBaseEntityBlock> codec() {
        return simpleCodec(CrucibleBlock::new);
    }

    @Override
    public boolean canSurvive(BlockState s, LevelReader l, BlockPos p)
    {
        BlockPos bwp = p.below();
        BlockState bws = l.getBlockState(bwp);
        
        boolean isCampfire = bws.is(BlockTags.CAMPFIRES);
        boolean isLava = bws.getFluidState().is(FluidTags.LAVA) || bws.is(Blocks.LAVA);
        
        return isCampfire || isLava;
    }

    @Override
    public BlockState updateShape(
        BlockState s, Direction d, BlockState ns,
        LevelAccessor l, BlockPos bp, BlockPos np
    )
    {
        if(d == Direction.DOWN && !this.canSurvive(s, l, bp))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(s, d, ns, l, bp, np);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(!state.is(newState.getBlock()))
        {
            if(level.getBlockEntity(pos) instanceof CrucibleBlockEntity be)
            {    
                if(be.storedItem != null && !be.storedItem.isEmpty())
                {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.storedItem);
                }
                if(be.catalyst != null && !be.catalyst.isEmpty())
                {
                    ItemStack fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                }
                if(be.coproducts != null && !be.coproducts.isEmpty())
                {
                    for(ItemStack coproduct : be.coproducts)
                    {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), coproduct);
                    }
                }
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
        return;
    }
}