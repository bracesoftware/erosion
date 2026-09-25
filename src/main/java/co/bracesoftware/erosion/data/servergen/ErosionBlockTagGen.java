package co.bracesoftware.erosion.data.servergen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ErosionBlockTagGen extends BlockTagsProvider implements ErosionTags.ErosionTaggable<Block>
{
    public ErosionBlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) 
    {
        super(output, lookupProvider, Erosion.MODID, existingFileHelper);
    }

    public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> tagz(TagKey<Block> e)
    {
        return this.tag(e);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) 
    {
        //SIMPLE BLOCKS
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleGravel(this, provider, ErosionRegistry.Blocks.DRIED_DIRT.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleStone(this, provider, ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleStone(this, provider, ErosionRegistry.Blocks.CRACKED_STONE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleGravel(this, provider, ErosionRegistry.Blocks.QUARTZ_GRAVEL.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleStone(this, provider, ErosionRegistry.Blocks.ALBITIZED_GRANITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleStone(this, provider, ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleStone(this, provider, ErosionRegistry.Blocks.CRACKED_CALCITE.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.MAGNETITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.HEMATITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.BORAX_DEPOSIT.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.LIMONITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.BISMUTHINITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.SPHALERITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.AZURITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.GOETHITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.RUBY_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(this, provider, ErosionRegistry.Blocks.SAPPHIRE_ORE.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(
            this,provider,ErosionRegistry.Blocks.PYRITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(
            this,provider,ErosionRegistry.Blocks.ANGLESITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleOre(
            this,provider,ErosionRegistry.Blocks.GALENA_ORE.get()
        );

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleGravel(this, provider, ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleGravel(this, provider, ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleGravel(this, provider, ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleDirt(this, provider, ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_LIMONITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_HEMATITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_MAGNETITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_MALACHITE.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.NATIVE_GOLD.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.NATIVE_SILVER.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_BISMUTHINITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_CASSITERITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_SPHALERITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_AZURITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(this, provider, ErosionRegistry.Blocks.RAW_ARSENOPYRITE.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(
            this,provider,ErosionRegistry.Blocks.RAW_PYRITE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(
            this,provider,ErosionRegistry.Blocks.RAW_ANGLESITE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(
            this,provider,ErosionRegistry.Blocks.RAW_GALENA.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleRock(
            this,provider,ErosionRegistry.Blocks.RAW_GOETHITE.get()
        );
        //MACHINES
        // ============================================= //
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleMachine(
            this,provider,ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleMachine(
            this,provider,ErosionRegistry.Blocks.CRUCIBLE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleMachine(
            this,provider,ErosionRegistry.Blocks.CHEMICAL_REACTOR.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleMachine(
            this,provider,ErosionRegistry.Blocks.CHEMICAL_REACTOR_SCRUBBER.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleMachine(
            this,provider,ErosionRegistry.Blocks.CHEMICAL_REACTOR_MODULE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Blocks.createSimpleMachine(
            this,provider,ErosionRegistry.Blocks.CHEMICAL_REACTOR_COOLING_SYSTEM.get()
        );
        return;
    }
}