package co.bracesoftware.erosion.data;

import java.io.File;
import java.util.List;
import java.util.Map;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionExceptions.ErosionDataGenException;
import co.bracesoftware.erosion.ErosionModCompat;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.RockBlock;
import co.bracesoftware.erosion.data.clientgen.ErosionBlockStateGen;
import co.bracesoftware.erosion.data.commongen.ErosionTextureGen;
import co.bracesoftware.erosion.data.servergen.ErosionAdvGen;
import co.bracesoftware.erosion.data.servergen.ErosionBlockTagGen;
import co.bracesoftware.erosion.data.servergen.ErosionItemTagGen;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

public class ErosionDataGeneratorsProgInterface
{
    public static class ErosionTags
    {
        public abstract interface ErosionTaggable<T>
        {
            public abstract IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> tagz(TagKey<T> e);
        }
        public static class Items
        {
            public static void createSimpleRawOre(ErosionItemTagGen t, HolderLookup.Provider p, Item i)
            {
                t.tagz(Tags.Items.ORES).add(i);
            }

            public static void createSimplePowder(ErosionItemTagGen t, HolderLookup.Provider p, Item i)
            {
                t.tagz(Tags.Items.DUSTS).add(i);
            }
        }
        public static class Blocks
        {
            public static void createSimpleStone(ErosionBlockTagGen t, HolderLookup.Provider p, Block b)
            {
                t.tagz(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "stones"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "stone"))).add(b);
                return;
            }

            public static void createSimpleOre(ErosionBlockTagGen t, HolderLookup.Provider p, Block b)
            {
                t.tagz(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
                t.tagz(BlockTags.NEEDS_STONE_TOOL).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ore"))).add(b);
                return;
            }

            public static void createSimpleGravel(ErosionBlockTagGen t, HolderLookup.Provider p, Block b)
            {
                t.tagz(BlockTags.MINEABLE_WITH_SHOVEL).add(b);
                t.tagz(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "gravels"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "gravel"))).add(b);
                return;
            }

            public static void createSimpleRock(ErosionBlockTagGen t, HolderLookup.Provider p, Block b)
            {
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "rock"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "rocks"))).add(b);
                return;
            }

            public static void createSimpleDirt(ErosionBlockTagGen t, HolderLookup.Provider p, Block b)
            {
                t.tagz(BlockTags.MINEABLE_WITH_SHOVEL).add(b);
                t.tagz(BlockTags.DIRT).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirts"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirt"))).add(b);
                return;
            }

            public static void createSimpleMachine(ErosionBlockTagGen t, HolderLookup.Provider p, Block b)
            {
                t.tagz(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
                t.tagz(BlockTags.MINEABLE_WITH_AXE).add(b);

                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "functional_blocks"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "functional_block"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "crafting_tables"))).add(b);
                t.tagz(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "crafting_table"))).add(b);
                return;
            }
        }
    }
    public static class ErosionAdvancement
    {
        private static Boolean PARENT_ADVANCEMENT_CREATED = false;

        public static AdvancementHolder generateParentAdvancement(
            ErosionAdvGen.Generator t
        ) throws ErosionDataGenException
        {
            if(PARENT_ADVANCEMENT_CREATED)
            {
                throw new ErosionDataGenException("Parent advancement is already generated!");
            }
            PARENT_ADVANCEMENT_CREATED = true;
            
            ErosionUtils.Log(
                "Generated parent advancement."
            );
            return Advancement.Builder.advancement()
            .display(
                ErosionRegistry.Items.KAOLINIZED_GRANITE.get(),
                Component.literal(Erosion.MODNAME),
                Component.literal(Erosion.SUBTITLE),
                ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("tick", net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.tick())
            .save(t.k, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, Erosion.MODID), t.efh);
        }

        public static AdvancementHolder generateAdvancement(
            ErosionAdvGen.Generator t,
            String title, String desc, Item it, String id,
            AdvancementHolder a
        ) throws ErosionDataGenException
        {
            var b = Advancement.Builder.advancement();
            ResourceLocation bb = (a == null) 
            ? ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png")
            : null;

            if(a != null) b.parent(a);

            ErosionUtils.Log(
                "Generated advancement -> " + title
            );
            return b.display(
                it,//icon
                Component.literal(title),//title
                Component.literal(desc),//desc
                bb,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(it))
            .save(t.k, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, id), t.efh);
        }

        public static AdvancementHolder generateSimpleAdvancement(
            ErosionAdvGen.Generator t,
            String title, String desc, Item it, String id,
            AdvancementHolder a
        ) throws ErosionDataGenException
        {
            var b = Advancement.Builder.advancement();
            ResourceLocation bb = (a == null) 
            ? ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png")
            : null;

            if(a != null) b.parent(a);

            ErosionUtils.Log(
                "Generated simple advancement -> " + title
            );
            return b.display(
                it,
                Component.literal(title),
                Component.literal(desc),
                bb,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("manual_trigger", CriteriaTriggers.IMPOSSIBLE.createCriterion(
                new ImpossibleTrigger.TriggerInstance()
            ))
            .save(t.k, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, id), t.efh);
        }
    }
    public static class ErosionRecipe
    {
        public static void generateRecipe(
            String r,
            String o,
            List<String> p,
            Map<String, String> pd
        )
        {
            ErosionModCompat.JsonRecipeGenerator.generateCraftingRecipe(r, o, p, pd);
        }
    }

    public static class ErosionBlockState
    {
        public static void generateCustomTextures()
        {
            String BLOCKID = ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId();
            String resourcePath = ErosionUtils.getResourcesFolder() + "assets/" + Erosion.MODID + "/textures/block/";
            String generatedResourcesPath = ErosionUtils.getResourcesFolder() + "assets/" + Erosion.MODID + "/textures/block/" + ErosionUtils.getGeneratedFolder();
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

                    File outputFile = new File(generatedResourcesPath + BLOCKID + "_front_fuel_" + fuel + "_" + status + ".png");

                    ErosionTextureGen.combine(baseFile, layers, outputFile);
                }
            }

            BLOCKID = ErosionRegistry.RawRegistry.CRUCIBLE.getId();
            File baseCrucibleContent = new File(resourcePath + BLOCKID + ".png");

            for(int i = 1; i <= ErosionConfig.CRUCIBLE_SECONDS; i++)
            {
                File outputTex = new File(generatedResourcesPath + BLOCKID + "_heat_" + i + ".png");
                ErosionTextureGen.generateHeatedTexture(baseCrucibleContent, outputTex, i, ErosionConfig.CRUCIBLE_SECONDS);
            }
            return;
        }

        public static BlockModelBuilder createRockModel(
            ErosionBlockStateGen g,
            String modelName, String texturePath
        )
        {
            return g.models().withExistingParent(modelName, g.mcLoc("block/block"))
            .texture("particle", g.modLoc("block/" + texturePath))
            .texture("texture", g.modLoc("block/" + texturePath))
            
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

        public static void generateRandomRotations(ErosionBlockStateGen g, Block b)
        {
            var model = g.cubeAll(b);
            g.getVariantBuilder(b)
            .forAllStates(
                s -> new ConfiguredModel[] {
                    new ConfiguredModel(model, 0, 0, false),
                    new ConfiguredModel(model, 0, 90, false),
                    new ConfiguredModel(model, 0, 180, false),
                    new ConfiguredModel(model, 0, 270, false),
                    new ConfiguredModel(model, 90, 0, false),
                    new ConfiguredModel(model, 90, 90, false),
                    new ConfiguredModel(model, 90, 180, false),
                    new ConfiguredModel(model, 90, 270, false),
                    new ConfiguredModel(model, 180, 0, false),
                    new ConfiguredModel(model, 180, 90, false),
                    new ConfiguredModel(model, 180, 180, false),
                    new ConfiguredModel(model, 180, 270, false),
                    new ConfiguredModel(model, 270, 0, false),
                    new ConfiguredModel(model, 270, 90, false),
                    new ConfiguredModel(model, 270, 180, false),
                    new ConfiguredModel(model, 270, 270, false)
                }
            );
            g.simpleBlockItem(b, model);
            return;
        }
        public static void generateRockWithRandomRotations(
            ErosionBlockStateGen g,
            Item it, Block b, BlockModelBuilder m
        )
        {
            if(ErosionConfig.SOMETHING_WENT_WRONG) g.getVariantBuilder(b)
            .forAllStates(
                s -> new ConfiguredModel[] {
                    new ConfiguredModel(m, 0, 0, false),
                    new ConfiguredModel(m, 0, 90, false),
                    new ConfiguredModel(m, 0, 180, false),
                    new ConfiguredModel(m, 0, 270, false)
                }
            );

            g.getVariantBuilder(b)
            .forAllStates(
                s -> {
                    Direction d = s.getValue(ErosionSimpleBlocks.RockBlock.FACING);
                    int y = (int) d.toYRot();
                    return ConfiguredModel.builder()
                    .modelFile(m)
                    .rotationY((y + 180) % 360)
                    .build();
                }
            );

            g.simpleBlockItem(b, m);
            g.itemModels().basicItem(it);
            return;
        }
    }
}