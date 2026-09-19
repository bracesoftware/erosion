package co.bracesoftware.erosion;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import co.bracesoftware.erosion.api.eventbus.ErosionEvents;
import net.neoforged.fml.ModList;

//@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionModCompat
{
    public static class CompatibleMod
    {
        private String modid;
        private String name;

        public Runnable setup;
        public Runnable discard;

        private boolean present = false;

        public CompatibleMod(String m, String n, List<Runnable> l)
        {
            this.modid = m;
            this.name = n;

            if(l.size() != 2)
            {
                throw new RuntimeException("Only 2 Runnable objects are required for mod compat -> err::" + this.modid);
            }
            this.setup = l.get(0);
            this.discard = l.get(1);
        }

        public String getName()
        {
            return this.name;
        }

        public String getModId()
        {
            return this.modid;
        }

        public void setupCompat()
        {
            ErosionUtils.Log("Setting up compatibility for mod: " + this.name);
            if(!ModList.get().isLoaded(this.modid))
            {
                ErosionUtils.Log("Compatible mod `" + this.name + "` not found.");
                return;
            }
            this.present = true;
            this.setup.run();
            ErosionUtils.Log("Compatible mod `" + this.name + "` found. Setup finished.");
            return;
        }

        public void discardCompat()
        {
            ErosionUtils.Log("Discarding compatibility for mod: " + this.name);
            if(!ModList.get().isLoaded(this.modid))
            {
                ErosionUtils.Log("Compatible mod `" + this.name + "` not found.");
                return;
            }
            this.discard.run();
            ErosionUtils.Log("Compatible mod `" + this.name + "` found. Discard finished.");
            return;
        }

        public boolean isPresent()
        {
            return this.present;
        }
    }
    /////////////////////////////////////////////////////////////////
    public static class CompatibleMods
    {
        public static final CompatibleMod CREATE = new CompatibleMod(
            "create", "Create Mod", List.of(
                () -> {
                    ErosionUtils.Log("Added compatibility for ZINC ORES.");
                    return;
                },
                () -> {
                    return;
                }
            )
        );
        public static final CompatibleMod OREGANIZED = new CompatibleMod(
            "oreganized", "Oreganized: Blacksmith Mod", List.of(
                () -> {
                    ErosionUtils.Log("Added compatibility for SILVER ORES.");
                    return;
                },
                () -> {
                    return;
                }
            )
        );
        public static final CompatibleMod BUTCHERY = new CompatibleMod(
            "butchery", "Butchery Mod", List.of(
                () -> {
                    ErosionUtils.Log("Added compatibility for SILVER ORES.");
                    return;
                },
                () -> {
                    return;
                }
            )
        );
    }
    /////////////////////////////////////////////////////////////////
    public static final List<CompatibleMod> COMPATIBLE_MODS = List.of(
        CompatibleMods.CREATE,
        CompatibleMods.OREGANIZED,
        CompatibleMods.BUTCHERY
    );

    public static class JsonRecipeGenerator
    {   
        public static String getItemNameFromNamespaceAndPath(String namespace, String path)
        {
            //dummy func for better look
            return namespace + ':' + path;
        }
        public static void generateCraftingRecipe(
            String recipeName,
            String outputitem,
            List<String> pattern,
            Map<String, String> patternDefinition
        )
        {
            JsonObject recipeJson = new JsonObject();

            JsonArray conditions = new JsonArray();
            JsonObject itemCondition = new JsonObject();
            itemCondition.addProperty("type", "neoforge:item_exists");
            itemCondition.addProperty("item", outputitem);
            conditions.add(itemCondition);
            recipeJson.add("neoforge:conditions", conditions);

            recipeJson.addProperty("type", "minecraft:crafting_shaped");
            recipeJson.addProperty("category", "misc");

            JsonArray p = new JsonArray();
            for(String row : pattern)
            {
                p.add(row);
            }
            recipeJson.add("pattern", p);

            JsonObject key = new JsonObject();
            for(Map.Entry<String, String> entry : patternDefinition.entrySet())
            {
                JsonObject ingredient = new JsonObject();
                ingredient.addProperty("item", entry.getValue());
                key.add(entry.getKey(), ingredient);
            }
            recipeJson.add("key", key);

            JsonObject result = new JsonObject();
            result.addProperty("count", 1);
            result.addProperty("id", outputitem);
            recipeJson.add("result", result);

            try
            {
                String n = ErosionUtils.getResourcesFolder() + "data/" + Erosion.MODID + "/recipe/" + recipeName + ".json";
                ErosionUtils.Log("Created recipe -> " + n);
                Path path = Path.of(n);

                java.nio.file.Files.createDirectories(path.getParent());
                try(FileWriter writer = new FileWriter(path.toFile()))
                {
                    ErosionMod.GSON.toJson(recipeJson, writer);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }
    }
    
    public static final Map<Supplier<Item>, List<Component>> COMPAT_MOD_ITEM_INFO = Map.ofEntries(
        Map.entry(
            () -> BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(
                    CompatibleMods.CREATE.getModId(),
                    "raw_zinc"
                )
            ), List.of(
                Component.literal("Compatible zinc item (Zn)").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
            )
        ),
        Map.entry(
            () -> BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(
                    CompatibleMods.OREGANIZED.getModId(),
                    "raw_silver"
                )
            ), List.of(
                Component.literal("Compatible silver item (Ag)").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
            )
        ),
        Map.entry(
            () -> BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(
                    CompatibleMods.BUTCHERY.getModId(),
                    "sulfur"
                )
            ), List.of(
                Component.literal("Compatible sulfur item (S)").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
            )
        ),
        Map.entry(
            () -> BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(
                    CompatibleMods.BUTCHERY.getModId(),
                    "bottle_of_sulfuric_acid"
                )
            ), List.of(
                Component.literal("Compatible sulfur item (S)").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
            )
        )
    );
    
    @ErosionEvents.ErosionEventSubscribe
    public static void DoDescriptions(ErosionEvents.ErosionItemDescription e)
    {
        for(var m : COMPAT_MOD_ITEM_INFO.entrySet())
        {
            var i = m.getKey().get();
            var l = m.getValue();
            if(i == e.whatItem())
            {
                for(var c : l)
                {
                    e.addItemDescription(c);
                }
            }
        }
        return;
    }

}