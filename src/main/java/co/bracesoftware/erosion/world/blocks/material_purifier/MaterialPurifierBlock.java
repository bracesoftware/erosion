package co.bracesoftware.erosion.world.blocks.material_purifier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

import org.jetbrains.annotations.Nullable;

import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBaseEntityBlock;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;

public class MaterialPurifierBlock extends ErosionNetworkSafeBaseEntityBlock<MaterialPurifierBlock>
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
        super(p,() -> (
            BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>
        ) ErosionRegistry.BlockEntities.MATERIAL_PURIFIER.get(), MaterialPurifierBlock::new);
        this.registerDefaultState(
            this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(FUEL, 0)
            .setValue(FINISHED, true)
            .setValue(WORKING, false)
        );
        this.callUseItemOnOnly(true);
        this.setServerLogic(new MaterialPurifierBlockServerLogic());
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

    @Nullable 
    @Override 
    public BlockEntity newBlockEntity(BlockPos p, BlockState s)
    {
        return new MaterialPurifierBlockEntity(p,s);
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
    
    public static class MaterialPurifierBlockServerLogic extends ErosionNetworkSafeBlockSidedLogic
    {
        @Override public boolean useItemOn(ErosionBlockInteractionPacket p)
        {
            if(p.getServerLevel().getBlockEntity(p.getBlockPos()) instanceof MaterialPurifierBlockEntity be)
            {
                if(p.getItemStack().is(Items.REDSTONE))
                {
                    if(be.fuel == ErosionConfig.MAX_PURIFIER_FUEL)
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Fuel tank is full (3/3)"
                        );
                        return true;
                    }
                    if(be.fuel < ErosionConfig.MAX_PURIFIER_FUEL)
                    {
                        be.fuel++;
                        p.getItemStack().shrink(1);
                        
                        if(!be.finished && !be.working && be.fuel > 0)
                        {
                            be.fuel--;
                            be.working = true;
                        }
                        
                        be.setChanged();
                        p.getServerLevel().setBlock(
                            p.getBlockPos(), 
                            p.getBlockState()
                            .setValue(FUEL, be.fuel)
                            .setValue(FINISHED, be.finished)
                            .setValue(WORKING, be.working), 
                            Block.UPDATE_ALL
                        );
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Fuel level: " + be.fuel + "/" + ErosionConfig.MAX_PURIFIER_FUEL
                        );
                        return true;
                    }
                }

                if(p.getItemStack().isEmpty()) if(!be.working && be.finished && !be.storedItem.isEmpty())
                {
                    p.getServerPlayer().getInventory().placeItemBackInInventory(be.storedItem);
                    be.storedItem = ItemStack.EMPTY;
                    be.setChanged();
                    p.getServerLevel().setBlock(
                        p.getBlockPos(),
                        p.getBlockState()
                        .setValue(FUEL, be.fuel)
                        .setValue(FINISHED, be.finished)
                        .setValue(WORKING, be.working),
                        Block.UPDATE_ALL
                    );
                    return true;
                }
            }

            return false;
        }

        @Override public void onInteractionFail(ErosionBlockInteractionPacket p)
        {
            ErosionUtils.displayMessage(
                p.getServerPlayer(), "Cannot do that",
                ErosionScreenMessage.Color.DARK_RED
            );
            return;
        }
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