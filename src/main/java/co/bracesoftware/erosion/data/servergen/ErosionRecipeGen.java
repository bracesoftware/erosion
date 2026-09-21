package co.bracesoftware.erosion.data.servergen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.erosion.ErosionModCompat;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.ItemExistsCondition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ErosionRecipeGen extends RecipeProvider implements IConditionBuilder {

    public ErosionRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        ShapedRecipeBuilder.shaped(
            RecipeCategory.BREWING, ErosionRegistry.Blocks.CHEMICAL_REACTOR.get()
        )
        .pattern("IMI")
        .pattern("DGD")
        .pattern("OOO")
        .define('I', Items.IRON_INGOT)
        .define('M', ErosionRegistry.Blocks.MATERIAL_PURIFIER.get())
        .define('G', Items.GLASS)
        .define('D', Items.DEEPSLATE)
        .define('O', Items.OBSIDIAN)
        .unlockedBy("has_purifier", has(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()))
        .save(output);

        ShapedRecipeBuilder.shaped(
            RecipeCategory.BREWING, ErosionRegistry.Blocks.CHEMICAL_REACTOR_SCRUBBER.get()
        )
        .pattern("IMI")
        .pattern("DKD")
        .pattern("ONO")
        .define('I', Items.IRON_INGOT)
        .define('M', ErosionRegistry.Blocks.MATERIAL_PURIFIER.get())
        .define('K', Items.ITEM_FRAME)
        .define('D', Items.NETHERITE_SCRAP)
        .define('O', Items.OBSIDIAN)
        .define('N', Items.NETHERITE_INGOT)
        .unlockedBy("has_purifier", has(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()))
        .save(output);


        ShapedRecipeBuilder.shaped(
            RecipeCategory.REDSTONE, ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()
        ).pattern("IHI")
        .pattern("FRC")
        .pattern("WHW")
        .define('I', Items.IRON_INGOT)
        .define('R', Items.REDSTONE)
        .define('F', Items.FURNACE)
        .define('W', Items.STONE_BRICKS)
        .define('H', Items.HOPPER)
        .define('C', Items.CHEST)
        .unlockedBy("has_redstone", has(Items.REDSTONE))
        .save(output);

        ShapedRecipeBuilder.shaped(
            RecipeCategory.BREWING, ErosionRegistry.Items.GAS_FILTER.get()
        ).pattern("PPP")
        .pattern("CAC")
        .pattern("PPP")
        .define('P', Items.PAPER)
        .define('C', Items.CHARCOAL)
        .define('A', Items.ITEM_FRAME)
        .unlockedBy("has_charcoal", has(Items.CHARCOAL))
        .save(output);

        ShapedRecipeBuilder.shaped(
            RecipeCategory.BREWING, ErosionRegistry.Blocks.CRUCIBLE.get()
        ).pattern("G G")
        .pattern("G G")
        .pattern("GGG")
        .define('G', ErosionRegistry.Items.KAOLINIZED_GRANITE.get())
        .unlockedBy("has_kaolinized_granite", has(ErosionRegistry.Items.KAOLINIZED_GRANITE.get()))
        .save(output);

        ShapedRecipeBuilder.shaped(
            RecipeCategory.COMBAT, ErosionRegistry.Items.BASIC_MASK.get()
        ).pattern("S S")
        .pattern("PPP")
        .pattern("PWP")
        .define('S', Items.STRING)
        .define('P', Items.PAPER)
        .define('W', Items.WHITE_WOOL)
        .unlockedBy("has_paper", has(Items.PAPER))
        .save(output);

        ShapedRecipeBuilder.shaped(
            RecipeCategory.COMBAT, ErosionRegistry.Items.GAS_MASK.get()
        ).pattern("S S")
        .pattern("GPG")
        .pattern("PWP")
        .define('S', Items.STRING)
        .define('P', Items.LEATHER)
        .define('W', Items.NETHERITE_INGOT)
        .define('G', Items.GLASS_PANE)
        .unlockedBy("has_netheriteingot", has(Items.NETHERITE_INGOT))
        .save(output);

        ShapelessRecipeBuilder.shapeless(
            RecipeCategory.MISC, ErosionRegistry.Items.FLUX.get()
        ).requires(ErosionRegistry.Items.FELDSPAR_POWDER.get())
        .unlockedBy("has_feldspar_powder", has(ErosionRegistry.Items.FELDSPAR_POWDER.get()))
        .save(output);

        ShapelessRecipeBuilder.shapeless(
            RecipeCategory.MISC, ErosionRegistry.Items.CRUSHED_EGG_SHELL.get()
        ).requires(Items.EGG)
        .unlockedBy("has_egg", has(Items.EGG))
        .save(output);

        ShapelessRecipeBuilder.shapeless(
            RecipeCategory.MISC, Items.BONE_MEAL
        ).requires(ErosionRegistry.Items.CRACKED_CALCITE.get())
        .unlockedBy("has_cracked_calcite", has(ErosionRegistry.Items.CRACKED_CALCITE.get()))
        .save(output, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, "bone_meal_from_cracked_calcite"));

        ShapelessRecipeBuilder.shapeless(
            RecipeCategory.MISC, Items.BONE_MEAL
        ).requires(ErosionRegistry.Items.DEBRIS.get())
        .unlockedBy("has_debris", has(ErosionRegistry.Items.DEBRIS.get()))
        .save(output, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, "bone_meal_from_debris"));

        //MOD COMPAT!!
        ErosionModCompat.JsonRecipeGenerator.generateCraftingRecipe(
            "raw_zinc_from_chunks", 
            ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                ErosionModCompat.CompatibleMods.CREATE.getModId(), "raw_zinc"
            ),
            List.of(
                "XXX",
                "XXX",
                "XXX"
            ),
            Map.of(
                "X", ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                    Erosion.MODID, ErosionRegistry.RawRegistry.ZINC_CHUNK.getId()
                )
            )
        );
        ErosionModCompat.JsonRecipeGenerator.generateCraftingRecipe(
            "raw_silver_from_chunks", 
            ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                ErosionModCompat.CompatibleMods.OREGANIZED.getModId(), "raw_silver"
            ),
            List.of(
                "XXX",
                "XXX",
                "XXX"
            ),
            Map.of(
                "X", ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                    Erosion.MODID, ErosionRegistry.RawRegistry.SILVER_CHUNK.getId()
                )
            )
        );
        ErosionDataGeneratorsProgInterface.ErosionRecipe.generateRecipe(
            "sulfuric_acid_bucket_from_bottle",
            ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                Erosion.MODID, ErosionRegistry.RawRegistry.BUCKET_OF_SULFURIC_ACID.getId()
            ),
            List.of(
                "AB"
            ),
            Map.of(
                "A", ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                    ErosionModCompat.CompatibleMods.BUTCHERY.getModId(), "bottle_of_sulfuric_acid"
                ),
                "B", ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                    ResourceLocation.DEFAULT_NAMESPACE, Items.BUCKET.getDescription().getString().toLowerCase()
                )
            )
        );
        ErosionModCompat.JsonRecipeGenerator.generateCraftingRecipe(
            "sulfur_from_sulfur_slag", 
            ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                ErosionModCompat.CompatibleMods.BUTCHERY.getModId(), "sulfur"
            ),
            List.of(
                "XXX",
                "XXX",
                "XXX"
            ),
            Map.of(
                "X", ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                    Erosion.MODID, ErosionRegistry.RawRegistry.SULFUR_SLAG.getId()
                )
            )
        );

        return;
    }
}