package co.bracesoftware.erosion.data.servergen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ErosionItemTagGen extends ItemTagsProvider implements ErosionTags.ErosionTaggable<Item>
{
    public ErosionItemTagGen(
        PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, 
        CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper
    )
    {
        super(output, lookupProvider, blockTags, Erosion.MODID, existingFileHelper);
    }

    public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tagz(TagKey<Item> e)
    {
        return this.tag(e);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        //SIMPLE ITEMS
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(this, provider, ErosionRegistry.Items.FELDSPAR_POWDER.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(this, provider, ErosionRegistry.Items.FLUX.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleItem(this, provider, ErosionRegistry.Items.GAS_FILTER.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RUBY.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.BORAX.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(this, provider, ErosionRegistry.Items.DEHYDRATED_BORAX.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.SAPPHIRE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(
            this, provider, ErosionRegistry.Items.SULFUR_SLAG.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(
            this, provider, ErosionRegistry.Items.ANTIMONY_SLAG.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(this, provider, ErosionRegistry.Items.DEBRIS.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimplePowder(this, provider, ErosionRegistry.Items.CRUSHED_EGG_SHELL.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_HEMATITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_LIMONITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_MAGNETITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_MALACHITE.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.NATIVE_GOLD.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_CASSITERITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.TIN_CHUNK.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.SILVER_CHUNK.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.NATIVE_SILVER.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_BISMUTHINITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.BISMUTH_CHUNK.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_SPHALERITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.ZINC_CHUNK.get());

        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_AZURITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_TETRAHEDRITE.get());
        ErosionDataGeneratorsProgInterface.ErosionTags.Items.createSimpleRawOre(this, provider, ErosionRegistry.Items.RAW_ARSENOPYRITE.get());

        ErosionTags.Items.createSimpleRawOre(
            this,provider,ErosionRegistry.Items.RAW_PYRITE.get()
        );

        ErosionTags.Items.createSimpleArmorPiece(
            this,provider,ErosionRegistry.Items.GAS_MASK.get()
        );
        ErosionTags.Items.createSimpleArmorPiece(
            this,provider,ErosionRegistry.Items.BASIC_MASK.get()
        );

        return;
    }
}