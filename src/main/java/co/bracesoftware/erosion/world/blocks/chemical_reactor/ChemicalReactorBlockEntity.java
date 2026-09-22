package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.ErosionExceptions.ErosionException;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorSystemComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ChemicalReactorBlockEntity extends ErosionNetworkSafeBlockEntity<ChemicalReactorBlockEntity>
implements IErosionChemicalReactorSystemComponent
{
    public long cachedScrubberPos = 0;
    public long cachedCoolingSystemPos = 0;
    public static class DataRawName
    {
        public static final String CACHED_SCRUBBER_POS = "scrubber_cache";
        public static final String CACHED_COOLING_SYSTEM_POS = "coolin_sys_cache";
    }

    public ChemicalReactorBlockEntity(BlockPos pos, BlockState state)
    {
        super(ErosionRegistry.BlockEntities.CHEMICAL_REACTOR.get(), pos, state);
    }

    @Override public boolean onBlockEntityTickOnServer(
        ChemicalReactorBlockEntity e, ErosionBlockEntityTickPacket p
    ) throws ErosionException
    {
        return true;
    }

    //--------------------------------------------------------------------------
    @Override 
    protected void saveAdditional(CompoundTag t, HolderLookup.Provider r)
    {
        super.saveAdditional(t, r);
        t.putLong(DataRawName.CACHED_SCRUBBER_POS, this.cachedScrubberPos);
        t.putLong(DataRawName.CACHED_COOLING_SYSTEM_POS, this.cachedCoolingSystemPos);
        return;
    }

    @Override 
    public void loadAdditional(CompoundTag t, HolderLookup.Provider r)
    {
        super.loadAdditional(t, r);
        this.cachedScrubberPos = t.getLong(DataRawName.CACHED_SCRUBBER_POS);
        this.cachedCoolingSystemPos = t.getLong(DataRawName.CACHED_COOLING_SYSTEM_POS);
        return;
    }
}
