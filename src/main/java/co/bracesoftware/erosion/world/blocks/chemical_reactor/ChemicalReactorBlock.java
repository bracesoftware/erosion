package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber.ChemicalReactorScrubberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChemicalReactorBlock extends ErosionNetworkSafeBlock implements IErosionChemicalReactorMultiBlockComponent
{
    public ChemicalReactorBlock(Properties p)
    {
        super(p);
    }

    @Override public boolean serverUseItemOn(ErosionBlockInteractionPacket p)
    {
        ErosionUtils.displayMessage(
            p.getServerPlayer(), "You must be empty-handed to use the reactor",
            ErosionScreenMessage.Color.RED
        );
        return true;
    }

    @Override public boolean serverUseWithoutItem(ErosionBlockInteractionPacket p)
    {
        p.getServerPlayer().openMenu(
            new SimpleMenuProvider(
                (cid, pinv, pid) -> new ChemicalReactorMenu(cid, pinv, p.getBlockPos()),
                Component.literal("Chemical Reactor")
            ), a -> a.writeBlockPos(p.getBlockPos())
        );
        return true;
    }

    public static boolean isFunctionalScrubberPresent(ServerLevel l, BlockPos p)
    {
        for(var d : Direction.Plane.HORIZONTAL)
        {
            var s = l.getBlockState(p.relative(d));
            if(s.getBlock() instanceof ChemicalReactorScrubberBlock b)
            {
                int filter = s.getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
                if(filter > 0) return true;
            }
        }
        return false;
    }
    //use after checked with function above xD
    public static void damageScrubberFilter(ServerLevel l, BlockPos p)
    {
        for(var d : Direction.Plane.HORIZONTAL)
        {
            var scrubberPosition = p.relative(d);
            var s = l.getBlockState(scrubberPosition);
            if(s.getBlock() instanceof ChemicalReactorScrubberBlock b)
            {
                int filter = s.getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
                if(filter > 0)
                {
                    var ns = s.setValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY, filter - 1);
                    l.setBlockAndUpdate(scrubberPosition, ns);
                    return;
                }
            }
        }
        return;
    }
}