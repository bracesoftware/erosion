package co.bracesoftware.erosion.world.recipes;

import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore.ErosionRecipeRegistry;
import co.bracesoftware.erosion.world.ErosionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ErosionFoodSaltingRecipe extends CustomRecipe
{
    public ErosionFoodSaltingRecipe(CraftingBookCategory c)
    {
        super(c);
    }

    @Override public boolean canCraftInDimensions(int w, int h) { return w * h >= 2; }
    @Override public RecipeSerializer<?> getSerializer() { return ErosionRegistry.DataComponents.IS_SALTED_FOOD_SERIALIZER.get(); }

    @Override public final boolean matches(CraftingInput ci, Level l)
    {
        var food = ItemStack.EMPTY;
        var salt = ItemStack.EMPTY;
        int c = 0;
        for(int i = 0; i < ci.size(); i++)
        {
            var s = ci.getItem(i);
            if(s.isEmpty()) continue;
            ++c;
            if(s.is(ErosionRegistry.Items.SALT.get()))
            {
                salt = s;
            }
            else if(
                ErosionRecipeRegistry.Misc.SaltableFoodsSystem.isSaltableFood(s) &&
                s.has(DataComponents.FOOD)
            )
            {
                boolean yes = s.getOrDefault(ErosionRegistry.DataComponents.IS_SALTED_FOOD.get(),false);
                if(!yes) food = s;
            }
        }
        return c == 2 && !food.isEmpty() && !salt.isEmpty();
    }

    @Override public final ItemStack assemble(CraftingInput c, HolderLookup.Provider r)
    {
        var food = ItemStack.EMPTY;

        for(int i = 0; i < c.size(); i++)
        {
            var s = c.getItem(i);
            if(
                !s.isEmpty() &&
                ErosionRecipeRegistry.Misc.SaltableFoodsSystem.isSaltableFood(s)
            )
            {
                food = s.copy();
                break;
            }
        }

        if(food.isEmpty()) return ItemStack.EMPTY;

        var o = food.get(DataComponents.FOOD);
        if(o != null)
        {
            var newFood = new FoodProperties.Builder()
            .nutrition(o.nutrition() + ErosionConfig.SaltableFoodsSystem.EXTRA_NUTRITION)
            .saturationModifier(o.saturation() + ErosionConfig.SaltableFoodsSystem.EXTRA_SATURATION)
            .build();

            food.set(DataComponents.FOOD, newFood);
            food.set(ErosionRegistry.DataComponents.IS_SALTED_FOOD.get(), true);
        }

        food.setCount(1);
        return food;
    }
}
