package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.world.blocks.chemical_reactor.module.ChemicalReactorModuleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;

public class ChemicalReactorSystemCore
{
    public static interface IErosionChemicalReactorSystemComponent {}
    public static interface IErosionChemicalReactorMultiBlockComponent
    {
        default public boolean isInMultiBlockSystem(LevelReader l, BlockPos bp)
        {
            if(!(this instanceof ChemicalReactorModuleBlock))
            {
                if(!(l.getBlockState(bp.relative(Direction.UP)).isAir()))
                {
                    return false;
                }
            }

            var dd = (this instanceof ChemicalReactorModuleBlock)
            ? Direction.values()
            : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
            
            for(var d : dd)
            {
                var pos = bp.relative(d);
                var state = l.getBlockState(pos);

                if(state.getBlock() instanceof ChemicalReactorBlock)
                {
                    return true;
                }
                if(state.getBlock() instanceof ChemicalReactorModuleBlock)
                {
                    return true;
                }
            }
            return false;
        }
    }
    public static interface IErosionChemicalReactorItem {}
}