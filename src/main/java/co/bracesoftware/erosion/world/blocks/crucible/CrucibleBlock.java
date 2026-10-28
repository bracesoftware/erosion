package co.bracesoftware.erosion.world.blocks.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleTypes;
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
import co.bracesoftware.erosion.ErosionCore.Alloy;
import co.bracesoftware.erosion.ErosionCore.CrucibleCatalyst;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBaseEntityBlock;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;

@SuppressWarnings("all")
public class CrucibleBlock extends ErosionNetworkSafeBaseEntityBlock<CrucibleBlock>
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

    private static final VoxelShape SHAPE_OLD = Shapes.or(
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

    public static final int CRUCIBLE_THICK = 2;
    public static final int CRUCIBLE_WIDTH = 10;
    public static final int CRUCIBLE_HEIGHT = 14;

    public static final VoxelShape SHAPE = Shapes.or(
        ErosionUtils.ModelRendering.createCenteredPipe(CRUCIBLE_HEIGHT, CRUCIBLE_WIDTH,CRUCIBLE_WIDTH, 0, CRUCIBLE_THICK),
        ErosionUtils.ModelRendering.createBoxCentered(1, CRUCIBLE_WIDTH,CRUCIBLE_WIDTH,0)
    );

    public static final VoxelShape SEALED_SHAPE = Shapes.or(
        ErosionUtils.ModelRendering.createBoxCentered(CRUCIBLE_HEIGHT,CRUCIBLE_WIDTH,CRUCIBLE_WIDTH,0),
        ErosionUtils.ModelRendering.createBoxCentered(1,CRUCIBLE_WIDTH + 2,CRUCIBLE_WIDTH + 2,CRUCIBLE_HEIGHT),
        ErosionUtils.ModelRendering.createBoxCentered(1,2,2,CRUCIBLE_HEIGHT + 1)
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
    public static final BooleanProperty SEALED = BooleanProperty.create(
        CrucibleBlockEntity.DataRawName.SEALED.toLowerCase()
    );

    public CrucibleBlock(Properties p)
    {
        super(p.lightLevel(s -> s.getValue(WORKING) ? 10 : 0),
            () -> (
                BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>
            ) ErosionRegistry.BlockEntities.CRUCIBLE.getBlockEntityHolder().get(),
            CrucibleBlock::new
        );
        this.registerDefaultState(
            this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(FINISHED, true)
            .setValue(WORKING, false)
            .setValue(HEAT, 0)
            .setValue(SEALED, false)
        );
        this.callUseItemOnOnly(true);
        this.setServerLogic(new CrucibleBlockServerLogic());
    }

    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
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
    public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c)
    {
        if(s.hasProperty(SEALED) && s.getValue(SEALED))
        {
            return SEALED_SHAPE;
        }
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
        b.add(FACING, FINISHED, WORKING, HEAT, SEALED);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c)
    {
        return this.defaultBlockState().setValue(FACING, c.getHorizontalDirection().getOpposite());
    }

    public static class CrucibleBlockServerLogic extends ErosionNetworkSafeBlockSidedLogic
    {
        @Override public void onInteractionFail(ErosionBlockInteractionPacket p)
        {
            ErosionUtils.displayMessage(
                p.getServerPlayer(), "Cannot do that",
                ErosionScreenMessage.Color.DARK_RED
            );
            return;
        }

        @Override public boolean useItemOn(ErosionBlockInteractionPacket p)
        {
            if(p.getServerLevel().getBlockEntity(p.getBlockPos()) instanceof CrucibleBlockEntity be)
            {
                //dameg d playr if he touchin red hot shi
                if(be.working)
                {
                    ErosionUtils.displayMessage(
                        p.getServerPlayer(), "This is hot!",
                        ErosionScreenMessage.Color.DARK_RED
                    );
                    p.getServerPlayer().hurt(p.getServerPlayer().damageSources().generic(), 1f);
                    return true;
                }
              
                //if the crucible is SEALED, completely different logik
                if(be.sealed)
                {
                    boolean ff = p.getItemStack().isEmpty();
                    if(ff && be.isEmpty())
                    {
                        be.sealed = false;
                        be.setChanged();
                        p.getServerLevel().getLightEngine().checkBlock(p.getBlockPos());
                        p.getServerLevel().setBlock(
                            p.getBlockPos(),
                            p.getBlockState()
                            .setValue(FINISHED, be.finished)
                            .setValue(WORKING, be.working)
                            .setValue(SEALED, be.sealed),
                            Block.UPDATE_ALL
                        );
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Lid removed",
                            ErosionScreenMessage.Color.GRAY
                        );
                        ErosionUtils.PlayerFuncs.giveItem(
                            p.getServerPlayer(),
                            ErosionRegistry.Items.CRUCIBLE_LID.get(),1
                        );
                        return true;
                    }

                    if(ff && !be.isAlloyComponentsEmpty() && !be.working)
                    {
                        for(var is : be.alloyComponents)
                        {
                            ErosionUtils.PlayerFuncs.giveItem(
                                p.getServerPlayer(), is
                            );
                        }
                        be.alloyComponents.clear();

                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Everything taken out",
                            ErosionScreenMessage.Color.GRAY
                        );
                        return true;
                    }

                    var it = p.getItemStack().getItem();
                    if(Alloy.canBeUsedForAlloying(it) && !be.working)
                    {
                        p.getItemStack().shrink(1);
                        be.alloyComponents.add(new ItemStack(it,1));

                        var i = Alloy.getAlloyItemFromComponents(be.alloyComponents);
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Added " + it.getDescription().getString()
                        );
                        if(i != null)
                        {
                            be.storedItem = new ItemStack(i,1);
                            be.working = true;
                            be.finished = false;
                            be.alloyComponents.clear();

                            p.getServerLevel().playSound(
                                null,p.getBlockPos(),
                                ErosionRegistry.SoundEvents.CRUCIBLE_MELTING.get(),
                                SoundSource.BLOCKS
                            );

                            be.setChanged();
                            p.getServerLevel().getLightEngine().checkBlock(p.getBlockPos());
                            p.getServerLevel().setBlock(
                                p.getBlockPos(),
                                p.getBlockState()
                                .setValue(FINISHED, be.finished)
                                .setValue(WORKING, be.working)
                                .setValue(SEALED, be.sealed),
                                Block.UPDATE_ALL
                            );
                        }
                        return true;
                    }

                    boolean done = !be.working && be.finished;
                    if(done && !be.storedItem.isEmpty())
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "You got " + be.storedItem.getItem().getDescription().getString(),
                            ErosionScreenMessage.Color.DARK_AQUA
                        );

                        ErosionUtils.PlayerFuncs.giveItem(
                            p.getServerPlayer(), be.storedItem.copy()
                        );
                        be.storedItem = ItemStack.EMPTY;
                        return true;
                    }

                    return false;
                }
                //if player is holding a catalyst item
                if(CrucibleCatalyst.isItemCrucibleCatalyst(p.getItemStack().getItem()))
                {
                    //if clickin with catalyst on a crucible with an item,error msg
                    if(!be.storedItem.isEmpty())
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Crucible must be empty before applying a catalyst",
                            ErosionScreenMessage.Color.RED
                        );
                        return true;
                    }
                    if(!be.catalyst.isEmpty() && be.catalyst.getItem() != p.getItemStack().getItem())
                    {
                        ItemStack fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                        Containers.dropItemStack(p.getServerLevel(), p.getBlockPos().getX(), p.getBlockPos().getY(), p.getBlockPos().getZ(), fuelStack);
                        be.catalyst = ItemStack.EMPTY;
                    }

                    if(be.catalyst.isEmpty())
                    {
                        be.catalyst = p.getItemStack().copyWithCount(1);
                        p.getItemStack().shrink(1);
                        
                        if(!be.finished && !be.working)
                        {
                            be.working = true;
                            p.getServerLevel().playSound(
                                null,p.getBlockPos(),
                                ErosionRegistry.SoundEvents.CRUCIBLE_MELTING.get(),
                                SoundSource.BLOCKS
                            );
                        }
                        
                        be.setChanged();
                        p.getServerLevel().getLightEngine().checkBlock(p.getBlockPos());
                        p.getServerLevel().setBlock(
                            p.getBlockPos(),
                            p.getBlockState()
                            .setValue(FINISHED, be.finished)
                            .setValue(WORKING, be.working), 
                            Block.UPDATE_ALL
                        );
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Applied " + be.catalyst.getItem().getName(be.catalyst).getString()
                        );
                    }
                    return true;
                }

                //if empty hand ...
                if(p.getItemStack().isEmpty())
                {
                    boolean logik = ((!be.working) && (be.finished));

                    //if bro's klikin for no reason, say it
                    if(be.isEmpty())
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "The crucible is completely empty",
                            ErosionScreenMessage.Color.GRAY
                        );
                        return true;
                    }
                    //if crucible is done, get the product
                    if(logik && !be.storedItem.isEmpty())
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "You got " + be.storedItem.getItem().getDescription().getString(),
                            ErosionScreenMessage.Color.DARK_AQUA
                        );
                        ErosionUtils.PlayerFuncs.giveItem(
                            p.getServerPlayer(), be.storedItem
                        );
                        be.storedItem = ItemStack.EMPTY;
                        be.setChanged();
                        p.getServerLevel().getLightEngine().checkBlock(p.getBlockPos());
                        p.getServerLevel().setBlock(
                            p.getBlockPos(),
                            p.getBlockState()
                            .setValue(FINISHED, be.finished)
                            .setValue(WORKING, be.working),
                            Block.UPDATE_ALL
                        );

                        if(be.coproducts != null && !be.coproducts.isEmpty())
                        {
                            ErosionUtils.displayMessage(p.getServerPlayer(), "Crucible dropped coproduct(s)");
                            for(var it : be.coproducts)
                            {
                                Containers.dropItemStack(
                                    p.getServerLevel(), p.getBlockPos().getX(), p.getBlockPos().getY(), p.getBlockPos().getZ(), it.copy()
                                );
                            }
                            be.coproducts = null;
                        }
                        return true; //i want the hand anim bruv
                    }
                    //if crucible isn't working and is finished then take the catalyst out
                    if(logik && be.storedItem.isEmpty() && !be.catalyst.isEmpty())
                    {
                        var fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                        Containers.dropItemStack(p.getServerLevel(), p.getBlockPos().getX(), p.getBlockPos().getY(), p.getBlockPos().getZ(), fuelStack);
                        be.catalyst = ItemStack.EMPTY;
                        ErosionUtils.displayMessage(p.getServerPlayer(), "Catalyst taken out");
                        return true;
                    }
                }

                if(be.isEmpty() && p.getItemStack().is(ErosionRegistry.Items.CRUCIBLE_LID.get()))
                {
                    be.sealed = true;
                    p.getItemStack().shrink(1);
                    ErosionUtils.displayMessage(
                        p.getServerPlayer(), "Lid on",
                        ErosionScreenMessage.Color.GRAY
                    );

                    be.setChanged();
                    p.getServerLevel().getLightEngine().checkBlock(p.getBlockPos());
                    p.getServerLevel().setBlock(
                        p.getBlockPos(),
                        p.getBlockState()
                        .setValue(FINISHED, be.finished)
                        .setValue(SEALED, be.sealed)
                        .setValue(WORKING, be.working),
                        Block.UPDATE_ALL
                    );
                    return true;
                }
                
                //if holding a meltable item
                if(ErosionCore.ErosionRecipeRegistry.Crucible.getRecipes().containsKey(p.getItemStack().getItem()))
                {
                    if(!be.storedItem.isEmpty())
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Crucible must be empty before trying to melt anything",
                            ErosionScreenMessage.Color.RED
                        );
                        return true;
                    }
                    if(be.catalyst.isEmpty())
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "A catalyst has to be applied first",
                            ErosionScreenMessage.Color.YELLOW
                        );
                        return true;
                    }
                    var m = ErosionCore.ErosionRecipeRegistry.Crucible.getCatalysts();
                    if(m.containsKey(p.getItemStack().getItem()))
                    {
                        List<Item> c = m.get(p.getItemStack().getItem());
                        if(!c.contains(be.catalyst.getItem()))
                        {
                            ErosionUtils.displayMessage(
                                p.getServerPlayer(), "The material isn't eligible for the applied catalyst",
                                ErosionScreenMessage.Color.YELLOW
                            );
                            return true;
                        }
                    }
                    if(!be.working && be.finished && be.storedItem.isEmpty())
                    {
                        be.finished = false;
                        be.working = true;
                        p.getServerLevel().playSound(
                            null,p.getBlockPos(),
                            ErosionRegistry.SoundEvents.CRUCIBLE_MELTING.get(),
                            SoundSource.BLOCKS
                        );

                        be.storedItem = p.getItemStack().copyWithCount(1);
                        p.getItemStack().shrink(1);

                        be.setChanged();
                        p.getServerLevel().getLightEngine().checkBlock(p.getBlockPos());
                        p.getServerLevel().setBlock(
                            p.getBlockPos(),
                            p.getBlockState()
                            .setValue(FINISHED, be.finished)
                            .setValue(WORKING, be.working),
                            Block.UPDATE_ALL
                        );
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "Melting " + be.storedItem.getItem().getName(be.storedItem).getString()
                        );
                    
                        return true;
                    }
                }
            }

            return false;
        }

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
                if(!be.sealed) if(be.storedItem != null && !be.storedItem.isEmpty())
                {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), be.storedItem);
                }
                if(be.catalyst != null && !be.catalyst.isEmpty())
                {
                    var fuelStack = new ItemStack(be.catalyst.getItem(), 1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), fuelStack);
                }
                if(be.coproducts != null && !be.coproducts.isEmpty())
                {
                    for(var c : be.coproducts)
                    {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), c);
                    }
                }
                if(be.sealed)
                {
                    Containers.dropItemStack(
                        level,pos.getX(), pos.getY(), pos.getZ(),
                        new ItemStack(ErosionRegistry.Items.CRUCIBLE_LID.get(),1)
                    );
                }
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
        return;
    }
}