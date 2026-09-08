package co.bracesoftware.erosion.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.erosion.blocks.ErosionRegistry.Blocks;
import co.bracesoftware.erosion.blocks.ErosionRegistry.RawRegistry.IRawRegistry;
import co.bracesoftware.erosion.blocks.crucible.CrucibleBlock;
import co.bracesoftware.erosion.blocks.crucible.CrucibleBlockEntity;
import co.bracesoftware.erosion.blocks.material_purifier.MaterialPurifierBlock;
import co.bracesoftware.erosion.blocks.material_purifier.MaterialPurifierBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ErosionRegistry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Erosion.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Erosion.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE, Erosion.MODID
    );

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB, Erosion.MODID
    );
    // ===================================================== //
    public static class DefaultAlterationPaths
    {
        public static final String ALTERATION_BY_WATER = "Alteration by water";
        public static final String ALTERATION_BY_LAVA = "Alteration by lava";
    }
    // ===================================================== //
    public static class RawRegistry
    {
        public static class IRawRegistry
        {
            private String id = null;
            private String name = null;

            public IRawRegistry(String id, String name)
            {
                this.id = id;
                this.name = name;
            }

            public String getId()
            {
                return this.id;
            }
            public String getName()
            {
                return this.name;
            }
        }

        //raw registry

        //SIMPLE BLOCKS
        public static final IRawRegistry DRIED_DIRT = new IRawRegistry("dried_dirt", "Dried Dirt");

        public static final IRawRegistry KAOLINIZED_GRANITE = new IRawRegistry("kaolinized_granite", "Kaolinized Granite");
        public static final IRawRegistry ALBITIZED_GRANITE = new IRawRegistry("albitized_granite", "Albitized Granite");
        public static final IRawRegistry QUARTZ_GRAVEL = new IRawRegistry("quartz_gravel", "Quartz Gravel");
        public static final IRawRegistry PROPYLITIZED_DIORITE = new IRawRegistry("propylitized_diorite", "Propylitized Diorite");
        public static final IRawRegistry CRACKED_CALCITE = new IRawRegistry("cracked_calcite", "Cracked Calcite");
        
        public static final IRawRegistry LIMONITE_ORE = new IRawRegistry("limonite_ore", "Limonite Ore");
        public static final IRawRegistry HEMATITE_ORE = new IRawRegistry("hematite_ore", "Hematite Ore");
        public static final IRawRegistry MAGNETITE_ORE = new IRawRegistry("magnetite_ore", "Magnetite Ore");
        public static final IRawRegistry CALCITE_MALACHITE_ORE = new IRawRegistry("calcite_malachite_ore", "Calcite Malachite Ore");

        public static final IRawRegistry NATIVE_GOLD_DEPOSIT = new IRawRegistry("native_gold_deposit", "Native Gold Deposit");
        public static final IRawRegistry CASSITERITE_DEPOSIT = new IRawRegistry("cassiterite_deposit", "Cassiterite Deposit");
        public static final IRawRegistry NATIVE_SILVER_DEPOSIT = new IRawRegistry("native_silver_deposit", "Native Silver Deposit");

        public static final IRawRegistry BISMUTHINITE_ORE = new IRawRegistry("bismuthinite_ore", "Bismuthinite Ore");
        public static final IRawRegistry SPHALERITE_ORE = new IRawRegistry("sphalerite_ore", "Sphalerite Ore");

        //ITEMS
        public static final IRawRegistry FELDSPAR_POWDER = new IRawRegistry("feldspar_powder", "Feldspar Powder");
        public static final IRawRegistry FLUX = new IRawRegistry("flux", "Flux");
        public static final IRawRegistry CRUSHED_EGG_SHELL = new IRawRegistry("crushed_egg_shell", "Crushed Egg Shell");
        public static final IRawRegistry RAW_LIMONITE = new IRawRegistry("raw_limonite", "Raw Limonite");
        public static final IRawRegistry RAW_HEMATITE = new IRawRegistry("raw_hematite", "Raw Hematite");
        public static final IRawRegistry RAW_MAGNETITE = new IRawRegistry("raw_magnetite", "Raw Magnetite");
        public static final IRawRegistry RAW_MALACHITE = new IRawRegistry("raw_malachite", "Raw Malachite");

        public static final IRawRegistry NATIVE_GOLD = new IRawRegistry("native_gold", "Native Gold");
        public static final IRawRegistry NATIVE_SILVER = new IRawRegistry("native_silver", "Native Silver");
        public static final IRawRegistry SILVER_CHUNK = new IRawRegistry("silver_chunk", "Silver Chunk");
        public static final IRawRegistry RAW_CASSITERITE = new IRawRegistry("raw_cassiterite", "Raw Cassiterite");
        public static final IRawRegistry TIN_CHUNK = new IRawRegistry("tin_chunk", "Tin Chunk");

        public static final IRawRegistry RAW_BISMUTHINITE = new IRawRegistry("raw_bismuthinite", "Raw Bismuthinite");
        public static final IRawRegistry BISMUTH_CHUNK = new IRawRegistry("bismuth_chunk", "Bismuth Chunk");

        public static final IRawRegistry RAW_SPHALERITE = new IRawRegistry("raw_sphalerite", "Raw Sphalerite");
        public static final IRawRegistry ZINC_CHUNK = new IRawRegistry("zinc_chunk", "Zinc Chunk");

        public static final IRawRegistry MINERAL_RICH_DIRT = new IRawRegistry("mineral_rich_dirt", "Mineral-rich Soil");

        //MACHINES
        public static final IRawRegistry MATERIAL_PURIFIER = new IRawRegistry("material_purifier", "Material Purifier");
        public static final IRawRegistry CRUCIBLE = new IRawRegistry("crucible", "Crucible");

        //DATA ATTACHMENTS
        public static final IRawRegistry RETROGEN_DATA = new IRawRegistry("retrogen_data", "Erosion Retrogen Data");
    }

    public static class DataAttachments
    {
        //nothin yet
        public static Long2ObjectMap<List<String>> RETROGEN_DATA = new Long2ObjectOpenHashMap<>();
    }

    public static class Blocks
    {
        //MACHINES
        public static final DeferredBlock<Block> MATERIAL_PURIFIER = BLOCKS.register(
            RawRegistry.MATERIAL_PURIFIER.getId(), () -> new MaterialPurifierBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        );
        public static final DeferredBlock<Block> CRUCIBLE = BLOCKS.register(
            RawRegistry.CRUCIBLE.getId(), () -> new CrucibleBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        );

        //SIMPLE BLOCKS
        public static final DeferredBlock<Block> DRIED_DIRT = BLOCKS.register(
            ErosionRegistry.RawRegistry.DRIED_DIRT.getId(), () -> new ErosionSimpleBlocks.DirtBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> MINERAL_RICH_DIRT = BLOCKS.register(
            ErosionRegistry.RawRegistry.MINERAL_RICH_DIRT.getId(), () -> new ErosionSimpleBlocks.DirtBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );

        public static final DeferredBlock<Block> PROPYLITIZED_DIORITE = BLOCKS.register(
            ErosionRegistry.RawRegistry.PROPYLITIZED_DIORITE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );

        public static final DeferredBlock<Block> KAOLINIZED_GRANITE = BLOCKS.register(
            RawRegistry.KAOLINIZED_GRANITE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> ALBITIZED_GRANITE = BLOCKS.register(
            RawRegistry.ALBITIZED_GRANITE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> QUARTZ_GRAVEL = BLOCKS.register(
            RawRegistry.QUARTZ_GRAVEL.getId(), () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.GravelBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> CRACKED_CALCITE = BLOCKS.register(
            RawRegistry.CRACKED_CALCITE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        
        public static final DeferredBlock<Block> LIMONITE_ORE = BLOCKS.register(
            RawRegistry.LIMONITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> HEMATITE_ORE = BLOCKS.register(
            RawRegistry.HEMATITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> MAGNETITE_ORE = BLOCKS.register(
            RawRegistry.MAGNETITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> CALCITE_MALACHITE_ORE = BLOCKS.register(
            RawRegistry.CALCITE_MALACHITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> NATIVE_GOLD_DEPOSIT = BLOCKS.register(
            RawRegistry.NATIVE_GOLD_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.DirtBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> NATIVE_SILVER_DEPOSIT = BLOCKS.register(
            RawRegistry.NATIVE_SILVER_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.DirtBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> CASSITERITE_DEPOSIT = BLOCKS.register(
            RawRegistry.CASSITERITE_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.DirtBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> BISMUTHINITE_ORE = BLOCKS.register(
            RawRegistry.BISMUTHINITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> SPHALERITE_ORE = BLOCKS.register(
            RawRegistry.SPHALERITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );

        public static final DeferredBlock<Block> RAW_LIMONITE = BLOCKS.register(
            RawRegistry.RAW_LIMONITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_MAGNETITE = BLOCKS.register(
            RawRegistry.RAW_MAGNETITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_HEMATITE = BLOCKS.register(
            RawRegistry.RAW_HEMATITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_MALACHITE = BLOCKS.register(
            RawRegistry.RAW_MALACHITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> NATIVE_GOLD = BLOCKS.register(
            RawRegistry.NATIVE_GOLD.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> NATIVE_SILVER = BLOCKS.register(
            RawRegistry.NATIVE_SILVER.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_SPHALERITE = BLOCKS.register(
            RawRegistry.RAW_SPHALERITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_BISMUTHINITE = BLOCKS.register(
            RawRegistry.RAW_BISMUTHINITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_CASSITERITE = BLOCKS.register(
            RawRegistry.RAW_CASSITERITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
    }
    public static class Items
    {
        //MACHINES
        public static final DeferredItem<Item> MATERIAL_PURIFIER = ITEMS.register(
            RawRegistry.MATERIAL_PURIFIER.getId(), () -> new BlockItem(Blocks.MATERIAL_PURIFIER.get(), new Item.Properties())
        );
        public static final DeferredItem<Item> CRUCIBLE = ITEMS.register(
            RawRegistry.CRUCIBLE.getId(), () -> new BlockItem(Blocks.CRUCIBLE.get(), new Item.Properties())
        );

        //SIMPLEBLOCKS

        public static final DeferredItem<Item> DRIED_DIRT = ITEMS.register(
            RawRegistry.DRIED_DIRT.getId(), () -> new BlockItem(Blocks.DRIED_DIRT.get(), new Item.Properties())
        );
        public static final DeferredItem<Item> BISMUTHINITE_ORE = ITEMS.register(
            RawRegistry.BISMUTHINITE_ORE.getId(), () -> new BlockItem(
                Blocks.BISMUTHINITE_ORE.get(), new Item.Properties()
            )
        );

        public static final DeferredItem<Item> SPHALERITE_ORE = ITEMS.register(
            RawRegistry.SPHALERITE_ORE.getId(), () -> new BlockItem(
                Blocks.SPHALERITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> MINERAL_RICH_DIRT = ITEMS.register(
            RawRegistry.MINERAL_RICH_DIRT.getId(), () -> new BlockItem(
                Blocks.MINERAL_RICH_DIRT.get(), new Item.Properties()
            )
        );

        public static final DeferredItem<Item> KAOLINIZED_GRANITE = ITEMS.register(
            RawRegistry.KAOLINIZED_GRANITE.getId(), () -> new BlockItem(Blocks.KAOLINIZED_GRANITE.get(), new Item.Properties())
        );
        public static final DeferredItem<Item> ALBITIZED_GRANITE = ITEMS.register(
            RawRegistry.ALBITIZED_GRANITE.getId(), () -> new BlockItem(Blocks.ALBITIZED_GRANITE.get(), new Item.Properties())
        );
        public static final DeferredItem<Item> QUARTZ_GRAVEL = ITEMS.register(
            RawRegistry.QUARTZ_GRAVEL.getId(), () -> new BlockItem(Blocks.QUARTZ_GRAVEL.get(), new Item.Properties())
        );
        public static final DeferredItem<Item> PROPYLITIZED_DIORITE = ITEMS.register(
            RawRegistry.PROPYLITIZED_DIORITE.getId(), () -> new BlockItem(
                Blocks.PROPYLITIZED_DIORITE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> CRACKED_CALCITE = ITEMS.register(
            RawRegistry.CRACKED_CALCITE.getId(), () -> new BlockItem(
                Blocks.CRACKED_CALCITE.get(), new Item.Properties()
            )
        );

        public static final DeferredItem<Item> LIMONITE_ORE = ITEMS.register(
            RawRegistry.LIMONITE_ORE.getId(), () -> new BlockItem(
                Blocks.LIMONITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> HEMATITE_ORE = ITEMS.register(
            RawRegistry.HEMATITE_ORE.getId(), () -> new BlockItem(
                Blocks.HEMATITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> MAGNETITE_ORE = ITEMS.register(
            RawRegistry.MAGNETITE_ORE.getId(), () -> new BlockItem(
                Blocks.MAGNETITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> CALCITE_MALACHITE_ORE = ITEMS.register(
            RawRegistry.CALCITE_MALACHITE_ORE.getId(), () -> new BlockItem(
                Blocks.CALCITE_MALACHITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> NATIVE_GOLD_DEPOSIT = ITEMS.register(
            RawRegistry.NATIVE_GOLD_DEPOSIT.getId(), () -> new BlockItem(
                Blocks.NATIVE_GOLD_DEPOSIT.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> CASSITERITE_DEPOSIT = ITEMS.register(
            RawRegistry.CASSITERITE_DEPOSIT.getId(), () -> new BlockItem(
                Blocks.CASSITERITE_DEPOSIT.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> NATIVE_SILVER_DEPOSIT = ITEMS.register(
            RawRegistry.NATIVE_SILVER_DEPOSIT.getId(), () -> new BlockItem(
                Blocks.NATIVE_SILVER_DEPOSIT.get(), new Item.Properties()
            )
        );
        
        // SIMPLE ITEMS
        public static final DeferredItem<Item> FELDSPAR_POWDER = ITEMS.register(
            RawRegistry.FELDSPAR_POWDER.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final DeferredItem<Item> FLUX = ITEMS.register(
            RawRegistry.FLUX.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final DeferredItem<Item> CRUSHED_EGG_SHELL = ITEMS.register(
            RawRegistry.CRUSHED_EGG_SHELL.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );

        public static final DeferredItem<Item> RAW_LIMONITE = ITEMS.register(
            RawRegistry.RAW_LIMONITE.getId(), () -> new BlockItem(
                Blocks.RAW_LIMONITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_MAGNETITE = ITEMS.register(
            RawRegistry.RAW_MAGNETITE.getId(), () -> new BlockItem(
                Blocks.RAW_MAGNETITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_HEMATITE = ITEMS.register(
            RawRegistry.RAW_HEMATITE.getId(), () -> new BlockItem(
                Blocks.RAW_HEMATITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_MALACHITE = ITEMS.register(
            RawRegistry.RAW_MALACHITE.getId(), () -> new BlockItem(
                Blocks.RAW_MALACHITE.get(), new Item.Properties().stacksTo(32)
            )
        );

        public static final DeferredItem<Item> NATIVE_GOLD = ITEMS.register(
            RawRegistry.NATIVE_GOLD.getId(), () -> new BlockItem(
                Blocks.NATIVE_GOLD.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_CASSITERITE = ITEMS.register(
            RawRegistry.RAW_CASSITERITE.getId(), () -> new BlockItem(
                Blocks.RAW_CASSITERITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> TIN_CHUNK = ITEMS.register(
            RawRegistry.TIN_CHUNK.getId(), () -> new Item(new Item.Properties().stacksTo(32))
        );
        public static final DeferredItem<Item> NATIVE_SILVER = ITEMS.register(
            RawRegistry.NATIVE_SILVER.getId(), () -> new BlockItem(
                Blocks.NATIVE_SILVER.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> SILVER_CHUNK = ITEMS.register(
            RawRegistry.SILVER_CHUNK.getId(), () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final DeferredItem<Item> RAW_BISMUTHINITE = ITEMS.register(
            RawRegistry.RAW_BISMUTHINITE.getId(), () -> new BlockItem(
                Blocks.RAW_BISMUTHINITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> BISMUTH_CHUNK = ITEMS.register(
            RawRegistry.BISMUTH_CHUNK.getId(), () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final DeferredItem<Item> RAW_SPHALERITE = ITEMS.register(
            RawRegistry.RAW_SPHALERITE.getId(), () -> new BlockItem(
                Blocks.RAW_SPHALERITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> ZINC_CHUNK = ITEMS.register(
            RawRegistry.ZINC_CHUNK.getId(), () -> new Item(new Item.Properties().stacksTo(32))
        );
    }

    public class BlockEntities
    {
        //MACHINES
        public static final Supplier<BlockEntityType<MaterialPurifierBlockEntity>> MATERIAL_PURIFIER = BLOCK_ENTITY_TYPES.register(
            RawRegistry.MATERIAL_PURIFIER.getId(), () -> BlockEntityType.Builder.of(
                MaterialPurifierBlockEntity::new, 
                ErosionRegistry.Blocks.MATERIAL_PURIFIER.get()
            ).build(null)
        );
        public static final Supplier<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = BLOCK_ENTITY_TYPES.register(
            RawRegistry.CRUCIBLE.getId(), () -> BlockEntityType.Builder.of(
                CrucibleBlockEntity::new, 
                ErosionRegistry.Blocks.CRUCIBLE.get()
            ).build(null)
        );
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EROSION_TAB = CREATIVE_MODE_TABS.register(
        ErosionConfig.CREATIVE_TAB_NAME, () -> CreativeModeTab.builder()
        .title(Component.translatable(ErosionConfig.CREATIVE_TAB_ID))
        .icon(() -> ErosionRegistry.Items.KAOLINIZED_GRANITE.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            //ALL ITEMS GO HERE
            //SIMPLE BLOCKS
            output.accept(ErosionRegistry.Items.DRIED_DIRT.get());

            output.accept(ErosionRegistry.Items.KAOLINIZED_GRANITE.get());
            output.accept(ErosionRegistry.Items.ALBITIZED_GRANITE.get());
            output.accept(ErosionRegistry.Items.QUARTZ_GRAVEL.get());
            output.accept(ErosionRegistry.Items.PROPYLITIZED_DIORITE.get());
            output.accept(ErosionRegistry.Items.CRACKED_CALCITE.get());

            output.accept(ErosionRegistry.Items.LIMONITE_ORE.get());
            output.accept(ErosionRegistry.Items.CALCITE_MALACHITE_ORE.get());
            output.accept(ErosionRegistry.Items.HEMATITE_ORE.get());
            output.accept(ErosionRegistry.Items.MAGNETITE_ORE.get());

            output.accept(ErosionRegistry.Items.RAW_HEMATITE.get());
            output.accept(ErosionRegistry.Items.RAW_LIMONITE.get());
            output.accept(ErosionRegistry.Items.RAW_MAGNETITE.get());
            output.accept(ErosionRegistry.Items.RAW_MALACHITE.get());

            output.accept(ErosionRegistry.Items.NATIVE_GOLD_DEPOSIT.get());
            output.accept(ErosionRegistry.Items.NATIVE_GOLD.get());
            output.accept(ErosionRegistry.Items.RAW_CASSITERITE.get());
            output.accept(ErosionRegistry.Items.TIN_CHUNK.get());
            output.accept(ErosionRegistry.Items.CASSITERITE_DEPOSIT.get());

            output.accept(ErosionRegistry.Items.NATIVE_SILVER.get());
            output.accept(ErosionRegistry.Items.NATIVE_SILVER_DEPOSIT.get());
            output.accept(ErosionRegistry.Items.SILVER_CHUNK.get());

            output.accept(ErosionRegistry.Items.BISMUTH_CHUNK.get());
            output.accept(ErosionRegistry.Items.BISMUTHINITE_ORE.get());
            output.accept(ErosionRegistry.Items.RAW_BISMUTHINITE.get());

            output.accept(ErosionRegistry.Items.ZINC_CHUNK.get());
            output.accept(ErosionRegistry.Items.SPHALERITE_ORE.get());
            output.accept(ErosionRegistry.Items.RAW_SPHALERITE.get());
            output.accept(ErosionRegistry.Items.MINERAL_RICH_DIRT.get());

            //SIMPLE ITEMS
            output.accept(ErosionRegistry.Items.FLUX.get());
            output.accept(ErosionRegistry.Items.CRUSHED_EGG_SHELL.get());
            output.accept(ErosionRegistry.Items.FELDSPAR_POWDER.get());

            //MACHINES
            output.accept(ErosionRegistry.Items.MATERIAL_PURIFIER.get());
            output.accept(ErosionRegistry.Items.CRUCIBLE.get());
        })
        .build()
    );

    public static void init(IEventBus modEventBus)
    {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);

        try
        {
            Class.forName(Blocks.class.getName());
            Class.forName(Items.class.getName());
            Class.forName(BlockEntities.class.getName());
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}