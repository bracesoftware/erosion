package co.bracesoftware.erosion.world.blocks.chemical_reactor.cooling_system;

import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.ErosionCore.BlockEntityRecipes;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlock;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.IErosionBlockWithTip;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ChemicalReactorCoolingSystemBlock extends ErosionNetworkSafeBlock
implements IErosionBlockWithTip, IErosionChemicalReactorMultiBlockComponent
{
    public static final IntegerProperty COOLING_FLUID_LEVEL = IntegerProperty.create(
        "coolin_fluid_leva", 0, 1000
    );
    // ================================================== //
    @Override public void onBlockAimedOn(ServerPlayer p, BlockState s, BlockPos pos)
    {
        int d = s.getValue(COOLING_FLUID_LEVEL);
        if(d == 0) ErosionUtils.displayMessage(
            p, "Fill the system with a cooling fluid",
            ErosionScreenMessage.Color.DARK_RED
        );
        else
        {
            ErosionUtils.displayMessage(
                p, "Cooling fluid level: " + d + "mB",
                ErosionScreenMessage.Color.DARK_GREEN
            );
        }
        return;
    }

    public ChemicalReactorCoolingSystemBlock(Block.Properties p)
    {
        super(p);
        this.registerDefaultState(
            this.stateDefinition.any().
            setValue(COOLING_FLUID_LEVEL,0)
        );
        this.setServerLogic(new ChemicalReactorCoolingSystemBlockServerLogic());
    }

    public static class ChemicalReactorCoolingSystemBlockServerLogic extends ErosionNetworkSafeBlockSidedLogic
    {
        @Override public boolean useItemOn(ErosionBlockInteractionPacket p)
        {
            var holdingItem = p.getItemStack().getItem();
            int lev = p.getBlockState().getValue(ChemicalReactorCoolingSystemBlock.COOLING_FLUID_LEVEL);
            for(var item : BlockEntityRecipes.ChemicalReactor.getChemicalReactorCoolingLiquids().entrySet())
            {
                var coolingItem = item.getKey();
                var giveBack = item.getValue();
                if(holdingItem == coolingItem)
                {
                    if(lev > 0)
                    {
                        ErosionUtils.displayMessage(
                            p.getServerPlayer(), "There is still enough fluid in the system",
                            ErosionScreenMessage.Color.RED
                        );
                        return true;
                    }
                    p.getItemStack().shrink(1);
                    var ns = p.getBlockState().setValue(ChemicalReactorCoolingSystemBlock.COOLING_FLUID_LEVEL, 1000);
                    p.getServerLevel().setBlock(p.getBlockPos(), ns, Block.UPDATE_ALL);
                    p.getServerPlayer().getInventory().placeItemBackInInventory(new ItemStack(giveBack, 1));
                    ErosionUtils.displayMessage(
                        p.getServerPlayer(), "Fluid successfully applied"
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
    // ================================================== //
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
    {
        b.add(COOLING_FLUID_LEVEL);
        return;
    }

    @Override public boolean canSurvive(BlockState s, LevelReader l, BlockPos p)
    {
        return this.isInMultiBlockSystem(l, p);
    }

    @Override public BlockState updateShape(
        BlockState s, Direction d, BlockState ns,
        LevelAccessor l, BlockPos bp, BlockPos np
    )
    {
        if(!this.canSurvive(s, l, bp))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(s, d, ns, l, bp, np);
    }
}
