package co.bracesoftware.erosion.data.servergen;

import co.bracesoftware.erosion.world.ErosionModContentManager;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ErosionLootGen extends LootTableProvider 
{
    public ErosionLootGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) 
    {
        super(output, Set.of(), List.of(
            new SubProviderEntry(ErosionLootGenSubProvider::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    public static class ErosionLootGenSubProvider extends BlockLootSubProvider 
    {
        protected ErosionLootGenSubProvider(HolderLookup.Provider provider) 
        {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override public void dropSelf(Block b)
        {
            super.dropSelf(b);
        }

        public static ErosionLootGenSubProvider subProvider;

        @Override
        protected void generate() 
        {
            subProvider = this;
            for(var rrr : ErosionModContentManager.EROSION_LOOT_GEN_TASKS)
            {
                rrr.run();
            }
            //SIMPLE BLOCKS
            dropSelf(ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get());
            dropSelf(ErosionRegistry.Blocks.CRACKED_STONE.get());
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
            add(
                ErosionRegistry.Blocks.BORAX_DEPOSIT.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.BORAX.get()
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
            add(ErosionRegistry.Blocks.GOETHITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_GOETHITE.get()
                )
            );
            add(ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_TETRAHEDRITE.get()
                )
            );
            add(ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_ARSENOPYRITE.get()
                )
            );
            add(ErosionRegistry.Blocks.PYRITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_PYRITE.get()
                )
            );
            add(ErosionRegistry.Blocks.ANGLESITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_ANGLESITE.get()
                )
            );
            add(ErosionRegistry.Blocks.HALITE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_HALITE.get()
                )
            );
            add(ErosionRegistry.Blocks.GALENA_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RAW_GALENA.get()
                )
            );

            add(ErosionRegistry.Blocks.RUBY_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.RUBY.get()
                )
            );
            add(ErosionRegistry.Blocks.SAPPHIRE_ORE.get(),
                b -> createOreDrop(
                    b, ErosionRegistry.Items.SAPPHIRE.get()
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
            dropSelf(ErosionRegistry.Blocks.RAW_GOETHITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_ARSENOPYRITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_PYRITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_ANGLESITE.get());
            dropSelf(ErosionRegistry.Blocks.RAW_GALENA.get());
            dropSelf(ErosionRegistry.Blocks.RAW_HALITE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            var p = new ArrayList<>(List.of(
                //SIMPLE BLOCKS
                ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get(),
                ErosionRegistry.Blocks.CRACKED_STONE.get(),

                ErosionRegistry.Blocks.DRIED_DIRT.get(),
                ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get(),

                ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(),
                ErosionRegistry.Blocks.ALBITIZED_GRANITE.get(),
                ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get(),
                ErosionRegistry.Blocks.CRACKED_CALCITE.get(),

                ErosionRegistry.Blocks.LIMONITE_ORE.get(),
                ErosionRegistry.Blocks.HEMATITE_ORE.get(),
                ErosionRegistry.Blocks.BORAX_DEPOSIT.get(),
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
                ErosionRegistry.Blocks.GOETHITE_ORE.get(),
                ErosionRegistry.Blocks.RAW_AZURITE.get(),
                ErosionRegistry.Blocks.RAW_GOETHITE.get(),

                ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get(),
                ErosionRegistry.Blocks.RAW_ARSENOPYRITE.get(),
                ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(),
                ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get(),
                ErosionRegistry.Blocks.PYRITE_ORE.get(),
                ErosionRegistry.Blocks.ANGLESITE_ORE.get(),
                ErosionRegistry.Blocks.HALITE_ORE.get(),
                ErosionRegistry.Blocks.GALENA_ORE.get(),
                ErosionRegistry.Blocks.RAW_PYRITE.get(),
                ErosionRegistry.Blocks.RAW_ANGLESITE.get(),
                ErosionRegistry.Blocks.RAW_GALENA.get(),
                ErosionRegistry.Blocks.RAW_HALITE.get(),
                ErosionRegistry.Blocks.RUBY_ORE.get(),
                ErosionRegistry.Blocks.SAPPHIRE_ORE.get()

                // MACHINES
                //added via the manager
            ));
            p.addAll(
                ErosionModContentManager.EROSION_KNOWN_BLOCKS.stream()
                .map(ErosionModContent.ErosionBlock::get)
                .toList()
            );
            return p;
        }
    }
}