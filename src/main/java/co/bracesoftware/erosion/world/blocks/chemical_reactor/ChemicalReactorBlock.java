package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.ErosionExceptions.ErosionBlockExceptions.ErosionChemicalReactorException;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.erosion.ErosionUtils;

import java.util.HashSet;

import javax.annotation.Nullable;
import java.util.LinkedList;

import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBaseEntityBlock;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorMultiBlockComponent;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.cooling_system.ChemicalReactorCoolingSystemBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.module.ChemicalReactorModuleBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber.ChemicalReactorScrubberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalReactorBlock extends ErosionNetworkSafeBaseEntityBlock<ChemicalReactorBlock>
implements IErosionChemicalReactorMultiBlockComponent
{
    public ChemicalReactorBlock(Properties p)
    {
        super(p, () -> (
            BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>
        ) ErosionRegistry.BlockEntities.CHEMICAL_REACTOR.get(), ChemicalReactorBlock::new);
        this.setServerLogic(new ChemicalReactorBlockServerLogic());
    }

    @Nullable 
    @Override 
    public BlockEntity newBlockEntity(BlockPos p, BlockState s)
    {
        return new ChemicalReactorBlockEntity(p,s);
    }

    public static class ChemicalReactorBlockServerLogic extends ErosionNetworkSafeBlockSidedLogic
    {
        @Override public boolean useItemOn(ErosionBlockInteractionPacket p)
        {
            ErosionUtils.displayMessage(
                p.getServerPlayer(), "You must be empty-handed to use the reactor",
                ErosionScreenMessage.Color.RED
            );
            return true;
        }

        @Override public boolean useWithoutItem(ErosionBlockInteractionPacket p)
        {
            var pozz = p.getBlockPos().relative(Direction.UP);
            if(!p.getServerLevel().getBlockState(pozz).isAir())
            {
                ErosionUtils.displayMessage(
                    p.getServerPlayer(), "The top of the reactor is obstructed",
                    ErosionScreenMessage.Color.GRAY
                );
                return true;
            }
            p.getServerPlayer().openMenu(
                new SimpleMenuProvider(
                    (cid, pinv, pid) -> new ChemicalReactorMenu(cid, pinv, p.getBlockPos()),
                    Component.literal("Chemical Reactor")
                ), a -> a.writeBlockPos(p.getBlockPos())
            );
            return true;
        }
    }

    public static final class ChemicalReactorMultiBlockComponentPosPacket
    {
        public final boolean yes;
        public final long pos;

        public ChemicalReactorMultiBlockComponentPosPacket(boolean y, long p)
        {
            this.yes = y;
            this.pos = p;
        }

        public boolean no()
        {
            return this.yes ? false : true; //lets be extremely explicit, shall we?
        }
        public boolean yes()
        {
            return this.yes;
        }
    }

    public static ChemicalReactorMultiBlockComponentPosPacket getNearestChemicalReactorMultiBlockComponent(
        ServerLevel l, BlockPos crp, Class<? extends IErosionChemicalReactorMultiBlockComponent> c
    )
    {
        var xd = l.getBlockEntity(crp);
        if(xd instanceof ChemicalReactorBlockEntity e)
        {
            if(c == ChemicalReactorScrubberBlock.class && e.scrubberCached)
            {
                var pozz = BlockPos.of(e.cachedScrubberPos);
                var s = l.getBlockState(pozz);
                if(s.getBlock() instanceof ChemicalReactorScrubberBlock)
                {
                    int dur = s.getValue(ChemicalReactorScrubberBlock.FILTER_DURABILITY);
                    if(dur > 0) return new ChemicalReactorMultiBlockComponentPosPacket(true, pozz.asLong());
                }
            }
            else if(c == ChemicalReactorCoolingSystemBlock.class && e.coolingSysCached)
            {
                var pozz = BlockPos.of(e.cachedCoolingSystemPos);
                var s = l.getBlockState(pozz);
                if(s.getBlock() instanceof ChemicalReactorCoolingSystemBlock)
                {
                    int cf = s.getValue(ChemicalReactorCoolingSystemBlock.COOLING_FLUID_LEVEL);
                    if(cf > 0) return new ChemicalReactorMultiBlockComponentPosPacket(true, pozz.asLong());
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
                                e.scrubberCached = true;
                                return new ChemicalReactorMultiBlockComponentPosPacket(true, pozz.asLong());
                            }
                            continue;
                        }
                        else if(blok instanceof ChemicalReactorCoolingSystemBlock)
                        {
                            int cf = s.getValue(ChemicalReactorCoolingSystemBlock.COOLING_FLUID_LEVEL);
                            if(cf > 0)
                            {
                                e.cachedCoolingSystemPos = pozz.asLong();
                                e.coolingSysCached = true;
                                return new ChemicalReactorMultiBlockComponentPosPacket(true, pozz.asLong());
                            }
                            continue;
                        }
                        else
                        {
                            return new ChemicalReactorMultiBlockComponentPosPacket(true, pozz.asLong());
                        }
                    }
                    //but if not found and we are not searching for the module, we simply 
                    //go thru the module network for the target until we find it xD
                    else if(
                        blok instanceof ChemicalReactorModuleBlock ||
                        blok instanceof ChemicalReactorBlock
                    )
                    {
                        queue.add(pozz);
                    }
                }
            }
        }
        return new ChemicalReactorMultiBlockComponentPosPacket(false, 0);
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
                ErosionUtils.spawnGasParticle(l,p);
                return;
            }
            else throw new ErosionChemicalReactorException("Tried to damage a scrubber with no filter -> " + p);
        }
        else throw new ErosionChemicalReactorException("What are you doing? -> " + l + "::" + p);
    }
    public static void consumeSomeCoolingFluid(ServerLevel l, BlockPos p)
    {
        var s = l.getBlockState(p);
        if(s.getBlock() instanceof ChemicalReactorCoolingSystemBlock)
        {
            int f = s.getValue(ChemicalReactorCoolingSystemBlock.COOLING_FLUID_LEVEL);
            if(f > 0)
            {
                int mb = ErosionMod.RANDOM.nextInt(30);
                if(mb > f) mb = f;
                var ns = s.setValue(ChemicalReactorCoolingSystemBlock.COOLING_FLUID_LEVEL, f - mb);
                l.setBlockAndUpdate(p, ns);
                return;
            }
            else throw new ErosionChemicalReactorException("Tried to consume fluid but no fluid was present -> " + p);
        }
        else throw new ErosionChemicalReactorException("What are you doing now? -> " + l + "::" + p);
    }
}