package co.bracesoftware.erosion.data.clientgen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.world.ErosionModContentManager;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ErosionBlockStateGen extends BlockStateProvider 
{
    public ErosionBlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) 
    {
        super(output, Erosion.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        //AUTOMATIZACIO
        for(var r : ErosionModContentManager.EROSION_BLOCK_STATE_GEN_TASKS)
        {
            r.run();
        }

        //SIMPLE BLOCKS
        //RANDOMIZED ROTATION
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.CRACKED_STONE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.DRIED_DIRT.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.QUARTZ_GRAVEL.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.ALBITIZED_GRANITE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get());

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.CRACKED_CALCITE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.MAGNETITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.HEMATITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.LIMONITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.BORAX_DEPOSIT.get());

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get());

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.BISMUTHINITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.AZURITE_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.GOETHITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get()
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.PYRITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.ANGLESITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.GALENA_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.HALITE_ORE.get()
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(this, ErosionRegistry.Blocks.RUBY_ORE.get());
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.SAPPHIRE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.SPHALERITE_ORE.get()
        );

        //MACHINES
        // ============================================= //
        //ModelFile[][][] purifierModels = new ModelFile[ErosionConfig.MAX_PURIFIER_FUEL + 1][2][];

        // ============================================= //
        
        ////////////////////////////////////////////////////////////

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_LIMONITE.get(),
            ErosionRegistry.Blocks.RAW_LIMONITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_LIMONITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.LIMONITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_HEMATITE.get(),
            ErosionRegistry.Blocks.RAW_HEMATITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_HEMATITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.HEMATITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_MAGNETITE.get(),
            ErosionRegistry.Blocks.RAW_MAGNETITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_MAGNETITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.MAGNETITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_MALACHITE.get(),
            ErosionRegistry.Blocks.RAW_MALACHITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_MALACHITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.CALCITE_MALACHITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.NATIVE_GOLD.get(),
            ErosionRegistry.Blocks.NATIVE_GOLD.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.NATIVE_GOLD.getId(), //item for texture
                ErosionRegistry.RawRegistry.NATIVE_GOLD_DEPOSIT.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.NATIVE_SILVER.get(),
            ErosionRegistry.Blocks.NATIVE_SILVER.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.NATIVE_SILVER.getId(), //item for texture
                ErosionRegistry.RawRegistry.NATIVE_SILVER_DEPOSIT.getId() //block texture
            )
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_CASSITERITE.get(),
            ErosionRegistry.Blocks.RAW_CASSITERITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_CASSITERITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.CASSITERITE_DEPOSIT.getId() //block texture
            )
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
            ErosionRegistry.Blocks.RAW_BISMUTHINITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_BISMUTHINITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.BISMUTHINITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_SPHALERITE.get(),
            ErosionRegistry.Blocks.RAW_SPHALERITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_SPHALERITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.SPHALERITE_ORE.getId() //block texture
            )
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_AZURITE.get(),
            ErosionRegistry.Blocks.RAW_AZURITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_AZURITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.AZURITE_ORE.getId() //block texture
            )
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_GOETHITE.get(),
            ErosionRegistry.Blocks.RAW_GOETHITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_GOETHITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.GOETHITE_ORE.getId() //block texture
            )
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_TETRAHEDRITE.get(),
            ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_TETRAHEDRITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.TETRAHEDRITE_ORE.getId() //block texture
            )
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_ARSENOPYRITE.get(),
            ErosionRegistry.Blocks.RAW_ARSENOPYRITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_ARSENOPYRITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.ARSENOPYRITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_PYRITE.get(),
            ErosionRegistry.Blocks.RAW_PYRITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_PYRITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.PYRITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_ANGLESITE.get(),
            ErosionRegistry.Blocks.RAW_ANGLESITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_ANGLESITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.ANGLESITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_HALITE.get(),
            ErosionRegistry.Blocks.RAW_HALITE.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_HALITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.HALITE_ORE.getId() //block texture
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRockWithRandomRotations(
            this,
            ErosionRegistry.Items.RAW_GALENA.get(),
            ErosionRegistry.Blocks.RAW_GALENA.get(),
            ErosionDataGeneratorsProgInterface.ErosionBlockState.createRockModel(
                this,
                ErosionRegistry.RawRegistry.RAW_GALENA.getId(), //item for texture
                ErosionRegistry.RawRegistry.GALENA_ORE.getId() //block texture
            )
        );

        // ============================================= ////
        //CHEMICAL REACTOR
        

        //CHEMICAL REACTOR SCRUBBER
        

        //CHEMICAL REACTOR COOLING SYSTEM
        

        //CHEMICAL REACTOR MODULE
        
        return;
    }
}