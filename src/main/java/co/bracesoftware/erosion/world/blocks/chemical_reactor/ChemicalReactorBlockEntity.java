package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.ErosionExceptions.ErosionException;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorSystemComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalReactorBlockEntity extends ErosionNetworkSafeBlockEntity<ChemicalReactorBlockEntity>
implements IErosionChemicalReactorSystemComponent
{
    public boolean scrubberCached = false;
    public boolean coolingSysCached = false;
    public long cachedScrubberPos = 0;
    public long cachedCoolingSystemPos = 0;
    public static class DataRawName
    {
        public static final String CACHED_SCRUBBER_POS = "scrubber_cache";
        public static final String CACHED_COOLING_SYSTEM_POS = "coolin_sys_cache";
    }

    public ChemicalReactorBlockEntity(BlockPos pos, BlockState state)
    {
        super(ErosionRegistry.BlockEntities.CHEMICAL_REACTOR.getBlockEntityHolder().get(), pos, state);
    }

    @Override public boolean onBlockEntityTickOnServer(
        ChemicalReactorBlockEntity e, ErosionBlockEntityTickPacket p
    ) throws ErosionException
    {
        //every 5 minutes we reset cache flags
        if(e.getEntityAgeInTicks() % 6000 == 0)
        {
            this.scrubberCached = false;
            this.coolingSysCached = false;
        }
        return true;
    }
}
