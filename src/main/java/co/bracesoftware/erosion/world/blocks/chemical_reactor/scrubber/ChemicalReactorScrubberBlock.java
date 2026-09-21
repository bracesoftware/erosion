package co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber;

import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.ErosionExceptions.ErosionBlockExceptions.ErosionChemicalReactorException;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.IErosionBlockWithTip;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ChemicalReactorScrubberBlock extends Block implements IErosionBlockWithTip, IErosionChemicalReactorMultiBlockComponent
{
    public static final IntegerProperty FILTER_DURABILITY = IntegerProperty.create(
        "filter_durability", 0, 100
    );

    @Override 
    public void onBlockAimedOn(ServerPlayer p, BlockState s)
    {
        int d = s.getValue(FILTER_DURABILITY);
        if(d == 0) ErosionUtils.displayMessage(
            p, "Put a new filter into the scrubber!",
            ErosionScreenMessage.Color.DARK_RED
        );
        else
        {
            ErosionUtils.displayMessage(
                p, "Filter durability: " + d + "%",
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
    {
        b.add(FILTER_DURABILITY);
        return;
    }

    @Override
    public boolean canSurvive(BlockState s, LevelReader l, BlockPos p)
    {
        for(var d : Direction.Plane.HORIZONTAL)
        {
            var pos = p.relative(d);
            var state = l.getBlockState(pos);

            if(state.getBlock() instanceof ChemicalReactorBlock)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState updateShape(
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
