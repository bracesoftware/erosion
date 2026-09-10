package co.bracesoftware.erosion.data;

import java.io.File;
import java.util.List;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import co.bracesoftware.erosion.blocks.ErosionSimpleBlocks.RockBlock;
import co.bracesoftware.erosion.blocks.crucible.CrucibleBlock;
import co.bracesoftware.erosion.blocks.material_purifier.MaterialPurifierBlock;
import co.bracesoftware.erosion.blocks.material_purifier.MaterialPurifierBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider 
{
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) 
    {
        super(output, Erosion.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() 
    {
        //SIMPLE BLOCKS
        simpleBlockWithItem(
            ErosionRegistry.Blocks.DRIED_DIRT.get(),
            cubeAll(ErosionRegistry.Blocks.DRIED_DIRT.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get(),
            cubeAll(ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.ALBITIZED_GRANITE.get(),
            cubeAll(ErosionRegistry.Blocks.ALBITIZED_GRANITE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(),
            cubeAll(ErosionRegistry.Blocks.QUARTZ_GRAVEL.get())
        );

        simpleBlockWithItem(
            ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get(),
            cubeAll(ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.CRACKED_CALCITE.get(),
            cubeAll(ErosionRegistry.Blocks.CRACKED_CALCITE.get())
        );

        simpleBlockWithItem(
            ErosionRegistry.Blocks.LIMONITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.LIMONITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.MAGNETITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.MAGNETITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.HEMATITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.HEMATITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get())
        );

        simpleBlockWithItem(
            ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
            cubeAll(ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
            cubeAll(ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
            cubeAll(ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get())
        );

        simpleBlockWithItem(
            ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.BISMUTHINITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.AZURITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.AZURITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.RUBY_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.RUBY_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.SAPPHIRE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.SAPPHIRE_ORE.get())
        );

        simpleBlockWithItem(
            ErosionRegistry.Blocks.SPHALERITE_ORE.get(),
            cubeAll(ErosionRegistry.Blocks.SPHALERITE_ORE.get())
        );
        simpleBlockWithItem(
            ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get(),
            cubeAll(ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get())
        );
        //MACHINES
        // ============================================= //
        generateCustomTextures();
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
                modLoc("block/generated/" + texture), modLoc("block/" + BLOCKID + "_bottom"),
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
            String texturePath = (i == 0) ? ("block/" + BLOCKID) : ("block/generated/" + BLOCKID + "_heat_" + i);

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

        simpleBlock(
            ErosionRegistry.Blocks.RAW_LIMONITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_LIMONITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.LIMONITE_ORE.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.RAW_HEMATITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_HEMATITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.HEMATITE_ORE.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.RAW_MAGNETITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_MAGNETITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.MAGNETITE_ORE.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.RAW_MALACHITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_MALACHITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.CALCITE_MALACHITE_ORE.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.NATIVE_GOLD.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.NATIVE_GOLD.getId(), //item for texture
                ErosionRegistry.RawRegistry.NATIVE_GOLD_DEPOSIT.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.NATIVE_SILVER.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.NATIVE_SILVER.getId(), //item for texture
                ErosionRegistry.RawRegistry.NATIVE_SILVER_DEPOSIT.getId() //block texture
            )
        );

        simpleBlock(
            ErosionRegistry.Blocks.RAW_CASSITERITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_CASSITERITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.CASSITERITE_DEPOSIT.getId() //block texture
            )
        );

        simpleBlock(
            ErosionRegistry.Blocks.RAW_BISMUTHINITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_BISMUTHINITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.BISMUTHINITE_ORE.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.RAW_SPHALERITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_SPHALERITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.SPHALERITE_ORE.getId() //block texture
            )
        );

        simpleBlock(
            ErosionRegistry.Blocks.RAW_AZURITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_AZURITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.AZURITE_ORE.getId() //block texture
            )
        );
        simpleBlock(
            ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get(),
            createRockModel(
                ErosionRegistry.RawRegistry.RAW_TETRAHEDRITE.getId(), //item for texture
                ErosionRegistry.RawRegistry.TETRAHEDRITE_ORE.getId() //block texture
            )
        );

        // ============================================= //
        return;
    }

    private void generateCustomTextures()
    {
        String BLOCKID = ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId();
        String resourcePath = ErosionUtils.getResourcesFolder() + "assets/" + Erosion.MODID + "/textures/block/";
        //String generatedPath = "../src/generated/resources/assets/" + Erosion.MODID + "/textures/block/";
        File baseFile = new File(resourcePath + BLOCKID + "_front.png");

        for(int fuel = 0; fuel <= ErosionConfig.MAX_PURIFIER_FUEL; fuel++)
        {
            for(boolean finished : new boolean[]{false, true})
            {
                String status = finished ? "on" : "off";

                File fuelLayer = new File(resourcePath + "layers/fuel_" + fuel + ".png");
                File lampLayer = new File(resourcePath + "layers/lamp_" + status + ".png");
                
                List<File> layers = List.of(
                    fuelLayer,
                    lampLayer
                );

                File outputFile = new File(resourcePath + "generated/" + BLOCKID + "_front_fuel_" + fuel + "_" + status + ".png");

                TextureProvider.combine(baseFile, layers, outputFile);
            }
        }

        BLOCKID = ErosionRegistry.RawRegistry.CRUCIBLE.getId();
        File baseCrucibleContent = new File(resourcePath + BLOCKID + ".png");

        for(int i = 1; i <= ErosionConfig.CRUCIBLE_SECONDS; i++)
        {
            File outputTex = new File(resourcePath + "generated/" + BLOCKID + "_heat_" + i + ".png");
            TextureProvider.generateHeatedTexture(baseCrucibleContent, outputTex, i, ErosionConfig.CRUCIBLE_SECONDS);
        }
        return;
    }
    // Helper metoda za generisanje 3D modela kamenčića
    public BlockModelBuilder createRockModel(String modelName, String texturePath)
    {
        return models().withExistingParent(modelName, mcLoc("block/block"))
            .texture("particle", modLoc("block/" + texturePath))
            .texture("texture", modLoc("block/" + texturePath))
            
            .element()
            .from(RockBlock.SHAPE_FIRSTDIM_X1, RockBlock.SHAPE_FIRSTDIM_Y1, RockBlock.SHAPE_FIRSTDIM_Z1)
            .to(RockBlock.SHAPE_FIRSTDIM_X2, RockBlock.SHAPE_FIRSTDIM_Y2, RockBlock.SHAPE_FIRSTDIM_Z2)
            .allFaces((direction, builder) -> builder.texture("#texture"))
            .end()

            .element()
            .from(RockBlock.SHAPE_SECONDDIM_X1, RockBlock.SHAPE_SECONDDIM_Y1, RockBlock.SHAPE_SECONDDIM_Z1)
            .to(RockBlock.SHAPE_SECONDDIM_X2, RockBlock.SHAPE_SECONDDIM_Y2, RockBlock.SHAPE_SECONDDIM_Z2)
            .allFaces((direction, builder) -> builder.texture("#texture"))
            .end()

            .element()
            .from(RockBlock.SHAPE_THIRDDIM_X1, RockBlock.SHAPE_THIRDDIM_Y1, RockBlock.SHAPE_THIRDDIM_Z1)
            .to(RockBlock.SHAPE_THIRDDIM_X2, RockBlock.SHAPE_THIRDDIM_Y2, RockBlock.SHAPE_THIRDDIM_Z2)
            .allFaces((direction, builder) -> builder.texture("#texture"))
            .end();
    }
}