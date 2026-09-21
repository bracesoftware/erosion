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
            //module block can live anywhere
            if(this instanceof ChemicalReactorModuleBlock)
            {
                return true;
            }


            //if it is smth else, it has to have air on top
            if(!(this instanceof ChemicalReactorModuleBlock))
            {
                if(!(l.getBlockState(bp.relative(Direction.UP)).isAir()))
                {
                    return false;
                }
            }

            //since the top is air, we search only horizontally or under
            for(var d : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.WEST, Direction.EAST})
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