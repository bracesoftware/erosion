package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider 
{
    public ModLanguageProvider(PackOutput output) 
    {
        super(output, Erosion.MODID, "en_us");
    }

    @Override
    protected void addTranslations() 
    {
        add(ErosionConfig.CREATIVE_TAB_ID, Erosion.MODNAME);
        
        //SIMPLE ITEMS
        add(ErosionRegistry.Items.FLUX.get(), ErosionRegistry.RawRegistry.FLUX.getName());
        add(ErosionRegistry.Items.SULFUR_SLAG.get(), ErosionRegistry.RawRegistry.SULFUR_SLAG.getName());
        add(ErosionRegistry.Items.DEBRIS.get(), ErosionRegistry.RawRegistry.DEBRIS.getName());
        add(ErosionRegistry.Items.CRUSHED_EGG_SHELL.get(), ErosionRegistry.RawRegistry.CRUSHED_EGG_SHELL.getName());
        add(ErosionRegistry.Items.FELDSPAR_POWDER.get(), ErosionRegistry.RawRegistry.FELDSPAR_POWDER.getName());

        add(ErosionRegistry.Items.RAW_LIMONITE.get(), ErosionRegistry.RawRegistry.RAW_LIMONITE.getName());
        add(ErosionRegistry.Items.RAW_HEMATITE.get(), ErosionRegistry.RawRegistry.RAW_HEMATITE.getName());
        add(ErosionRegistry.Items.RAW_MALACHITE.get(), ErosionRegistry.RawRegistry.RAW_MALACHITE.getName());
        add(ErosionRegistry.Items.RAW_MAGNETITE.get(), ErosionRegistry.RawRegistry.RAW_MAGNETITE.getName());

        add(ErosionRegistry.Items.NATIVE_GOLD.get(), ErosionRegistry.RawRegistry.NATIVE_GOLD.getName());
        add(ErosionRegistry.Items.TIN_CHUNK.get(), ErosionRegistry.RawRegistry.TIN_CHUNK.getName());
        add(ErosionRegistry.Items.RAW_CASSITERITE.get(), ErosionRegistry.RawRegistry.RAW_CASSITERITE.getName());

        add(ErosionRegistry.Items.SILVER_CHUNK.get(), ErosionRegistry.RawRegistry.SILVER_CHUNK.getName());
        add(ErosionRegistry.Items.NATIVE_SILVER.get(), ErosionRegistry.RawRegistry.NATIVE_SILVER.getName());

        add(ErosionRegistry.Items.BISMUTH_CHUNK.get(), ErosionRegistry.RawRegistry.BISMUTH_CHUNK.getName());
        add(ErosionRegistry.Items.RAW_BISMUTHINITE.get(), ErosionRegistry.RawRegistry.RAW_BISMUTHINITE.getName());

        add(ErosionRegistry.Items.ZINC_CHUNK.get(), ErosionRegistry.RawRegistry.ZINC_CHUNK.getName());
        add(ErosionRegistry.Items.RAW_SPHALERITE.get(), ErosionRegistry.RawRegistry.RAW_SPHALERITE.getName());
        add(ErosionRegistry.Items.RAW_AZURITE.get(), ErosionRegistry.RawRegistry.RAW_AZURITE.getName());
        add(ErosionRegistry.Items.RAW_TETRAHEDRITE.get(), ErosionRegistry.RawRegistry.RAW_TETRAHEDRITE.getName());

        //SIMPLE BLOCKS
        add(ErosionRegistry.Blocks.DRIED_DIRT.get(), ErosionRegistry.RawRegistry.DRIED_DIRT.getName());
        add(ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get(), ErosionRegistry.RawRegistry.MINERAL_RICH_DIRT.getName());

        add(ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get(), ErosionRegistry.RawRegistry.KAOLINIZED_GRANITE.getName());
        add(ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(), ErosionRegistry.RawRegistry.QUARTZ_GRAVEL.getName());
        add(ErosionRegistry.Blocks.ALBITIZED_GRANITE.get(), ErosionRegistry.RawRegistry.ALBITIZED_GRANITE.getName());
        add(ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get(), ErosionRegistry.RawRegistry.PROPYLITIZED_DIORITE.getName());
        add(ErosionRegistry.Blocks.CRACKED_CALCITE.get(), ErosionRegistry.RawRegistry.CRACKED_CALCITE.getName());

        add(ErosionRegistry.Blocks.MAGNETITE_ORE.get(), ErosionRegistry.RawRegistry.MAGNETITE_ORE.getName());
        add(ErosionRegistry.Blocks.LIMONITE_ORE.get(), ErosionRegistry.RawRegistry.LIMONITE_ORE.getName());
        add(ErosionRegistry.Blocks.HEMATITE_ORE.get(), ErosionRegistry.RawRegistry.HEMATITE_ORE.getName());
        add(ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get(), ErosionRegistry.RawRegistry.CALCITE_MALACHITE_ORE.getName());

        add(ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(), ErosionRegistry.RawRegistry.NATIVE_GOLD_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(), ErosionRegistry.RawRegistry.CASSITERITE_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(), ErosionRegistry.RawRegistry.NATIVE_SILVER_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(), ErosionRegistry.RawRegistry.BISMUTHINITE_ORE.getName());
        add(ErosionRegistry.Blocks.SPHALERITE_ORE.get(), ErosionRegistry.RawRegistry.SPHALERITE_ORE.getName());
        add(ErosionRegistry.Blocks.AZURITE_ORE.get(), ErosionRegistry.RawRegistry.AZURITE_ORE.getName());
        add(ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(), ErosionRegistry.RawRegistry.TETRAHEDRITE_ORE.getName());

        //MACHINES
        add(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get(), ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getName());
        add(ErosionRegistry.Blocks.CRUCIBLE.get(), ErosionRegistry.RawRegistry.CRUCIBLE.getName());
    }
}