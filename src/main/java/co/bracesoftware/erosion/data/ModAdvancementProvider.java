package co.bracesoftware.erosion.data;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModAdvancementProvider extends AdvancementProvider
{
    public ModAdvancementProvider(
        PackOutput o, CompletableFuture<HolderLookup.Provider> r,
        ExistingFileHelper efh
    )
    {
        super(o, r, efh, List.of(new Generator()));
    }

    private static class Generator implements AdvancementProvider.AdvancementGenerator
    {
        private ExistingFileHelper efh = null;
        private Consumer<AdvancementHolder> k = null;

        private static Boolean PARENT_ADVANCEMENT_CREATED = false;

        private AdvancementHolder generateParentAdvancement()
        {
            if(PARENT_ADVANCEMENT_CREATED)
            {
                throw new RuntimeException("Parent advancement is already generated!");
            }
            PARENT_ADVANCEMENT_CREATED = true;
            return Advancement.Builder.advancement()
            .display(
                ErosionRegistry.Items.KAOLINIZED_GRANITE.get(),
                Component.literal(Erosion.MODNAME),
                Component.literal("Welcome to the geochemically accurate Minecraft!"),
                ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("tick", net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.tick())
            .save(this.k, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, Erosion.MODID), this.efh);
        }

        private AdvancementHolder generateAdvancement(
            String title, String desc, Item it, String id,
            AdvancementHolder a
        )
        {
            var b = Advancement.Builder.advancement();
            ResourceLocation bb = (a == null) 
            ? ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png")
            : null;

            if(a != null) b.parent(a);

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
            .save(this.k, ResourceLocation.fromNamespaceAndPath(Erosion.MODID, id), this.efh);
        }
        @Override
        public void generate(HolderLookup.Provider r, Consumer<AdvancementHolder> s, ExistingFileHelper efh)
        {
            //DO NOT TOUCH
            this.efh = efh;
            this.k = s;

            //GENERATE
            var ra = generateParentAdvancement();
            
            var crucible = generateAdvancement(
                "A cook!",
                "Craft a Crucible.",
                ErosionRegistry.Items.CRUCIBLE.get(),
                ErosionRegistry.RawRegistry.CRUCIBLE.getId(),
                ra
            );

            var purifier = generateAdvancement(
                "Purifying Dirt",
                "Craft a Material Purifier.",
                ErosionRegistry.Items.MATERIAL_PURIFIER.get(),
                ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId(),
                ra
            );
            return;
        }
    }
}