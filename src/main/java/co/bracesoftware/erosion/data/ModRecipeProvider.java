package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.erosion.ErosionModCompat;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
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

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        ShapedRecipeBuilder.shaped(
            RecipeCategory.REDSTONE, ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()
        ).pattern("I I")
        .pattern("RFR")
        .pattern("III")
        .define('I', Items.IRON_INGOT)
        .define('R', Items.REDSTONE)
        .define('F', Items.FURNACE)
        .unlockedBy("has_redstone", has(Items.REDSTONE))
        .save(output);

        ShapedRecipeBuilder.shaped(
            RecipeCategory.BREWING, ErosionRegistry.Blocks.CRUCIBLE.get()
        ).pattern("G G")
        .pattern("G G")
        .pattern("GGG")
        .define('G', ErosionRegistry.Items.KAOLINIZED_GRANITE.get())
        .unlockedBy("has_kaolinized_granite", has(ErosionRegistry.Items.KAOLINIZED_GRANITE.get()))
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
        .save(output);

        //MOD COMPAT!!
        ErosionModCompat.JsonRecipeGenerator.generateCraftingRecipe(
            "raw_zinc_from_chunks", 
            ErosionModCompat.JsonRecipeGenerator.getItemNameFromNamespaceAndPath(
                ErosionModCompat.CREATE.getModId(), "raw_zinc"
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
                ErosionModCompat.OREGANIZED.getModId(), "raw_silver"
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

        return;
    }
}