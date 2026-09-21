package co.bracesoftware.erosion.world.blocks.chemical_reactor.module;

import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalReactorModuleBlock extends ErosionNetworkSafeBlock
implements IErosionChemicalReactorMultiBlockComponent
{
    public ChemicalReactorModuleBlock(Block.Properties p)
    {
        super(p);
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