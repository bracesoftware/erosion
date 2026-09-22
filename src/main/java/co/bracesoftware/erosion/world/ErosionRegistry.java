package co.bracesoftware.erosion.world;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.network.server.ErosionAimedAtBlockPosPacket;
import co.bracesoftware.erosion.network.server.ErosionScreenMessagePacket;
import co.bracesoftware.erosion.network.server.ErosionStatusSyncPacket;
import co.bracesoftware.erosion.world.custom.ErosionCustomEntitySys.GasType;
import co.bracesoftware.erosion.world.items.ErosionSimpleItems;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.*;

import co.bracesoftware.erosion.world.blocks.chemical_reactor.*;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.module.ChemicalReactorModuleBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber.ChemicalReactorScrubberBlock;
import co.bracesoftware.erosion.world.blocks.crucible.*;
import co.bracesoftware.erosion.world.blocks.material_purifier.*;
import co.bracesoftware.erosion.world.ErosionRegistry.RawRegistry;
import co.bracesoftware.erosion.world.ErosionRegistry.RawRegistry.IRawRegistry;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionRegistry
{
    public static class DataPackets
    {
        public static final Type<ErosionStatusSyncPacket> MOD_STATUS_SYNC = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Erosion.MODID, "status_sync")
        );
        public static final Type<ErosionScreenMessagePacket> SCREEN_MESSAGE_PACKET = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Erosion.MODID, "screen_msg_packet")
        );
        public static final Type<ErosionAimedAtBlockPosPacket> AIMED_AT_BLOCK_PACKET = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Erosion.MODID, "aimed_at_block")
        );
    }

///////////////////////////////////////////////////////////////////////////////////////

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Erosion.MODID);
    
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Erosion.MODID);
    
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Erosion.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE, Erosion.MODID
    );

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB, Erosion.MODID
    );
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(
        Registries.ARMOR_MATERIAL, Erosion.MODID
    );

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
        Registries.SOUND_EVENT, Erosion.MODID
    );
    // ===================================================== //
    public static class DefaultAlterationPaths
    {
        public static final String ALTERATION_BY_WATER = "Alteration by water";
        public static final String ALTERATION_BY_LAVA = "Alteration by lava";
        public static final String HYDROTHERMAL_ALTERATION = "Hydrothermal alteration";
        public static final String ALTERATION_BY_HEAT_AND_PRESSURE = "Alteration by heat and lithostatic pressure";
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
        //RETROGEN FEATURES
        public static class ErosionRetrogenFeatures
        {
            public static final IRawRegistry PLACE_ROCKS = new IRawRegistry("place_rocks", "Place rocks around the chunk");
        }

        //SIMPLE BLOCKS
        public static final IRawRegistry DRIED_DIRT = new IRawRegistry("dried_dirt", "Dried Dirt");
        public static final IRawRegistry MINERAL_RICH_DIRT = new IRawRegistry("mineral_rich_dirt", "Mineral-rich Soil");
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

        public static final IRawRegistry AZURITE_ORE = new IRawRegistry("azurite_ore", "Azurite Ore");
        public static final IRawRegistry TETRAHEDRITE_ORE = new IRawRegistry("tetrahedrite_ore", "Tetrahedrite Ore");
        public static final IRawRegistry ARSENOPYRITE_ORE = new IRawRegistry("arsenopyrite_ore", "Arsenopyrite Ore");
        public static final IRawRegistry PYRITE_ORE = new IRawRegistry("pyrite_ore", "Pyrite Ore");

        public static final IRawRegistry RUBY_ORE = new IRawRegistry("ruby_ore", "Ruby Ore");
        public static final IRawRegistry SAPPHIRE_ORE = new IRawRegistry("sapphire_ore", "Sapphire Ore");

        public static final IRawRegistry BORAX_DEPOSIT = new IRawRegistry("borax_deposit", "Borax Deposit");
        public static final IRawRegistry CRACKED_STONE = new IRawRegistry("cracked_stone", "Cracked Stone");

        //ITEMS
        public static final IRawRegistry FELDSPAR_POWDER = new IRawRegistry("feldspar_powder", "Feldspar Powder");
        public static final IRawRegistry FLUX = new IRawRegistry("flux", "Flux");
        public static final IRawRegistry SULFUR_SLAG = new IRawRegistry("sulfur_slag", "Sulfur Slag");
        public static final IRawRegistry ANTIMONY_SLAG = new IRawRegistry("antimony_slag", "Antimony Slag");
        public static final IRawRegistry DEBRIS = new IRawRegistry("debris", "Debris");
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

        public static final IRawRegistry RAW_AZURITE = new IRawRegistry("raw_azurite", "Raw Azurite");
        public static final IRawRegistry RUBY = new IRawRegistry("ruby", "Ruby");
        public static final IRawRegistry SAPPHIRE = new IRawRegistry("sapphire", "Sapphire");
        public static final IRawRegistry RAW_TETRAHEDRITE = new IRawRegistry("raw_tetrahedrite", "Raw Tetrahedrite");
        public static final IRawRegistry RAW_ARSENOPYRITE = new IRawRegistry("raw_arsenopyrite", "Raw Arsenopyrite");
        public static final IRawRegistry RAW_PYRITE = new IRawRegistry("raw_pyrite", "Raw Pyrite");

        public static final IRawRegistry BORAX = new IRawRegistry("borax", "Borax");
        public static final IRawRegistry DEHYDRATED_BORAX = new IRawRegistry("dehydrated_borax", "Dehydrated Borax");

        public static final IRawRegistry BUCKET_OF_SULFURIC_ACID = new IRawRegistry("sulfuric_acid_bucket", "Bucket of Sulfuric Acid");
        public static final IRawRegistry BORIC_ACID_CRYSTAL = new IRawRegistry("boric_acid_crystal", "Boric Acid Crystal");

        public static final IRawRegistry GAS_FILTER = new IRawRegistry("gas_filter", "Gas Filter");

        //MACHINES
        public static final IRawRegistry MATERIAL_PURIFIER = new IRawRegistry("material_purifier", "Material Purifier");
        public static final IRawRegistry CRUCIBLE = new IRawRegistry("crucible", "Crucible");

        public static final IRawRegistry CHEMICAL_REACTOR = new IRawRegistry("chemical_reactor", "Chemical Reactor");
        public static final IRawRegistry CHEMICAL_REACTOR_SCRUBBER = new IRawRegistry("chemical_reactor_scrubber", "Chemical Reactor Scrubber");
        public static final IRawRegistry CHEMICAL_REACTOR_MODULE = new IRawRegistry("chemical_reactor_module", "Chemical Reactor Module");
        public static final IRawRegistry CHEMICAL_REACTOR_COOLING_SYSTEM = new IRawRegistry(
            "chemical_reactor_cooling_system", 
            "Chemical Reactor Cooling System"
        );

        //MANUAL ADVANCEMENTS
        public static class ManualAdvancements
        {
            public static final IRawRegistry INVISIBLE_FIRE = new IRawRegistry(
                "invizible_fire", "Invisible Fire..."
            );
        }

        //CHEMICAL REACTIONS

        public static class ChemicalReactions
        {
            public static final IRawRegistry SULFURIC_ACID_SYNTHESIS = new IRawRegistry(
                "sulfuric_acid_synth", "Sulfuric Acid Synthesis"
            );
            public static final IRawRegistry BORIC_ACID_SYNTHESIS = new IRawRegistry(
                "boric_acid_synth", "Boric Acid Synthesis"
            );
            public static final IRawRegistry DIRT_HYDRATION = new IRawRegistry(
                "dirt_hydration", "Dirt Hydration"
            );
            public static final IRawRegistry ANHYDROUS_BORAX_HYDRATION = new IRawRegistry(
                "borax_hydration", "Anhydrous Borax Hydration"
            );
        }

        public static class SMLModSides
        {
            public static final IRawRegistry CLIENT = new IRawRegistry("client_side", "Erosion Client Side");
            public static final IRawRegistry SERVER = new IRawRegistry("server_side", "Erosion Server Side");
            public static final IRawRegistry COMMON = new IRawRegistry("common", "Erosion Common Side");
        }

        public static class CommandNames
        {
            public static final IRawRegistry MOD_STATUS = new IRawRegistry("status", "STATUS");
            public static final IRawRegistry RELOAD_CONFIG = new IRawRegistry("reload_config", "RELOAD_CONFIG");
            public static final IRawRegistry VIEW_CONFIG = new IRawRegistry("view_config", "VIEW_CONFIG");
            public static final IRawRegistry SET_CONFIG = new IRawRegistry("set_config", "SET_CONFIG");
        }

        //GASES
        public static final IRawRegistry SULFUR_DIOXIDE = new IRawRegistry("sulfur_dioxide", "Sulfur Dioxide");
        public static final IRawRegistry WATER_VAPOR = new IRawRegistry("water_vapor", "Water Vapor");
        public static final IRawRegistry ARSENIC_TRIOXIDE = new IRawRegistry("arsenic_trioxide", "Arsenic Trioxide");

        //COOL ITEMS
        public static final IRawRegistry BASIC_MASK = new IRawRegistry("basic_mask", "Basic Mask");
        public static final IRawRegistry GAS_MASK = new IRawRegistry("gas_mask", "Gas Mask");

        //DATA ATTACHMENTS
        public static final IRawRegistry RETROGEN_DATA = new IRawRegistry("retrogen_data", "Erosion Retrogen Data");

        //SOUND EVENTS
        public static final IRawRegistry ORE_MINE = new IRawRegistry("ore_mine", "Erosion Ore Sound");
        public static final IRawRegistry ORE_PLACE = new IRawRegistry("ore_place", "Erosion Ore Sound");
        public static final IRawRegistry CRUCIBLE_MELTING = new IRawRegistry("crucible_melting", "Crucible Melting");
        public static final IRawRegistry ROCK = new IRawRegistry("rock", "Rock Sound");
    }

    public static class ErosionRenderingElements
    {
        public static final ResourceLocation SCREEN_MESSAGE = ResourceLocation.fromNamespaceAndPath(
            Erosion.MODID, "screen_message"
        );
    }

    public static class ArmorMaterials
    {
        //no materials rn
    }

    public static class DataAttachments
    {
        //nothin yet
        public static Long2ObjectMap<List<String>> RETROGEN_DATA = new Long2ObjectOpenHashMap<>();
    }

    public static class GasTypes
    {
        public static final GasType SULFUR_DIOXIDE = new GasType(
            RawRegistry.SULFUR_DIOXIDE.getId(),
            RawRegistry.SULFUR_DIOXIDE.getName(),
            200, true, 4,
            ParticleTypes.CLOUD, 5,
            List.of(
                MobEffects.CONFUSION,
                MobEffects.POISON
            )
        );
        public static final GasType ARSENIC_TRIOXIDE = new GasType(
            RawRegistry.ARSENIC_TRIOXIDE.getId(),
            RawRegistry.ARSENIC_TRIOXIDE.getName(),
            300, true, 6,
            ParticleTypes.SNEEZE, 5,
            List.of(
                MobEffects.WITHER,
                MobEffects.CONFUSION,
                MobEffects.MOVEMENT_SLOWDOWN
            )
        );
        public static final GasType WATER_VAPOR = new GasType(
            RawRegistry.WATER_VAPOR.getId(),
            RawRegistry.WATER_VAPOR.getName(),
            100, false, 3,
            ParticleTypes.CAMPFIRE_COSY_SMOKE, 5,
            List.of()
        );
    }

    public static class Menus
    {
        public static final DeferredHolder<MenuType<?>, MenuType<ChemicalReactorMenu>> CHEMICAL_REACTOR = MENUS.register(
            RawRegistry.CHEMICAL_REACTOR.getId(), () -> IMenuTypeExtension.create(
                (winid, inv, data) -> new ChemicalReactorMenu(winid, inv, data.readBlockPos())
            )
        );
    }

    public static class SoundEvents
    {
        public static final Supplier<SoundEvent> ORE_MINE = SOUND_EVENTS.register(
            RawRegistry.ORE_MINE.getId(), () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.ORE_MINE.getId()
                )
            )
        );
        public static final Supplier<SoundEvent> ORE_PLACE = SOUND_EVENTS.register(
            RawRegistry.ORE_PLACE.getId(), () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.ORE_PLACE.getId()
                )
            )
        );
        public static final Supplier<SoundEvent> CRUCIBLE_MELTING = SOUND_EVENTS.register(
            RawRegistry.CRUCIBLE_MELTING.getId(), () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.CRUCIBLE_MELTING.getId()
                )
            )
        );
        public static final Supplier<SoundEvent> ROCK = SOUND_EVENTS.register(
            RawRegistry.ROCK.getId(), () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.ROCK.getId()
                )
            )
        );
    }

    public static class SoundTypes
    {
        public static final SoundType ORE = new SoundType(
            1f,1f,
            ErosionRegistry.SoundEvents.ORE_MINE.get(),
            ErosionRegistry.SoundEvents.ORE_PLACE.get(),
            ErosionRegistry.SoundEvents.ORE_PLACE.get(),
            ErosionRegistry.SoundEvents.ORE_MINE.get(),
            ErosionRegistry.SoundEvents.ORE_MINE.get()
        );
        public static final SoundType ROCK = new SoundType(
            1f,1f,
            ErosionRegistry.SoundEvents.ROCK.get(),
            ErosionRegistry.SoundEvents.ROCK.get(),
            ErosionRegistry.SoundEvents.ROCK.get(),
            ErosionRegistry.SoundEvents.ROCK.get(),
            ErosionRegistry.SoundEvents.ROCK.get()
        );
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
        //======================= CHEMICAL REACTOR SYS
        public static final DeferredBlock<Block> CHEMICAL_REACTOR = BLOCKS.register(
            RawRegistry.CHEMICAL_REACTOR.getId(), () -> new ChemicalReactorBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        );
        public static final DeferredBlock<Block> CHEMICAL_REACTOR_SCRUBBER = BLOCKS.register(
            RawRegistry.CHEMICAL_REACTOR_SCRUBBER.getId(), () -> new ChemicalReactorScrubberBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        );
        public static final DeferredBlock<Block> CHEMICAL_REACTOR_MODULE = BLOCKS.register(
            RawRegistry.CHEMICAL_REACTOR_MODULE.getId(), () -> new ChemicalReactorModuleBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        );
        //----------------------------------------------
        //SIMPLE BLOCKS
        public static final DeferredBlock<Block> DRIED_DIRT = BLOCKS.register(
            ErosionRegistry.RawRegistry.DRIED_DIRT.getId(), () -> new ErosionSimpleBlocks.GravelBlock(
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

        public static final DeferredBlock<Block> BORAX_DEPOSIT = BLOCKS.register(
            ErosionRegistry.RawRegistry.BORAX_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );

        public static final DeferredBlock<Block> KAOLINIZED_GRANITE = BLOCKS.register(
            RawRegistry.KAOLINIZED_GRANITE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> CRACKED_STONE = BLOCKS.register(
            RawRegistry.CRACKED_STONE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
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
            RawRegistry.NATIVE_GOLD_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> NATIVE_SILVER_DEPOSIT = BLOCKS.register(
            RawRegistry.NATIVE_SILVER_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> CASSITERITE_DEPOSIT = BLOCKS.register(
            RawRegistry.CASSITERITE_DEPOSIT.getId(), () -> new ErosionSimpleBlocks.GravelBlock(
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
        public static final DeferredBlock<Block> AZURITE_ORE = BLOCKS.register(
            RawRegistry.AZURITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> TETRAHEDRITE_ORE = BLOCKS.register(
            RawRegistry.TETRAHEDRITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> ARSENOPYRITE_ORE = BLOCKS.register(
            RawRegistry.ARSENOPYRITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> PYRITE_ORE = BLOCKS.register(
            RawRegistry.PYRITE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RUBY_ORE = BLOCKS.register(
            RawRegistry.RUBY_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> SAPPHIRE_ORE = BLOCKS.register(
            RawRegistry.SAPPHIRE_ORE.getId(), () -> new ErosionSimpleBlocks.StoneBlock(
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
        public static final DeferredBlock<Block> RAW_AZURITE = BLOCKS.register(
            RawRegistry.RAW_AZURITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_TETRAHEDRITE = BLOCKS.register(
            RawRegistry.RAW_TETRAHEDRITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_ARSENOPYRITE = BLOCKS.register(
            RawRegistry.RAW_ARSENOPYRITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final DeferredBlock<Block> RAW_PYRITE = BLOCKS.register(
            RawRegistry.RAW_PYRITE.getId(), () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
    }
    public static class Items
    {
        //MACHINES
        public static final DeferredItem<Item> MATERIAL_PURIFIER = ITEMS.register(
            RawRegistry.MATERIAL_PURIFIER.getId(), () -> new BlockItem(
                Blocks.MATERIAL_PURIFIER.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> CRUCIBLE = ITEMS.register(
            RawRegistry.CRUCIBLE.getId(), () -> new BlockItem(
                Blocks.CRUCIBLE.get(), new Item.Properties()
            )
        );
        //------------------------------------------------------
        public static final DeferredItem<Item> CHEMICAL_REACTOR = ITEMS.register(
            RawRegistry.CHEMICAL_REACTOR.getId(), () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> CHEMICAL_REACTOR_SCRUBBER = ITEMS.register(
            RawRegistry.CHEMICAL_REACTOR_SCRUBBER.getId(), () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR_SCRUBBER.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> CHEMICAL_REACTOR_MODULE = ITEMS.register(
            RawRegistry.CHEMICAL_REACTOR_MODULE.getId(), () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR_MODULE.get(), new Item.Properties()
            )
        );
        //-------------------------------------------------------
        //COOL ITEMS
        public static final DeferredItem<Item> GAS_MASK = ErosionSimpleItems.GasMask.newGasMaskItem(
            RawRegistry.GAS_MASK.getId(), ErosionSimpleItems.GasMask.Quality.HIGH
        );

        public static final DeferredItem<Item> BASIC_MASK = ErosionSimpleItems.GasMask.newGasMaskItem(
            RawRegistry.BASIC_MASK.getId(), ErosionSimpleItems.GasMask.Quality.LOW
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
        public static final DeferredItem<Item> CRACKED_STONE = ITEMS.register(
            RawRegistry.CRACKED_STONE.getId(), () -> new BlockItem(Blocks.CRACKED_STONE.get(), new Item.Properties())
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

        public static final DeferredItem<Item> BORAX_DEPOSIT = ITEMS.register(
            RawRegistry.BORAX_DEPOSIT.getId(), () -> new BlockItem(
                Blocks.BORAX_DEPOSIT.get(), new Item.Properties()
            )
        );

        public static final DeferredItem<Item> MAGNETITE_ORE = ITEMS.register(
            RawRegistry.MAGNETITE_ORE.getId(), () -> new BlockItem(
                Blocks.MAGNETITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> AZURITE_ORE = ITEMS.register(
            RawRegistry.AZURITE_ORE.getId(), () -> new BlockItem(
                Blocks.AZURITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> TETRAHEDRITE_ORE = ITEMS.register(
            RawRegistry.TETRAHEDRITE_ORE.getId(), () -> new BlockItem(
                Blocks.TETRAHEDRITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> ARSENOPYRITE_ORE = ITEMS.register(
            RawRegistry.ARSENOPYRITE_ORE.getId(), () -> new BlockItem(
                Blocks.ARSENOPYRITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> PYRITE_ORE = ITEMS.register(
            RawRegistry.PYRITE_ORE.getId(), () -> new BlockItem(
                Blocks.PYRITE_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> RUBY_ORE = ITEMS.register(
            RawRegistry.RUBY_ORE.getId(), () -> new BlockItem(
                Blocks.RUBY_ORE.get(), new Item.Properties()
            )
        );
        public static final DeferredItem<Item> SAPPHIRE_ORE = ITEMS.register(
            RawRegistry.SAPPHIRE_ORE.getId(), () -> new BlockItem(
                Blocks.SAPPHIRE_ORE.get(), new Item.Properties()
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
        public static final DeferredItem<Item> GAS_FILTER = ITEMS.register(
            RawRegistry.GAS_FILTER.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final DeferredItem<Item> RUBY = ITEMS.register(
            RawRegistry.RUBY.getId(), () -> new Item(new Item.Properties().stacksTo(64))
        );
        public static final DeferredItem<Item> SAPPHIRE = ITEMS.register(
            RawRegistry.SAPPHIRE.getId(), () -> new Item(new Item.Properties().stacksTo(64))
        );
        public static final DeferredItem<Item> SULFUR_SLAG = ITEMS.register(
            RawRegistry.SULFUR_SLAG.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final DeferredItem<Item> ANTIMONY_SLAG = ITEMS.register(
            RawRegistry.ANTIMONY_SLAG.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final DeferredItem<Item> DEBRIS = ITEMS.register(
            RawRegistry.DEBRIS.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final DeferredItem<Item> CRUSHED_EGG_SHELL = ITEMS.register(
            RawRegistry.CRUSHED_EGG_SHELL.getId(), () -> new Item(new Item.Properties().stacksTo(16))
        );

        public static final DeferredItem<Item> BORAX = ITEMS.register(
            RawRegistry.BORAX.getId(), () -> new Item(new Item.Properties().stacksTo(32))
        );
        public static final DeferredItem<Item> DEHYDRATED_BORAX = ITEMS.register(
            RawRegistry.DEHYDRATED_BORAX.getId(), () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final DeferredItem<Item> BUCKET_OF_SULFURIC_ACID = ITEMS.register(
            RawRegistry.BUCKET_OF_SULFURIC_ACID.getId(), () -> new Item(new Item.Properties().stacksTo(1))
        );

        public static final DeferredItem<Item> BORIC_ACID_CRYSTAL = ITEMS.register(
            RawRegistry.BORIC_ACID_CRYSTAL.getId(), () -> new Item(new Item.Properties().stacksTo(32))
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

        public static final DeferredItem<Item> RAW_AZURITE = ITEMS.register(
            RawRegistry.RAW_AZURITE.getId(), () -> new BlockItem(
                Blocks.RAW_AZURITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_TETRAHEDRITE = ITEMS.register(
            RawRegistry.RAW_TETRAHEDRITE.getId(), () -> new BlockItem(
                Blocks.RAW_TETRAHEDRITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_ARSENOPYRITE = ITEMS.register(
            RawRegistry.RAW_ARSENOPYRITE.getId(), () -> new BlockItem(
                Blocks.RAW_ARSENOPYRITE.get(), new Item.Properties().stacksTo(32)
            )
        );
        public static final DeferredItem<Item> RAW_PYRITE = ITEMS.register(
            RawRegistry.RAW_PYRITE.getId(), () -> new BlockItem(
                Blocks.RAW_PYRITE.get(), new Item.Properties().stacksTo(32)
            )
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
        public static final Supplier<BlockEntityType<ChemicalReactorBlockEntity>> CHEMICAL_REACTOR = BLOCK_ENTITY_TYPES.register(
            RawRegistry.CHEMICAL_REACTOR.getId(), () -> BlockEntityType.Builder.of(
                ChemicalReactorBlockEntity::new,
                ErosionRegistry.Blocks.CHEMICAL_REACTOR.get()
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
            output.accept(ErosionRegistry.Items.CRACKED_STONE.get());
            output.accept(ErosionRegistry.Items.ALBITIZED_GRANITE.get());
            output.accept(ErosionRegistry.Items.QUARTZ_GRAVEL.get());
            output.accept(ErosionRegistry.Items.PROPYLITIZED_DIORITE.get());
            output.accept(ErosionRegistry.Items.CRACKED_CALCITE.get());

            output.accept(ErosionRegistry.Items.LIMONITE_ORE.get());
            output.accept(ErosionRegistry.Items.CALCITE_MALACHITE_ORE.get());
            output.accept(ErosionRegistry.Items.HEMATITE_ORE.get());
            output.accept(ErosionRegistry.Items.BORAX_DEPOSIT.get());
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

            output.accept(ErosionRegistry.Items.RAW_AZURITE.get());
            output.accept(ErosionRegistry.Items.AZURITE_ORE.get());
            output.accept(ErosionRegistry.Items.TETRAHEDRITE_ORE.get());
            output.accept(ErosionRegistry.Items.ARSENOPYRITE_ORE.get());
            output.accept(ErosionRegistry.Items.PYRITE_ORE.get());
            output.accept(ErosionRegistry.Items.RAW_TETRAHEDRITE.get());
            output.accept(ErosionRegistry.Items.RAW_ARSENOPYRITE.get());
            output.accept(ErosionRegistry.Items.RAW_PYRITE.get());
            output.accept(ErosionRegistry.Items.RUBY_ORE.get());
            output.accept(ErosionRegistry.Items.SAPPHIRE_ORE.get());

            //SIMPLE ITEMS
            output.accept(ErosionRegistry.Items.FLUX.get());
            output.accept(ErosionRegistry.Items.RUBY.get());
            output.accept(ErosionRegistry.Items.BORAX.get());
            output.accept(ErosionRegistry.Items.DEHYDRATED_BORAX.get());
            output.accept(ErosionRegistry.Items.BUCKET_OF_SULFURIC_ACID.get());
            output.accept(ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get());
            output.accept(ErosionRegistry.Items.SAPPHIRE.get());
            output.accept(ErosionRegistry.Items.SULFUR_SLAG.get());
            output.accept(ErosionRegistry.Items.ANTIMONY_SLAG.get());
            output.accept(ErosionRegistry.Items.DEBRIS.get());
            output.accept(ErosionRegistry.Items.CRUSHED_EGG_SHELL.get());
            output.accept(ErosionRegistry.Items.FELDSPAR_POWDER.get());

            //MACHINES
            output.accept(ErosionRegistry.Items.MATERIAL_PURIFIER.get());
            output.accept(ErosionRegistry.Items.CRUCIBLE.get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR.get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR_SCRUBBER.get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR_MODULE.get());
            output.accept(ErosionRegistry.Items.BASIC_MASK.get());
            output.accept(ErosionRegistry.Items.GAS_MASK.get());
            output.accept(ErosionRegistry.Items.GAS_FILTER.get());
        })
        .build()
    );

    public static void init(IEventBus modEventBus)
    {
        ErosionRegistry.ARMOR_MATERIALS.register(modEventBus);
        ErosionRegistry.BLOCKS.register(modEventBus);
        ErosionRegistry.ITEMS.register(modEventBus);
        ErosionRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        ErosionRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        ErosionRegistry.MENUS.register(modEventBus);
        ErosionRegistry.SOUND_EVENTS.register(modEventBus);

        try
        {
            Class.forName(ErosionRegistry.Blocks.class.getName());
            Class.forName(ErosionRegistry.Items.class.getName());
            Class.forName(ErosionRegistry.BlockEntities.class.getName());
            Class.forName(ErosionRegistry.Menus.class.getName());
            Class.forName(ErosionRegistry.ArmorMaterials.class.getName());
            Class.forName(ErosionRegistry.SoundEvents.class.getName());
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent 
    public static void setup(FMLClientSetupEvent e)
    {
        ErosionUtils.Log("We're in the registry.");
    }
}