package co.bracesoftware.erosion.data.clientgen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.world.ErosionRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ErosionLang extends LanguageProvider 
{
    public ErosionLang(PackOutput output) 
    {
        super(output, Erosion.MODID, "en_us");
    }

    @Override
    protected void addTranslations() 
    {
        add(ErosionConfig.CREATIVE_TAB_ID, Erosion.MODNAME);
        
        //SIMPLE ITEMS
        add(ErosionRegistry.Items.FLUX.get(), ErosionRegistry.RawRegistry.FLUX.getName());
        add(ErosionRegistry.Items.SALT.get(), ErosionRegistry.RawRegistry.SALT.getName());
        add(ErosionRegistry.Items.GAS_FILTER.get(), ErosionRegistry.RawRegistry.GAS_FILTER.getName());
        add(ErosionRegistry.Items.RUBY.get(), ErosionRegistry.RawRegistry.RUBY.getName());
        add(ErosionRegistry.Items.BORAX.get(), ErosionRegistry.RawRegistry.BORAX.getName());
        add(ErosionRegistry.Items.DEHYDRATED_BORAX.get(), ErosionRegistry.RawRegistry.DEHYDRATED_BORAX.getName());
        add(ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get(), ErosionRegistry.RawRegistry.BORIC_ACID_CRYSTAL.getName());
        add(ErosionRegistry.Items.BUCKET_OF_SULFURIC_ACID.get(), ErosionRegistry.RawRegistry.BUCKET_OF_SULFURIC_ACID.getName());
        add(ErosionRegistry.Items.SAPPHIRE.get(), ErosionRegistry.RawRegistry.SAPPHIRE.getName());
        add(ErosionRegistry.Items.SULFUR_SLAG.get(), ErosionRegistry.RawRegistry.SULFUR_SLAG.getName());
        add(ErosionRegistry.Items.ANTIMONY_SLAG.get(), ErosionRegistry.RawRegistry.ANTIMONY_SLAG.getName());
        add(ErosionRegistry.Items.DEBRIS.get(), ErosionRegistry.RawRegistry.DEBRIS.getName());
        add(ErosionRegistry.Items.CRUSHED_EGG_SHELL.get(), ErosionRegistry.RawRegistry.CRUSHED_EGG_SHELL.getName());
        add(ErosionRegistry.Items.FELDSPAR_POWDER.get(), ErosionRegistry.RawRegistry.FELDSPAR_POWDER.getName());
        add(ErosionRegistry.Items.BASIC_MASK.get(), ErosionRegistry.RawRegistry.BASIC_MASK.getName());
        add(ErosionRegistry.Items.GAS_MASK.get(), ErosionRegistry.RawRegistry.GAS_MASK.getName());

        add(ErosionRegistry.Items.RAW_LIMONITE.get(), ErosionRegistry.RawRegistry.RAW_LIMONITE.getName());
        add(ErosionRegistry.Items.RAW_HEMATITE.get(), ErosionRegistry.RawRegistry.RAW_HEMATITE.getName());
        add(ErosionRegistry.Items.RAW_MALACHITE.get(), ErosionRegistry.RawRegistry.RAW_MALACHITE.getName());
        add(ErosionRegistry.Items.RAW_MAGNETITE.get(), ErosionRegistry.RawRegistry.RAW_MAGNETITE.getName());

        add(ErosionRegistry.Items.NATIVE_GOLD.get(), ErosionRegistry.RawRegistry.NATIVE_GOLD.getName());
        add(ErosionRegistry.Items.TIN_CHUNK.get(), ErosionRegistry.RawRegistry.TIN_CHUNK.getName());
        add(ErosionRegistry.Items.LEAD_CHUNK.get(), ErosionRegistry.RawRegistry.LEAD_CHUNK.getName());
        add(ErosionRegistry.Items.RAW_CASSITERITE.get(), ErosionRegistry.RawRegistry.RAW_CASSITERITE.getName());

        add(ErosionRegistry.Items.SILVER_CHUNK.get(), ErosionRegistry.RawRegistry.SILVER_CHUNK.getName());
        add(ErosionRegistry.Items.NATIVE_SILVER.get(), ErosionRegistry.RawRegistry.NATIVE_SILVER.getName());

        add(ErosionRegistry.Items.BISMUTH_CHUNK.get(), ErosionRegistry.RawRegistry.BISMUTH_CHUNK.getName());
        add(ErosionRegistry.Items.RAW_BISMUTHINITE.get(), ErosionRegistry.RawRegistry.RAW_BISMUTHINITE.getName());

        add(ErosionRegistry.Items.ZINC_CHUNK.get(), ErosionRegistry.RawRegistry.ZINC_CHUNK.getName());
        add(ErosionRegistry.Items.RAW_SPHALERITE.get(), ErosionRegistry.RawRegistry.RAW_SPHALERITE.getName());
        add(ErosionRegistry.Items.RAW_AZURITE.get(), ErosionRegistry.RawRegistry.RAW_AZURITE.getName());
        add(ErosionRegistry.Items.RAW_GOETHITE.get(), ErosionRegistry.RawRegistry.RAW_GOETHITE.getName());
        add(ErosionRegistry.Items.RAW_TETRAHEDRITE.get(), ErosionRegistry.RawRegistry.RAW_TETRAHEDRITE.getName());
        add(ErosionRegistry.Items.RAW_ARSENOPYRITE.get(), ErosionRegistry.RawRegistry.RAW_ARSENOPYRITE.getName());
        add(ErosionRegistry.Items.RAW_PYRITE.get(), ErosionRegistry.RawRegistry.RAW_PYRITE.getName());
        add(ErosionRegistry.Items.RAW_ANGLESITE.get(), ErosionRegistry.RawRegistry.RAW_ANGLESITE.getName());
        add(ErosionRegistry.Items.RAW_GALENA.get(), ErosionRegistry.RawRegistry.RAW_GALENA.getName());
        add(ErosionRegistry.Items.RAW_HALITE.get(), ErosionRegistry.RawRegistry.RAW_HALITE.getName());

        //SIMPLE BLOCKS
        add(ErosionRegistry.Blocks.DRIED_DIRT.get(), ErosionRegistry.RawRegistry.DRIED_DIRT.getName());
        add(ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get(), ErosionRegistry.RawRegistry.MINERAL_RICH_DIRT.getName());

        add(ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get(), ErosionRegistry.RawRegistry.KAOLINIZED_GRANITE.getName());
        add(ErosionRegistry.Blocks.CRACKED_STONE.get(), ErosionRegistry.RawRegistry.CRACKED_STONE.getName());
        add(ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(), ErosionRegistry.RawRegistry.QUARTZ_GRAVEL.getName());
        add(ErosionRegistry.Blocks.ALBITIZED_GRANITE.get(), ErosionRegistry.RawRegistry.ALBITIZED_GRANITE.getName());
        add(ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get(), ErosionRegistry.RawRegistry.PROPYLITIZED_DIORITE.getName());
        add(ErosionRegistry.Blocks.CRACKED_CALCITE.get(), ErosionRegistry.RawRegistry.CRACKED_CALCITE.getName());

        add(ErosionRegistry.Blocks.MAGNETITE_ORE.get(), ErosionRegistry.RawRegistry.MAGNETITE_ORE.getName());
        add(ErosionRegistry.Blocks.LIMONITE_ORE.get(), ErosionRegistry.RawRegistry.LIMONITE_ORE.getName());
        add(ErosionRegistry.Blocks.HEMATITE_ORE.get(), ErosionRegistry.RawRegistry.HEMATITE_ORE.getName());
        add(ErosionRegistry.Blocks.BORAX_DEPOSIT.get(), ErosionRegistry.RawRegistry.BORAX_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get(), ErosionRegistry.RawRegistry.CALCITE_MALACHITE_ORE.getName());

        add(ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(), ErosionRegistry.RawRegistry.NATIVE_GOLD_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(), ErosionRegistry.RawRegistry.CASSITERITE_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(), ErosionRegistry.RawRegistry.NATIVE_SILVER_DEPOSIT.getName());
        add(ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(), ErosionRegistry.RawRegistry.BISMUTHINITE_ORE.getName());
        add(ErosionRegistry.Blocks.SPHALERITE_ORE.get(), ErosionRegistry.RawRegistry.SPHALERITE_ORE.getName());
        add(ErosionRegistry.Blocks.AZURITE_ORE.get(), ErosionRegistry.RawRegistry.AZURITE_ORE.getName());
        add(ErosionRegistry.Blocks.GOETHITE_ORE.get(), ErosionRegistry.RawRegistry.GOETHITE_ORE.getName());
        add(ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(), ErosionRegistry.RawRegistry.TETRAHEDRITE_ORE.getName());
        add(ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get(), ErosionRegistry.RawRegistry.ARSENOPYRITE_ORE.getName());
        add(ErosionRegistry.Blocks.PYRITE_ORE.get(), ErosionRegistry.RawRegistry.PYRITE_ORE.getName());
        add(ErosionRegistry.Blocks.ANGLESITE_ORE.get(), ErosionRegistry.RawRegistry.ANGLESITE_ORE.getName());
        add(ErosionRegistry.Blocks.HALITE_ORE.get(), ErosionRegistry.RawRegistry.HALITE_ORE.getName());
        add(ErosionRegistry.Blocks.GALENA_ORE.get(), ErosionRegistry.RawRegistry.GALENA_ORE.getName());
        add(ErosionRegistry.Blocks.RUBY_ORE.get(), ErosionRegistry.RawRegistry.RUBY_ORE.getName());
        add(ErosionRegistry.Blocks.SAPPHIRE_ORE.get(), ErosionRegistry.RawRegistry.SAPPHIRE_ORE.getName());

        //MACHINES
        add(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get(), ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getName());
        add(ErosionRegistry.Blocks.CRUCIBLE.get(), ErosionRegistry.RawRegistry.CRUCIBLE.getName());
        add(ErosionRegistry.Blocks.CHEMICAL_REACTOR.get(), ErosionRegistry.RawRegistry.CHEMICAL_REACTOR.getName());
        add(ErosionRegistry.Blocks.CHEMICAL_REACTOR_SCRUBBER.get(), ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_SCRUBBER.getName());
        add(ErosionRegistry.Blocks.CHEMICAL_REACTOR_COOLING_SYSTEM.get(), ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getName());
        add(ErosionRegistry.Blocks.CHEMICAL_REACTOR_MODULE.get(), ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getName());
        return;
    }
}