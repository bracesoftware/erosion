package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Erosion.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        basicItem(ErosionRegistry.Items.FELDSPAR_POWDER.get());
        basicItem(ErosionRegistry.Items.FLUX.get());
        basicItem(ErosionRegistry.Items.SULFUR_SLAG.get());
        basicItem(ErosionRegistry.Items.DEBRIS.get());
        basicItem(ErosionRegistry.Items.CRUSHED_EGG_SHELL.get());

        basicItem(ErosionRegistry.Items.RAW_HEMATITE.get());
        basicItem(ErosionRegistry.Items.RAW_LIMONITE.get());
        basicItem(ErosionRegistry.Items.RAW_MAGNETITE.get());
        basicItem(ErosionRegistry.Items.RAW_MALACHITE.get());

        basicItem(ErosionRegistry.Items.NATIVE_GOLD.get());
        basicItem(ErosionRegistry.Items.RAW_CASSITERITE.get());
        basicItem(ErosionRegistry.Items.TIN_CHUNK.get());
        
        basicItem(ErosionRegistry.Items.SILVER_CHUNK.get());
        basicItem(ErosionRegistry.Items.NATIVE_SILVER.get());

        basicItem(ErosionRegistry.Items.RAW_BISMUTHINITE.get());
        basicItem(ErosionRegistry.Items.BISMUTH_CHUNK.get());

        basicItem(ErosionRegistry.Items.RAW_SPHALERITE.get());
        basicItem(ErosionRegistry.Items.ZINC_CHUNK.get());

        basicItem(ErosionRegistry.Items.RAW_AZURITE.get());
        basicItem(ErosionRegistry.Items.RAW_TETRAHEDRITE.get());

        return;
    }
}