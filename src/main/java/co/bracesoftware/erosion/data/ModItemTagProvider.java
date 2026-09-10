package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, 
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Erosion.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        //SIMPLE ITEMS
        createSimplePowder(provider, ErosionRegistry.Items.FELDSPAR_POWDER.get());
        createSimplePowder(provider, ErosionRegistry.Items.FLUX.get());
        createSimplePowder(provider, ErosionRegistry.Items.SULFUR_SLAG.get());
        createSimplePowder(provider, ErosionRegistry.Items.DEBRIS.get());
        createSimplePowder(provider, ErosionRegistry.Items.CRUSHED_EGG_SHELL.get());

        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_HEMATITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_LIMONITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_MAGNETITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_MALACHITE.get());

        createSimpleRawOre(provider, ErosionRegistry.Items.NATIVE_GOLD.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_CASSITERITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.TIN_CHUNK.get());

        createSimpleRawOre(provider, ErosionRegistry.Items.SILVER_CHUNK.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.NATIVE_SILVER.get());

        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_BISMUTHINITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.BISMUTH_CHUNK.get());

        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_SPHALERITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.ZINC_CHUNK.get());

        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_AZURITE.get());
        createSimpleRawOre(provider, ErosionRegistry.Items.RAW_TETRAHEDRITE.get());

        return;
    }

    public void createSimpleRawOre(HolderLookup.Provider p, Item i)
    {
        tag(Tags.Items.ORES).add(i);
    }

    public void createSimplePowder(HolderLookup.Provider p, Item i)
    {
        tag(Tags.Items.DUSTS).add(i);
    }
}