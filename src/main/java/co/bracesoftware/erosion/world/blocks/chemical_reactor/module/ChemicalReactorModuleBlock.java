package co.bracesoftware.erosion.world.blocks.chemical_reactor.module;

import java.util.HashSet;
import java.util.LinkedList;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlock;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.IErosionBlockWithTip;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.cooling_system.ChemicalReactorCoolingSystemBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber.ChemicalReactorScrubberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalReactorModuleBlock extends ErosionNetworkSafeBlock
implements IErosionChemicalReactorMultiBlockComponent, IErosionBlockWithTip
{
    public ChemicalReactorModuleBlock(Block.Properties p)
    {
        super(p);
        this.setServerLogic(new ChemicalReactorModuleBlockServerLogic());
        this.setCommonLogic(new ChemicalReactorModuleBlockCommonLogic());
    }

    //--------------------------------------------------
    @Override public void onBlockAimedOn(ServerPlayer p, BlockState bs, BlockPos crp)
    {
        //we wanna know how many reactors are connected to the multiblock system
        //use same mechanism for searching as i did in findNearestReactorComponent or whatever it is called xd
        int reactorsFound = 0;
        int scrubbersFound = 0;
        int coolingSystemsFound = 0;
        var visited = new HashSet<BlockPos>();
        var queue = new LinkedList<BlockPos>();

        visited.add(crp);
        queue.add(crp);

        while(!(queue.isEmpty()))
        {
            var currentpos = queue.poll();
            for(var d : Direction.values())
            {
                var pozz = currentpos.relative(d);
                if(visited.contains(pozz)) continue;
                visited.add(pozz);
                var s = p.level().getBlockState(pozz);
                var blok = s.getBlock();
                //if it is a functional multiblock component,increase the counter
                if(blok instanceof ChemicalReactorBlock)
                {
                    reactorsFound++;
                    queue.add(pozz);//since a reactor contains same wiring and pipery as the modul
                }
                else if(blok instanceof ChemicalReactorScrubberBlock)
                {
                    scrubbersFound++;
                }
                else if(blok instanceof ChemicalReactorCoolingSystemBlock)
                {
                    coolingSystemsFound++;
                }
                //else we search
                else if(
                    blok instanceof ChemicalReactorModuleBlock
                )
                {
                    queue.add(pozz);
                }
            }
        }

        if(reactorsFound == 0) ErosionUtils.displayMessage(
            p, "No reactors are connected",
            ErosionScreenMessage.Color.DARK_RED
        );
        else ErosionUtils.displayMessage(
            p, reactorsFound + " reactor(s) connected",
            ErosionScreenMessage.Color.DARK_GREEN
        );

        if(scrubbersFound == 0) ErosionUtils.displayMessage(
            p, "No scrubbers are connected",
            ErosionScreenMessage.Color.DARK_RED
        );
        else ErosionUtils.displayMessage(
            p, scrubbersFound + " scrubber(s) connected",
            ErosionScreenMessage.Color.DARK_GREEN
        );

        if(coolingSystemsFound == 0) ErosionUtils.displayMessage(
            p, "No cooling systems are connected",
            ErosionScreenMessage.Color.DARK_RED
        );
        else ErosionUtils.displayMessage(
            p, coolingSystemsFound + " cooling system(s) connected",
            ErosionScreenMessage.Color.DARK_GREEN
        );

        return;
    }

    public static class ChemicalReactorModuleBlockServerLogic extends ErosionNetworkSafeBlockSidedLogic
    {
        @Override public void onInteractionFail(ErosionBlockInteractionPacket p)
        {
            ErosionUtils.displayMessage(
                p.getServerPlayer(), "Cannot do that",
                ErosionScreenMessage.Color.DARK_RED
            );
            return;
        }
    }

    public static class ChemicalReactorModuleBlockCommonLogic extends ErosionNetworkSafeBlockSidedLogic
    {
        @Override public boolean onAttemptToPlaceBlock(ErosionBlockInteractionPacket p)
        {
            if(p.getBlockClassInfo() instanceof ChemicalReactorModuleBlock)
            {
                return true;
            }
            if(p.getBlockClassInfo() instanceof ChemicalReactorBlock)
            {
                return true;
            }
            if(p.getBlockClassInfo() instanceof ChemicalReactorCoolingSystemBlock)
            {
                return true;
            }
            if(p.getBlockClassInfo() instanceof ChemicalReactorScrubberBlock)
            {
                return true;
            }
            return false;
        }
    }
    //--------------------------------------------------

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