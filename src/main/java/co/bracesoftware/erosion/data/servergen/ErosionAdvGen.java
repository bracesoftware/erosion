package co.bracesoftware.erosion.data.servergen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
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

public class ErosionAdvGen extends AdvancementProvider
{
    public ErosionAdvGen(
        PackOutput o, CompletableFuture<HolderLookup.Provider> r,
        ExistingFileHelper efh
    )
    {
        super(o, r, efh, List.of(new Generator()));
    }

    public static class Generator implements AdvancementProvider.AdvancementGenerator
    {
        public ExistingFileHelper efh = null;
        public Consumer<AdvancementHolder> k = null;

        @Override
        public void generate(HolderLookup.Provider r, Consumer<AdvancementHolder> s, ExistingFileHelper efh)
        {
            //DO NOT TOUCH
            this.efh = efh;
            this.k = s;

            //GENERATE
            var root = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateParentAdvancement(this);
            
            var crucible = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateAdvancement(
                this, "A cook!",
                "Craft a Crucible.",
                ErosionRegistry.Items.CRUCIBLE.get(),
                ErosionRegistry.RawRegistry.CRUCIBLE.getId(),
                root
            );

            var purifier = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateAdvancement(
                this, "Purifying Dirt",
                "Craft a Material Purifier.",
                ErosionRegistry.Items.MATERIAL_PURIFIER.get(),
                ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId(),
                root
            );
            return;
        }
    }
}