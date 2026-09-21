package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.ErosionExceptions;
import co.bracesoftware.erosion.ErosionExceptions.ErosionBlockExceptions.ErosionChemicalReactorException;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionUtils.ErosionPair;

import java.util.HashSet;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBaseEntityBlock;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlock;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.module.ChemicalReactorModuleBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber.ChemicalReactorScrubberBlock;
import co.bracesoftware.erosion.world.blocks.crucible.CrucibleBlock;
import co.bracesoftware.erosion.world.blocks.crucible.CrucibleBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChemicalReactorBlock extends ErosionNetworkSafeBaseEntityBlock<ChemicalReactorBlock>
implements IErosionChemicalReactorMultiBlockComponent
{
    public ChemicalReactorBlock(Properties p)
    {
        super(p, () -> (
            BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>
        ) ErosionRegistry.BlockEntities.CHEMICAL_REACTOR.get(), ChemicalReactorBlock::new);
    }

    @Nullable 
    @Override 
    public BlockEntity newBlockEntity(BlockPos p, BlockState s)
    {
        return new ChemicalReactorBlockEntity(p,s);
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

    public static ErosionPair<Boolean, BlockPos> getNearestChemicalReactorMultiBlockComponent(
        ServerLevel l, BlockPos crp, Class<? extends IErosionChemicalReactorMultiBlockComponent> c
    )
    {
        var xd = l.getBlockEntity(crp);
        if(xd instanceof ChemicalReactorBlockEntity e)
        {
            if(c == ChemicalReactorScrubberBlock.class)
            {
                var pozz = BlockPos.of(e.cachedScrubberPos);
                var s = l.getBlockState(pozz);
                if(s.getBlock() instanceof ChemicalReactorScrubberBlock)
                {
                    int dur = s.getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
                    if(dur > 0) return new ErosionPair<>(true, pozz);
                }
            }

            var visited = new HashSet<BlockPos>();
            var queue = new LinkedList<BlockPos>();

            visited.add(crp);
            queue.add(crp);

            while(!(queue.isEmpty()))
            {
                var currentpos = queue.poll();
                var lmao = (l.getBlockState(currentpos).getBlock() instanceof ChemicalReactorModuleBlock)
                ? Direction.values()
                : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
                for(var d : lmao)
                {
                    var pozz = currentpos.relative(d);
                    if(visited.contains(pozz)) continue;
                    visited.add(pozz);
                    var s = l.getBlockState(pozz);
                    var blok = s.getBlock();
                    if(c.isInstance(blok)) //we firstly search for whatever we looking for
                    {
                        if(blok instanceof ChemicalReactorScrubberBlock)
                        {
                            int dur = s.getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
                            if(dur > 0)
                            {
                                e.cachedScrubberPos = pozz.asLong();
                                return new ErosionPair<>(true, pozz);
                            }
                            continue;
                        }
                        else
                        {
                            return new ErosionPair<>(true, pozz);
                        }
                    }
                    //but if not found and we are not searching for the module, we simply 
                    //recursively go for the target until we find it xD
                    else if(blok instanceof ChemicalReactorModuleBlock)
                    {
                        queue.add(pozz);
                    }
                }
            }
        }
        return new ErosionPair<>(false, null);
    }
    public static ChemicalReactorBlockEntity getChemicalReactorEntity(ServerLevel l, BlockPos pos)
    {
        BlockEntity be = l.getBlockEntity(pos);
        if(be instanceof ChemicalReactorBlockEntity re)
        {
            return re;
        }
        return null;
    }
    //use after checked with function above xD
    public static void damageScrubberFilter(ServerLevel l, BlockPos p)
    {
        var s = l.getBlockState(p);
        if(s.getBlock() instanceof ChemicalReactorScrubberBlock)
        {
            int filter = s.getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
            if(filter > 0)
            {
                var ns = s.setValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY, filter - 1);
                l.setBlockAndUpdate(p, ns);
                return;
            }
            else throw new ErosionChemicalReactorException("Tried to damage a scrubber with no filter -> " + p);
        }
        return;
    }
}