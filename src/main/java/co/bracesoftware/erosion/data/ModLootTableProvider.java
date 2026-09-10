package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider 
{
    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) 
    {
        super(output, Set.of(), List.of(
            new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class ModBlockLootTables extends BlockLootSubProvider 
    {
        protected ModBlockLootTables(HolderLookup.Provider provider) 
        {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() 
        {
            //SIMPLE BLOCKS
            dropSelf(ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get());
            dropSelf(ErosionRegistry.Blocks.DRIED_DIRT.get());
            dropSelf(ErosionRegistry.Blocks.ALBITIZED_GRANITE.get());
            dropSelf(ErosionRegistry.Blocks.QUARTZ_GRAVEL.get());
            dropSelf(ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get());
            dropSelf(ErosionRegistry.Blocks.CRACKED_CALCITE.get());

            add(ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_MALACHITE.get()
                )
            );
            add(ErosionRegistry.Blocks.HEMATITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_HEMATITE.get()
                )
            );
            add(ErosionRegistry.Blocks.LIMONITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_LIMONITE.get()
                )
            );
            add(ErosionRegistry.Blocks.MAGNETITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_MAGNETITE.get()
                )
            );

            add(ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.NATIVE_GOLD.get()
                )
            );

            add(ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_CASSITERITE.get()
                )
            );

            add(ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.NATIVE_SILVER.get()
                )
            );

            add(ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_BISMUTHINITE.get()
                )
            );
            add(ErosionRegistry.Blocks.SPHALERITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_SPHALERITE.get()
                )
            );
            add(ErosionRegistry.Blocks.AZURITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_AZURITE.get()
                )
            );
            add(ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_TETRAHEDRITE.get()
                )
            );

            add(ErosionRegistry.Blocks.RUBY_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RUBY.get()
                )
            );

            dropSelf(ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get());

            dropSelf(ErosionRegistry.Blocks.RAW_LIMONITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_HEMATITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_MAGNETITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_MALACHITE.get());

            dropSelf(ErosionRegistry.Blocks.NATIVE_GOLD.get());
            dropSelf(ErosionRegistry.Blocks.NATIVE_SILVER.get());

            dropSelf(ErosionRegistry.Blocks.RAW_SPHALERITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_BISMUTHINITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_CASSITERITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_AZURITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get());

            //MACHINES
            dropSelf(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get());
            dropSelf(ErosionRegistry.Blocks.CRUCIBLE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return List.of(
                //SIMPLE BLOCKS
                ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get(),

                ErosionRegistry.Blocks.DRIED_DIRT.get(),
                ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get(),

                ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(),
                ErosionRegistry.Blocks.ALBITIZED_GRANITE.get(),
                ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get(),
                ErosionRegistry.Blocks.CRACKED_CALCITE.get(),

                ErosionRegistry.Blocks.LIMONITE_ORE.get(),
                ErosionRegistry.Blocks.HEMATITE_ORE.get(),
                ErosionRegistry.Blocks.MAGNETITE_ORE.get(),
                ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get(),

                ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
                ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(),
                ErosionRegistry.Blocks.SPHALERITE_ORE.get(),

                ErosionRegistry.Blocks.RAW_LIMONITE.get(),
                ErosionRegistry.Blocks.RAW_HEMATITE.get(),
                ErosionRegistry.Blocks.RAW_MAGNETITE.get(),
                ErosionRegistry.Blocks.RAW_MALACHITE.get(),

                ErosionRegistry.Blocks.NATIVE_GOLD.get(),
                ErosionRegistry.Blocks.NATIVE_SILVER.get(),

                ErosionRegistry.Blocks.RAW_SPHALERITE.get(),
                ErosionRegistry.Blocks.RAW_BISMUTHINITE.get(),
                ErosionRegistry.Blocks.RAW_CASSITERITE.get(),

                ErosionRegistry.Blocks.AZURITE_ORE.get(),
                ErosionRegistry.Blocks.RAW_AZURITE.get(),

                ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get(),
                ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(),
                ErosionRegistry.Blocks.RUBY_ORE.get(),

                // MACHINES
                ErosionRegistry.Blocks.MATERIAL_PURIFIER.get(),
                ErosionRegistry.Blocks.CRUCIBLE.get()
            );
        }
    }
}