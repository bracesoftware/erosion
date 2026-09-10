package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider 
{
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) 
    {
        super(output, lookupProvider, Erosion.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) 
    {
        //SIMPLE BLOCKS
        createSimpleGravel(provider, ErosionRegistry.Blocks.DRIED_DIRT.get());

        createSimpleStone(provider, ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get());
        createSimpleGravel(provider, ErosionRegistry.Blocks.QUARTZ_GRAVEL.get());
        createSimpleStone(provider, ErosionRegistry.Blocks.ALBITIZED_GRANITE.get());
        createSimpleStone(provider, ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get());
        createSimpleStone(provider, ErosionRegistry.Blocks.CRACKED_CALCITE.get());

        createSimpleOre(provider, ErosionRegistry.Blocks.MAGNETITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.HEMATITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.LIMONITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.BISMUTHINITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.SPHALERITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.AZURITE_ORE.get());
        createSimpleOre(provider, ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get());

        createSimpleGravel(provider, ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get());
        createSimpleGravel(provider, ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get());
        createSimpleGravel(provider, ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get());

        createSimpleDirt(provider, ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get());

        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_LIMONITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_HEMATITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_MAGNETITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_MALACHITE.get());

        createSimpleRock(provider, ErosionRegistry.Blocks.NATIVE_GOLD.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.NATIVE_SILVER.get());

        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_BISMUTHINITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_CASSITERITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_SPHALERITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_AZURITE.get());
        createSimpleRock(provider, ErosionRegistry.Blocks.RAW_TETRAHEDRITE.get());

        //MACHINES
        // ============================================= //
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get());
        tag(BlockTags.MINEABLE_WITH_AXE).add(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ErosionRegistry.Blocks.MATERIAL_PURIFIER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ErosionRegistry.Blocks.CRUCIBLE.get());
    }

    public void createSimpleStone(HolderLookup.Provider p, Block b)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "stones"))).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "stone"))).add(b);
        return;
    }

    public void createSimpleOre(HolderLookup.Provider p, Block b)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
        tag(BlockTags.NEEDS_STONE_TOOL).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores"))).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ore"))).add(b);
        return;
    }

    public void createSimpleGravel(HolderLookup.Provider p, Block b)
    {
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "gravels"))).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "gravel"))).add(b);
        return;
    }

    public void createSimpleRock(HolderLookup.Provider p, Block b)
    {
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "rock"))).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "rocks"))).add(b);
        return;
    }

    public void createSimpleDirt(HolderLookup.Provider p, Block b)
    {
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(b);
        tag(BlockTags.DIRT).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirts"))).add(b);
        tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "dirt"))).add(b);
        return;
    }
}