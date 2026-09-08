package co.bracesoftware.erosion.blocks.material_purifier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import com.mojang.serialization.MapCodec;

public class MaterialPurifierBlock extends BaseEntityBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty FUEL = IntegerProperty.create(
        MaterialPurifierBlockEntity.DataRawName.FUEL.toLowerCase(), 0, ErosionConfig.MAX_PURIFIER_FUEL
    );
    public static final BooleanProperty FINISHED = BooleanProperty.create(
        MaterialPurifierBlockEntity.DataRawName.FINISHED.toLowerCase()
    );
    public static final BooleanProperty WORKING = BooleanProperty.create(
        MaterialPurifierBlockEntity.DataRawName.WORKING.toLowerCase()
    );

    public MaterialPurifierBlock(Properties p)
    {
        super(p);
        this.registerDefaultState(
            this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(FUEL, 0)
            .setValue(FINISHED, true)
            .setValue(WORKING, false)
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
                    ParticleTypes.CAMPFIRE_COSY_SMOKE, 
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

    @Nullable 
    @Override 
    public BlockEntity newBlockEntity(BlockPos p, BlockState s)
    {
        return new MaterialPurifierBlockEntity(p,s);
    }

    @Nullable 
    @Override 
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState s, BlockEntityType<T> type)
    {
        return level.isClientSide() ? null : createTickerHelper(
            type, ErosionRegistry.BlockEntities.MATERIAL_PURIFIER.get(), MaterialPurifierBlockEntity::tick
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
    {
        //super.createBlockStateDefinition(b);
        b.add(FACING, FUEL, FINISHED, WORKING);
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

        if(level.getBlockEntity(pos) instanceof MaterialPurifierBlockEntity be)
        {
            if(stack.is(Items.REDSTONE) && be.fuel < ErosionConfig.MAX_PURIFIER_FUEL)
            {
                be.fuel++;
                stack.shrink(1);
                
                if(!be.finished && !be.working && be.fuel > 0)
                {
                    be.fuel--;
                    be.working = true;
                }
                
                be.setChanged();
                level.setBlock(pos, state.setValue(FUEL, be.fuel).setValue(FINISHED, be.finished).setValue(WORKING, be.working), Block.UPDATE_ALL);
                ErosionUtils.displayMessage(player, "Fuel level: " + be.fuel + "/" + ErosionConfig.MAX_PURIFIER_FUEL);
                return ItemInteractionResult.CONSUME;
            }

            if(stack.isEmpty()) if(!be.working && be.finished && !be.storedItem.isEmpty())
            {
                player.getInventory().placeItemBackInInventory(be.storedItem);
                be.storedItem = ItemStack.EMPTY;
                be.setChanged();
                level.setBlock(pos, state.setValue(FUEL, be.fuel).setValue(FINISHED, be.finished).setValue(WORKING, be.working), Block.UPDATE_ALL);
                return ItemInteractionResult.CONSUME;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(MaterialPurifierBlock::new);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if(!state.is(newState.getBlock()))
        {
            if(level.getBlockEntity(pos) instanceof MaterialPurifierBlockEntity be)
            {    
                if(!be.storedItem.isEmpty())
                {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.storedItem);
                }
                if(be.fuel > 0)
                {
                    ItemStack fuelStack = new ItemStack(Items.REDSTONE, be.fuel);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                }
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
        return;
    }
}