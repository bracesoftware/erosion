package co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber;

import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlock;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.IErosionBlockWithTip;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.module.ChemicalReactorModuleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ChemicalReactorScrubberBlock extends ErosionNetworkSafeBlock
implements IErosionBlockWithTip, IErosionChemicalReactorMultiBlockComponent
{
    public static final IntegerProperty FILTER_DURABILITY = IntegerProperty.create(
        "filter_durability", 0, 100
    );
    // ================================================== //
    @Override public void onBlockAimedOn(ServerPlayer p, BlockState s)
    {
        var ggwp = ErosionRegistry.RawRegistry.GAS_FILTER.getName();
        int d = s.getValue(FILTER_DURABILITY);
        if(d == 0) ErosionUtils.displayMessage(
            p, "Put a new " + ggwp + " into the scrubber!",
            ErosionScreenMessage.Color.DARK_RED
        );
        else
        {
            ErosionUtils.displayMessage(
                p, ggwp + " durability: " + d + "%",
                ErosionScreenMessage.Color.DARK_GREEN
            );
        }
        return;
    }

    public ChemicalReactorScrubberBlock(Block.Properties p)
    {
        super(p);
        this.registerDefaultState(
            this.stateDefinition.any().
            setValue(FILTER_DURABILITY,0)
        );
    }

    @Override public boolean serverUseItemOn(ErosionBlockInteractionPacket p)
    {
        var it = p.getItemStack().getItem();
        int dur = p.getBlockState().getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
        if(it == ErosionRegistry.Items.GAS_FILTER.get())
        {
            if(dur > 0)
            {
                ErosionUtils.displayMessage(
                    p.getServerPlayer(), "Filter in the scrubber is not yet worn out",
                    ErosionScreenMessage.Color.RED
                );
                return false;
            }
            p.getItemStack().shrink(1);
            var ns = p.getBlockState().setValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY, 100);
            p.getServerLevel().setBlock(p.getBlockPos(), ns, Block.UPDATE_ALL);
            return true;
        }
        return false;
    }
    // ================================================== //
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
    {
        b.add(FILTER_DURABILITY);
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
