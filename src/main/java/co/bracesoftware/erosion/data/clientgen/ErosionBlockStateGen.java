package co.bracesoftware.erosion.data.clientgen;

import java.io.File;
import java.util.List;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.RockBlock;
import co.bracesoftware.erosion.world.blocks.crucible.CrucibleBlock;
import co.bracesoftware.erosion.world.blocks.material_purifier.MaterialPurifierBlock;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
import co.bracesoftware.erosion.data.commongen.ErosionTextureGen;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
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
            this, ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get()
        );

        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get()
        );
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateRandomRotations(
            this, ErosionRegistry.Blocks.PYRITE_ORE.get()
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
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateCustomTextures();
        ModelFile[][][] purifierModels = new ModelFile[ErosionConfig.MAX_PURIFIER_FUEL + 1][2][];
        getVariantBuilder(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()).forAllStates(s -> {
            Direction d = s.getValue(MaterialPurifierBlock.FACING);
            Integer f = s.getValue(MaterialPurifierBlock.FUEL);
            Boolean finished = s.getValue(MaterialPurifierBlock.FINISHED);
            
            String BLOCKID = ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId();
            String suf = finished ? "on" : "off";
            String texture = BLOCKID + "_front_fuel_" + f.toString() + "_" + suf;
            String modelf = BLOCKID + "_fuel_" + f.toString() + "_" + suf;

            ModelFile model = models().orientableWithBottom(
                modelf, modLoc("block/" + BLOCKID + "_side"),
                modLoc("block/" + ErosionUtils.getGeneratedFolder() + texture), modLoc("block/" + BLOCKID + "_bottom"),
                modLoc("block/" + BLOCKID + "_top")
            );

            return ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(((int) d.toYRot() + 180) % 360)
                .build()
            ;
        });

        String BLOCKID = ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId();
        simpleBlockItem(
            ErosionRegistry.Blocks.MATERIAL_PURIFIER.get(),
            models().orientableWithBottom(
                BLOCKID,
                modLoc("block/" + BLOCKID + "_side"),
                modLoc("block/" + BLOCKID + "_front"),
                modLoc("block/" + BLOCKID + "_bottom"),
                modLoc("block/" + BLOCKID + "_top")
            )
        );
        // ============================================= //
        BLOCKID = ErosionRegistry.RawRegistry.CRUCIBLE.getId();
        Block crucible = ErosionRegistry.Blocks.CRUCIBLE.get();

        ModelFile[] heatModels = new ModelFile[ErosionConfig.CRUCIBLE_SECONDS + 1];
        
        for(int i = 0; i <= ErosionConfig.CRUCIBLE_SECONDS; i++)
        {
            String modelName = BLOCKID + (i == 0 ? "" : "_heat_" + i);
            String texturePath = (i == 0) ? ("block/" + BLOCKID) : ("block/" + ErosionUtils.getGeneratedFolder() + BLOCKID + "_heat_" + i);

            heatModels[i] = models().withExistingParent(modelName, mcLoc("block/block"))
                .texture("particle", modLoc(texturePath))
                .texture("texture", modLoc(texturePath))
                
                // down
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_BOTTOM_FROM, CrucibleBlock.MIN_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_BOTTOM_TO, CrucibleBlock.MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()

                //walls
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_WALLS_FROM, CrucibleBlock.MIN_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_WALLS_TO, CrucibleBlock.INNER_MIN_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_WALLS_FROM, CrucibleBlock.INNER_MAX_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_WALLS_TO, CrucibleBlock.MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_WALLS_FROM, CrucibleBlock.INNER_MIN_XZ)
                    .to(CrucibleBlock.INNER_MIN_XZ, CrucibleBlock.Y_WALLS_TO, CrucibleBlock.INNER_MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                .element()
                    .from(CrucibleBlock.INNER_MAX_XZ, CrucibleBlock.Y_WALLS_FROM, CrucibleBlock.INNER_MIN_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_WALLS_TO, CrucibleBlock.INNER_MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                // edges
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_RIM_FROM, CrucibleBlock.MIN_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_RIM_TO, CrucibleBlock.RIM_MIN_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_RIM_FROM, CrucibleBlock.RIM_MAX_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_RIM_TO, CrucibleBlock.MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                .element()
                    .from(CrucibleBlock.MIN_XZ, CrucibleBlock.Y_RIM_FROM, CrucibleBlock.RIM_MIN_XZ)
                    .to(CrucibleBlock.RIM_MIN_XZ, CrucibleBlock.Y_RIM_TO, CrucibleBlock.RIM_MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end()
                    
                .element()
                    .from(CrucibleBlock.RIM_MAX_XZ, CrucibleBlock.Y_RIM_FROM, CrucibleBlock.RIM_MIN_XZ)
                    .to(CrucibleBlock.MAX_XZ, CrucibleBlock.Y_RIM_TO, CrucibleBlock.RIM_MAX_XZ)
                    .allFaces((direction, builder) -> builder.texture("#texture"))
                    .end();
        }

        getVariantBuilder(crucible).forAllStates(state -> {
            Direction d = state.getValue(CrucibleBlock.FACING);
            int heat = state.getValue(CrucibleBlock.HEAT);
            
            int safeHeat = Math.min(Math.max(heat, 0), ErosionConfig.CRUCIBLE_SECONDS);
            ModelFile model = heatModels[safeHeat];

            return ConfiguredModel.builder()
            .modelFile(model)
            .rotationY(((int) d.toYRot() + 180) % 360)
            .build();
        });

        simpleBlockItem(crucible, heatModels[0]);
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

        // ============================================= ////
        //CHEMICAL REACTOR
        var side = modLoc("block/chemical_reactor_side");
        var bottom = modLoc("block/chemical_reactor_bottom");
        var top = modLoc("block/" + ErosionUtils.getGeneratedFolder() + "chemical_reactor_top");

        ModelFile crm = models().cubeBottomTop(
            ErosionRegistry.RawRegistry.CHEMICAL_REACTOR.getId(),
            side, bottom, top
        );
        simpleBlock(ErosionRegistry.Blocks.CHEMICAL_REACTOR.get(), crm);
        simpleBlockItem(ErosionRegistry.Blocks.CHEMICAL_REACTOR.get(), crm);
        return;
    }

    
}