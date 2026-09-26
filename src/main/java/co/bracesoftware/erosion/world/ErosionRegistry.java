package co.bracesoftware.erosion.world;

import java.util.List;
import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.network.server.ErosionAimedAtBlockPosPacket;
import co.bracesoftware.erosion.network.server.ErosionScreenMessagePacket;
import co.bracesoftware.erosion.network.server.ErosionStatusSyncPacket;
import co.bracesoftware.erosion.world.custom.ErosionCustomEntitySys.GasType;
import co.bracesoftware.erosion.world.items.ErosionSimpleItems;
import co.bracesoftware.erosion.world.recipes.ErosionFoodSaltingRecipe;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.*;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.*;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorItem;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.cooling_system.ChemicalReactorCoolingSystemBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.module.ChemicalReactorModuleBlock;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.scrubber.ChemicalReactorScrubberBlock;
import co.bracesoftware.erosion.world.blocks.crucible.*;
import co.bracesoftware.erosion.world.blocks.material_purifier.*;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContent;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContentResourceLocation;
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

    // ===================================================== //
    public static class DefaultAlterationPaths
    {
        public static final String ALTERATION_BY_WATER = "Alteration by water";
        public static final String ALTERATION_BY_LAVA = "Alteration by lava";
        public static final String HYDROTHERMAL_ALTERATION = "Hydrothermal alteration";
        public static final String ALTERATION_BY_HEAT_AND_PRESSURE = "Alteration by heat and lithostatic pressure";
        public static final String ALTERATION_BY_AIR_EXPOSURE = "Alteration by being exposed to air";
    }
    // ===================================================== //
    public static class RawRegistry
    {
        //raw registry
        //RETROGEN FEATURES
        public static class ErosionRetrogenFeatures
        {
            public static final ErosionModContentResourceLocation PLACE_ROCKS = new ErosionModContentResourceLocation("place_rocks", "Place rocks around the chunk");
        }

        //SIMPLE BLOCKS
        public static final ErosionModContentResourceLocation DRIED_DIRT = new ErosionModContentResourceLocation("dried_dirt", "Dried Dirt");
        public static final ErosionModContentResourceLocation MINERAL_RICH_DIRT = new ErosionModContentResourceLocation("mineral_rich_dirt", "Mineral-rich Soil");
        public static final ErosionModContentResourceLocation KAOLINIZED_GRANITE = new ErosionModContentResourceLocation("kaolinized_granite", "Kaolinized Granite");
        public static final ErosionModContentResourceLocation ALBITIZED_GRANITE = new ErosionModContentResourceLocation("albitized_granite", "Albitized Granite");
        public static final ErosionModContentResourceLocation QUARTZ_GRAVEL = new ErosionModContentResourceLocation("quartz_gravel", "Quartz Gravel");
        public static final ErosionModContentResourceLocation PROPYLITIZED_DIORITE = new ErosionModContentResourceLocation("propylitized_diorite", "Propylitized Diorite");
        public static final ErosionModContentResourceLocation CRACKED_CALCITE = new ErosionModContentResourceLocation("cracked_calcite", "Cracked Calcite");
        
        public static final ErosionModContentResourceLocation LIMONITE_ORE = new ErosionModContentResourceLocation("limonite_ore", "Limonite Ore");
        public static final ErosionModContentResourceLocation HEMATITE_ORE = new ErosionModContentResourceLocation("hematite_ore", "Hematite Ore");
        public static final ErosionModContentResourceLocation MAGNETITE_ORE = new ErosionModContentResourceLocation("magnetite_ore", "Magnetite Ore");
        public static final ErosionModContentResourceLocation CALCITE_MALACHITE_ORE = new ErosionModContentResourceLocation("calcite_malachite_ore", "Calcite Malachite Ore");

        public static final ErosionModContentResourceLocation NATIVE_GOLD_DEPOSIT = new ErosionModContentResourceLocation("native_gold_deposit", "Native Gold Deposit");
        public static final ErosionModContentResourceLocation CASSITERITE_DEPOSIT = new ErosionModContentResourceLocation("cassiterite_deposit", "Cassiterite Deposit");
        public static final ErosionModContentResourceLocation NATIVE_SILVER_DEPOSIT = new ErosionModContentResourceLocation("native_silver_deposit", "Native Silver Deposit");

        public static final ErosionModContentResourceLocation BISMUTHINITE_ORE = new ErosionModContentResourceLocation("bismuthinite_ore", "Bismuthinite Ore");
        public static final ErosionModContentResourceLocation SPHALERITE_ORE = new ErosionModContentResourceLocation("sphalerite_ore", "Sphalerite Ore");

        public static final ErosionModContentResourceLocation AZURITE_ORE = new ErosionModContentResourceLocation("azurite_ore", "Azurite Ore");
        public static final ErosionModContentResourceLocation GOETHITE_ORE = new ErosionModContentResourceLocation("dehydrated_limonite_ore", "Dehydrated Limonite Ore");
        public static final ErosionModContentResourceLocation TETRAHEDRITE_ORE = new ErosionModContentResourceLocation("tetrahedrite_ore", "Tetrahedrite Ore");
        public static final ErosionModContentResourceLocation ARSENOPYRITE_ORE = new ErosionModContentResourceLocation("arsenopyrite_ore", "Arsenopyrite Ore");
        public static final ErosionModContentResourceLocation PYRITE_ORE = new ErosionModContentResourceLocation("pyrite_ore", "Pyrite Ore");
        public static final ErosionModContentResourceLocation ANGLESITE_ORE = new ErosionModContentResourceLocation("anglesite_ore", "Anglesite Ore");
        public static final ErosionModContentResourceLocation GALENA_ORE = new ErosionModContentResourceLocation("galena_ore", "Galena Ore");
        public static final ErosionModContentResourceLocation HALITE_ORE = new ErosionModContentResourceLocation("halite_ore", "Halite Ore");

        public static final ErosionModContentResourceLocation RUBY_ORE = new ErosionModContentResourceLocation("ruby_ore", "Ruby Ore");
        public static final ErosionModContentResourceLocation SAPPHIRE_ORE = new ErosionModContentResourceLocation("sapphire_ore", "Sapphire Ore");

        public static final ErosionModContentResourceLocation BORAX_DEPOSIT = new ErosionModContentResourceLocation("borax_deposit", "Borax Deposit");
        public static final ErosionModContentResourceLocation CRACKED_STONE = new ErosionModContentResourceLocation("cracked_stone", "Cracked Stone");

        //ITEMS
        public static final ErosionModContentResourceLocation FELDSPAR_POWDER = new ErosionModContentResourceLocation("feldspar_powder", "Feldspar Powder");
        public static final ErosionModContentResourceLocation FLUX = new ErosionModContentResourceLocation("flux", "Flux");
        public static final ErosionModContentResourceLocation SALT = new ErosionModContentResourceLocation("salt", "Salt");

        public static final ErosionModContentResourceLocation SULFUR_SLAG = new ErosionModContentResourceLocation("sulfur_slag", "Sulfur Slag");
        public static final ErosionModContentResourceLocation ANTIMONY_SLAG = new ErosionModContentResourceLocation("antimony_slag", "Antimony Slag");
        public static final ErosionModContentResourceLocation DEBRIS = new ErosionModContentResourceLocation("debris", "Debris");
        public static final ErosionModContentResourceLocation CRUSHED_EGG_SHELL = new ErosionModContentResourceLocation("crushed_egg_shell", "Crushed Egg Shell");
        public static final ErosionModContentResourceLocation RAW_LIMONITE = new ErosionModContentResourceLocation("raw_limonite", "Raw Limonite");
        public static final ErosionModContentResourceLocation RAW_HEMATITE = new ErosionModContentResourceLocation("raw_hematite", "Raw Hematite");
        public static final ErosionModContentResourceLocation RAW_MAGNETITE = new ErosionModContentResourceLocation("raw_magnetite", "Raw Magnetite");
        public static final ErosionModContentResourceLocation RAW_MALACHITE = new ErosionModContentResourceLocation("raw_malachite", "Raw Malachite");

        public static final ErosionModContentResourceLocation NATIVE_GOLD = new ErosionModContentResourceLocation("native_gold", "Native Gold");
        public static final ErosionModContentResourceLocation NATIVE_SILVER = new ErosionModContentResourceLocation("native_silver", "Native Silver");
        public static final ErosionModContentResourceLocation SILVER_CHUNK = new ErosionModContentResourceLocation("silver_chunk", "Silver Chunk");
        public static final ErosionModContentResourceLocation RAW_CASSITERITE = new ErosionModContentResourceLocation("raw_cassiterite", "Raw Cassiterite");
        public static final ErosionModContentResourceLocation TIN_CHUNK = new ErosionModContentResourceLocation("tin_chunk", "Tin Chunk");
        public static final ErosionModContentResourceLocation LEAD_CHUNK = new ErosionModContentResourceLocation("lead_chunk", "Lead Chunk");

        public static final ErosionModContentResourceLocation RAW_BISMUTHINITE = new ErosionModContentResourceLocation("raw_bismuthinite", "Raw Bismuthinite");
        public static final ErosionModContentResourceLocation BISMUTH_CHUNK = new ErosionModContentResourceLocation("bismuth_chunk", "Bismuth Chunk");

        public static final ErosionModContentResourceLocation RAW_SPHALERITE = new ErosionModContentResourceLocation("raw_sphalerite", "Raw Sphalerite");
        public static final ErosionModContentResourceLocation ZINC_CHUNK = new ErosionModContentResourceLocation("zinc_chunk", "Zinc Chunk");

        public static final ErosionModContentResourceLocation RAW_AZURITE = new ErosionModContentResourceLocation("raw_azurite", "Raw Azurite");
        public static final ErosionModContentResourceLocation RAW_GOETHITE = new ErosionModContentResourceLocation("raw_dehydrated_limonite", "Raw Dehydrated Limonite");
        public static final ErosionModContentResourceLocation RUBY = new ErosionModContentResourceLocation("ruby", "Ruby");
        public static final ErosionModContentResourceLocation SAPPHIRE = new ErosionModContentResourceLocation("sapphire", "Sapphire");
        public static final ErosionModContentResourceLocation RAW_TETRAHEDRITE = new ErosionModContentResourceLocation("raw_tetrahedrite", "Raw Tetrahedrite");
        public static final ErosionModContentResourceLocation RAW_ARSENOPYRITE = new ErosionModContentResourceLocation("raw_arsenopyrite", "Raw Arsenopyrite");
        public static final ErosionModContentResourceLocation RAW_PYRITE = new ErosionModContentResourceLocation("raw_pyrite", "Raw Pyrite");
        public static final ErosionModContentResourceLocation RAW_ANGLESITE = new ErosionModContentResourceLocation("raw_anglesite", "Raw Anglesite");
        public static final ErosionModContentResourceLocation RAW_GALENA = new ErosionModContentResourceLocation("raw_galena", "Raw Galena");
        public static final ErosionModContentResourceLocation RAW_HALITE = new ErosionModContentResourceLocation("raw_halite", "Raw Halite");

        public static final ErosionModContentResourceLocation BORAX = new ErosionModContentResourceLocation("borax", "Borax");
        public static final ErosionModContentResourceLocation DEHYDRATED_BORAX = new ErosionModContentResourceLocation("dehydrated_borax", "Dehydrated Borax");

        public static final ErosionModContentResourceLocation BUCKET_OF_SULFURIC_ACID = new ErosionModContentResourceLocation("sulfuric_acid_bucket", "Bucket of Sulfuric Acid");
        public static final ErosionModContentResourceLocation BORIC_ACID_CRYSTAL = new ErosionModContentResourceLocation("boric_acid_crystal", "Boric Acid Crystal");

        public static final ErosionModContentResourceLocation GAS_FILTER = new ErosionModContentResourceLocation("gas_filter", "Gas Filter");

        //MACHINES
        public static final ErosionModContentResourceLocation MATERIAL_PURIFIER = new ErosionModContentResourceLocation("material_purifier", "Material Purifier");
        public static final ErosionModContentResourceLocation CRUCIBLE = new ErosionModContentResourceLocation("crucible", "Crucible");

        public static final ErosionModContentResourceLocation CHEMICAL_REACTOR = new ErosionModContentResourceLocation("chemical_reactor", "Chemical Reactor");
        public static final ErosionModContentResourceLocation CHEMICAL_REACTOR_SCRUBBER = new ErosionModContentResourceLocation("chemical_reactor_scrubber", "Chemical Reactor Scrubber");
        public static final ErosionModContentResourceLocation CHEMICAL_REACTOR_MODULE = new ErosionModContentResourceLocation("chemical_reactor_module", "Chemical Reactor Module");
        public static final ErosionModContentResourceLocation CHEMICAL_REACTOR_COOLING_SYSTEM = new ErosionModContentResourceLocation(
            "chemical_reactor_cooling_system",
            "Chemical Reactor Cooling System"
        );

        //MANUAL ADVANCEMENTS
        public static class ManualAdvancements
        {
            public static final ErosionModContentResourceLocation INVISIBLE_FIRE = new ErosionModContentResourceLocation(
                "invizible_fire", "Invisible Fire..."
            );
        }

        //CHEMICAL REACTIONS

        public static class ChemicalReactions
        {
            public static final ErosionModContentResourceLocation SULFURIC_ACID_SYNTHESIS = new ErosionModContentResourceLocation(
                "sulfuric_acid_synth", "Sulfuric Acid Synthesis"
            );
            public static final ErosionModContentResourceLocation BORIC_ACID_SYNTHESIS = new ErosionModContentResourceLocation(
                "boric_acid_synth", "Boric Acid Synthesis"
            );
            public static final ErosionModContentResourceLocation DIRT_HYDRATION = new ErosionModContentResourceLocation(
                "dirt_hydration", "Dirt Hydration"
            );
            public static final ErosionModContentResourceLocation ANHYDROUS_BORAX_HYDRATION = new ErosionModContentResourceLocation(
                "borax_hydration", "Anhydrous Borax Hydration"
            );
        }

        public static class SMLModSides
        {
            public static final ErosionModContentResourceLocation CLIENT = new ErosionModContentResourceLocation("client_side", "Erosion Client Side");
            public static final ErosionModContentResourceLocation SERVER = new ErosionModContentResourceLocation("server_side", "Erosion Server Side");
            public static final ErosionModContentResourceLocation COMMON = new ErosionModContentResourceLocation("common", "Erosion Common Side");
        }

        public static class CommandNames
        {
            public static final ErosionModContentResourceLocation MOD_STATUS = new ErosionModContentResourceLocation("status", "STATUS");
            public static final ErosionModContentResourceLocation RELOAD_CONFIG = new ErosionModContentResourceLocation("reload_config", "RELOAD_CONFIG");
            public static final ErosionModContentResourceLocation VIEW_CONFIG = new ErosionModContentResourceLocation("view_config", "VIEW_CONFIG");
            public static final ErosionModContentResourceLocation SET_CONFIG = new ErosionModContentResourceLocation("set_config", "SET_CONFIG");
        }

        //GASES
        public static final ErosionModContentResourceLocation SULFUR_DIOXIDE = new ErosionModContentResourceLocation("sulfur_dioxide", "Sulfur Dioxide");
        public static final ErosionModContentResourceLocation WATER_VAPOR = new ErosionModContentResourceLocation("water_vapor", "Water Vapor");
        public static final ErosionModContentResourceLocation ARSENIC_TRIOXIDE = new ErosionModContentResourceLocation("arsenic_trioxide", "Arsenic Trioxide");
        public static final ErosionModContentResourceLocation LEAD_MONOXIDE = new ErosionModContentResourceLocation("lead_monoxide", "Lead Monoxide");

        //COOL ITEMS
        public static final ErosionModContentResourceLocation BASIC_MASK = new ErosionModContentResourceLocation("basic_mask", "Basic Mask");
        public static final ErosionModContentResourceLocation GAS_MASK = new ErosionModContentResourceLocation("gas_mask", "Gas Mask");

        //DATA ATTACHMENTS
        public static final ErosionModContentResourceLocation RETROGEN_DATA = new ErosionModContentResourceLocation("retrogen_data", "Erosion Retrogen Data");

        //DATA COMPONENTS
        public static final ErosionModContentResourceLocation IS_SALTED_FOOD = new ErosionModContentResourceLocation("is_salted_food", "Is Salted Food?");
        public static final ErosionModContentResourceLocation IS_SALTED_FOOD_SERIALIZER = new ErosionModContentResourceLocation("isf_serializer", "Is Salted Food Serializer");

        //SOUND EVENTS
        public static final ErosionModContentResourceLocation ORE_MINE = new ErosionModContentResourceLocation("ore_mine", "Erosion Ore Sound");
        public static final ErosionModContentResourceLocation ORE_PLACE = new ErosionModContentResourceLocation("ore_place", "Erosion Ore Sound");
        public static final ErosionModContentResourceLocation CRUCIBLE_MELTING = new ErosionModContentResourceLocation("crucible_melting", "Crucible Melting");
        public static final ErosionModContentResourceLocation ROCK = new ErosionModContentResourceLocation("rock", "Rock Sound");
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

    public static final class DataComponents
    {
        public static final ErosionModContent.ErosionBooleanDataComponent IS_SALTED_FOOD = new ErosionModContent
        .ErosionBooleanDataComponent(
            RawRegistry.IS_SALTED_FOOD
        );

        public static final ErosionModContent.ErosionSerializer<ErosionFoodSaltingRecipe> IS_SALTED_FOOD_SERIALIZER = new ErosionModContent
        .ErosionSerializer<>(
            RawRegistry.IS_SALTED_FOOD_SERIALIZER, () -> new SimpleCraftingRecipeSerializer<>(ErosionFoodSaltingRecipe::new)
        );
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
        public static final GasType LEAD_MONOXIDE = new GasType(
            RawRegistry.LEAD_MONOXIDE.getId(),
            RawRegistry.LEAD_MONOXIDE.getName(),
            356, true, 5,
            ParticleTypes.CRIMSON_SPORE, 15,
            List.of(
                MobEffects.WITHER,
                MobEffects.CONFUSION,
                MobEffects.MOVEMENT_SLOWDOWN,
                MobEffects.DIG_SLOWDOWN,
                MobEffects.POISON,
                MobEffects.OOZING
            )
        );
    }

    public static class Menus
    {
        public static final ErosionModContent.ErosionMenu<ChemicalReactorMenu> CHEMICAL_REACTOR = new ErosionModContent
        .ErosionMenu<>(
            RawRegistry.CHEMICAL_REACTOR, () -> IMenuTypeExtension.create(
                (w,i,d) -> new ChemicalReactorMenu(w,i,d.readBlockPos())
            )
        );
    }

    public static class SoundEvents
    {
        public static final ErosionModContent.ErosionSound ORE_MINE = new ErosionModContent.ErosionSound(
            RawRegistry.ORE_MINE, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.ORE_MINE.getId()
                )
            )
        );
        
        public static final ErosionModContent.ErosionSound ORE_PLACE = new ErosionModContent.ErosionSound(
            RawRegistry.ORE_PLACE, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.ORE_PLACE.getId()
                )
            )
        );

        public static final ErosionModContent.ErosionSound CRUCIBLE_MELTING = new ErosionModContent.ErosionSound(
            RawRegistry.CRUCIBLE_MELTING, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.CRUCIBLE_MELTING.getId()
                )
            )
        );

        public static final ErosionModContent.ErosionSound ROCK = new ErosionModContent.ErosionSound(
            RawRegistry.ROCK, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    Erosion.MODID, RawRegistry.ROCK.getId()
                )
            )
        );
    }

    @SuppressWarnings("all")
    public static class SoundTypes
    {
        public static final SoundType ORE = new DeferredSoundType(
            1f,1f,
            ErosionRegistry.SoundEvents.ORE_MINE,
            ErosionRegistry.SoundEvents.ORE_PLACE,
            ErosionRegistry.SoundEvents.ORE_PLACE,
            ErosionRegistry.SoundEvents.ORE_MINE,
            ErosionRegistry.SoundEvents.ORE_MINE
        );
        public static final SoundType ROCK = new DeferredSoundType(
            1f,1f,
            ErosionRegistry.SoundEvents.ROCK,
            ErosionRegistry.SoundEvents.ROCK,
            ErosionRegistry.SoundEvents.ROCK,
            ErosionRegistry.SoundEvents.ROCK,
            ErosionRegistry.SoundEvents.ROCK
        );
    }

    public static class Blocks
    {
        //MACHINES
        public static final ErosionModContent.ErosionBlock MATERIAL_PURIFIER = new ErosionModContent.ErosionBlock(
            RawRegistry.MATERIAL_PURIFIER, () -> new MaterialPurifierBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        ); static {
            MATERIAL_PURIFIER.ErosionModContentBuilder()
            .addKnownBlock(MATERIAL_PURIFIER);
        }
        public static final ErosionModContent.ErosionBlock CRUCIBLE = new ErosionModContent.ErosionBlock(
            RawRegistry.CRUCIBLE, () -> new CrucibleBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        ); static {
            CRUCIBLE.ErosionModContentBuilder()
            .addKnownBlock(CRUCIBLE);
        }
        //======================= CHEMICAL REACTOR SYS
        public static final ErosionModContent.ErosionBlock CHEMICAL_REACTOR = new ErosionModContent.ErosionBlock(
            RawRegistry.CHEMICAL_REACTOR, () -> new ChemicalReactorBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        ); static {
            CHEMICAL_REACTOR.ErosionModContentBuilder()
            .addKnownBlock(CHEMICAL_REACTOR);
        }
        public static final ErosionModContent.ErosionBlock CHEMICAL_REACTOR_SCRUBBER = new ErosionModContent.ErosionBlock(
            RawRegistry.CHEMICAL_REACTOR_SCRUBBER, () -> new ChemicalReactorScrubberBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        ); static {
            CHEMICAL_REACTOR_SCRUBBER.ErosionModContentBuilder()
            .addKnownBlock(CHEMICAL_REACTOR_SCRUBBER);
        }
        public static final ErosionModContent.ErosionBlock CHEMICAL_REACTOR_MODULE = new ErosionModContent.ErosionBlock(
            RawRegistry.CHEMICAL_REACTOR_MODULE, () -> new ChemicalReactorModuleBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        ); static {
            CHEMICAL_REACTOR_MODULE.ErosionModContentBuilder()
            .addKnownBlock(CHEMICAL_REACTOR_MODULE);
        }
        public static final ErosionModContent.ErosionBlock CHEMICAL_REACTOR_COOLING_SYSTEM = new ErosionModContent.ErosionBlock(
            RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM, () -> new ChemicalReactorCoolingSystemBlock(
                BlockBehaviour.Properties.of().strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
            )
        ); static {
            CHEMICAL_REACTOR_COOLING_SYSTEM.ErosionModContentBuilder()
            .addKnownBlock(CHEMICAL_REACTOR_COOLING_SYSTEM);
        }
        //----------------------------------------------
        //SIMPLE BLOCKS
        public static final ErosionModContent.ErosionBlock DRIED_DIRT = new ErosionModContent.ErosionBlock(
            ErosionRegistry.RawRegistry.DRIED_DIRT, () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock MINERAL_RICH_DIRT = new ErosionModContent.ErosionBlock(
            ErosionRegistry.RawRegistry.MINERAL_RICH_DIRT, () -> new ErosionSimpleBlocks.DirtBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );

        public static final ErosionModContent.ErosionBlock PROPYLITIZED_DIORITE = new ErosionModContent.ErosionBlock(
            ErosionRegistry.RawRegistry.PROPYLITIZED_DIORITE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );

        public static final ErosionModContent.ErosionBlock BORAX_DEPOSIT = new ErosionModContent.ErosionBlock(
            ErosionRegistry.RawRegistry.BORAX_DEPOSIT, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );

        public static final ErosionModContent.ErosionBlock KAOLINIZED_GRANITE = new ErosionModContent.ErosionBlock(
            RawRegistry.KAOLINIZED_GRANITE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock CRACKED_STONE = new ErosionModContent.ErosionBlock(
            RawRegistry.CRACKED_STONE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock ALBITIZED_GRANITE = new ErosionModContent.ErosionBlock(
            RawRegistry.ALBITIZED_GRANITE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock QUARTZ_GRAVEL = new ErosionModContent.ErosionBlock(
            RawRegistry.QUARTZ_GRAVEL, () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.GravelBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock CRACKED_CALCITE = new ErosionModContent.ErosionBlock(
            RawRegistry.CRACKED_CALCITE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        
        public static final ErosionModContent.ErosionBlock LIMONITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.LIMONITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock HEMATITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.HEMATITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock MAGNETITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.MAGNETITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock CALCITE_MALACHITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.CALCITE_MALACHITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock NATIVE_GOLD_DEPOSIT = new ErosionModContent.ErosionBlock(
            RawRegistry.NATIVE_GOLD_DEPOSIT, () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock NATIVE_SILVER_DEPOSIT = new ErosionModContent.ErosionBlock(
            RawRegistry.NATIVE_SILVER_DEPOSIT, () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock CASSITERITE_DEPOSIT = new ErosionModContent.ErosionBlock(
            RawRegistry.CASSITERITE_DEPOSIT, () -> new ErosionSimpleBlocks.GravelBlock(
                ErosionSimpleBlocks.DirtBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock BISMUTHINITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.BISMUTHINITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock SPHALERITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.SPHALERITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock AZURITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.AZURITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock GOETHITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.GOETHITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock TETRAHEDRITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.TETRAHEDRITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock ARSENOPYRITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.ARSENOPYRITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock PYRITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.PYRITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock ANGLESITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.ANGLESITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock HALITE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.HALITE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock GALENA_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.GALENA_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RUBY_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.RUBY_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock SAPPHIRE_ORE = new ErosionModContent.ErosionBlock(
            RawRegistry.SAPPHIRE_ORE, () -> new ErosionSimpleBlocks.StoneBlock(
                ErosionSimpleBlocks.StoneBlock.getDefaultBlockProperties()
            )
        );

        public static final ErosionModContent.ErosionBlock RAW_LIMONITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_LIMONITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_MAGNETITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_MAGNETITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_HEMATITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_HEMATITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_MALACHITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_MALACHITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock NATIVE_GOLD = new ErosionModContent.ErosionBlock(
            RawRegistry.NATIVE_GOLD, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock NATIVE_SILVER = new ErosionModContent.ErosionBlock(
            RawRegistry.NATIVE_SILVER, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_SPHALERITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_SPHALERITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_BISMUTHINITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_BISMUTHINITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_CASSITERITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_CASSITERITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_AZURITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_AZURITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_GOETHITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_GOETHITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_TETRAHEDRITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_TETRAHEDRITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_ARSENOPYRITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_ARSENOPYRITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_PYRITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_PYRITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_ANGLESITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_ANGLESITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_GALENA = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_GALENA, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
        public static final ErosionModContent.ErosionBlock RAW_HALITE = new ErosionModContent.ErosionBlock(
            RawRegistry.RAW_HALITE, () -> new ErosionSimpleBlocks.RockBlock(
                ErosionSimpleBlocks.RockBlock.getDefaultBlockProperties()
            )
        );
    }
    public static class Items
    {
        //MACHINES
        public static final ErosionModContent.ErosionItem MATERIAL_PURIFIER = new ErosionModContent.ErosionItem(
            RawRegistry.MATERIAL_PURIFIER, () -> new BlockItem(
                Blocks.MATERIAL_PURIFIER.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem CRUCIBLE = new ErosionModContent.ErosionItem(
            RawRegistry.CRUCIBLE, () -> new BlockItem(
                Blocks.CRUCIBLE.get(), new Item.Properties()
            )
        );
        //------------------------------------------------------
        public static final ErosionModContent.ErosionItem CHEMICAL_REACTOR = new ErosionModContent.ErosionItem(
            RawRegistry.CHEMICAL_REACTOR, () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR.get(), IErosionChemicalReactorItem.getDefaultProperties()
            )
        );
        public static final ErosionModContent.ErosionItem CHEMICAL_REACTOR_SCRUBBER = new ErosionModContent.ErosionItem(
            RawRegistry.CHEMICAL_REACTOR_SCRUBBER, () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR_SCRUBBER.get(), IErosionChemicalReactorItem.getDefaultProperties()
            )
        );
        public static final ErosionModContent.ErosionItem CHEMICAL_REACTOR_MODULE = new ErosionModContent.ErosionItem(
            RawRegistry.CHEMICAL_REACTOR_MODULE, () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR_MODULE.get(), IErosionChemicalReactorItem.getDefaultProperties()
            )
        );
        public static final ErosionModContent.ErosionItem CHEMICAL_REACTOR_COOLING_SYSTEM = new ErosionModContent.ErosionItem(
            RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM, () -> new BlockItem(
                Blocks.CHEMICAL_REACTOR_COOLING_SYSTEM.get(), IErosionChemicalReactorItem.getDefaultProperties()
            )
        );
        //-------------------------------------------------------
        //COOL ITEMS
        public static final ErosionModContent.ErosionItem GAS_MASK = ErosionSimpleItems.GasMask.newGasMaskItem(
            RawRegistry.GAS_MASK, ErosionSimpleItems.GasMask.Quality.HIGH
        );

        public static final ErosionModContent.ErosionItem BASIC_MASK = ErosionSimpleItems.GasMask.newGasMaskItem(
            RawRegistry.BASIC_MASK, ErosionSimpleItems.GasMask.Quality.LOW
        );

        //SIMPLEBLOCKS

        public static final ErosionModContent.ErosionItem DRIED_DIRT = new ErosionModContent.ErosionItem(
            RawRegistry.DRIED_DIRT, () -> new BlockItem(Blocks.DRIED_DIRT.get(), new Item.Properties())
        );
        public static final ErosionModContent.ErosionItem BISMUTHINITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.BISMUTHINITE_ORE, () -> new BlockItem(
                Blocks.BISMUTHINITE_ORE.get(), new Item.Properties()
            )
        );

        public static final ErosionModContent.ErosionItem SPHALERITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.SPHALERITE_ORE, () -> new BlockItem(
                Blocks.SPHALERITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem MINERAL_RICH_DIRT = new ErosionModContent.ErosionItem(
            RawRegistry.MINERAL_RICH_DIRT, () -> new BlockItem(
                Blocks.MINERAL_RICH_DIRT.get(), new Item.Properties()
            )
        );

        public static final ErosionModContent.ErosionItem KAOLINIZED_GRANITE = new ErosionModContent.ErosionItem(
            RawRegistry.KAOLINIZED_GRANITE, () -> new BlockItem(Blocks.KAOLINIZED_GRANITE.get(), new Item.Properties())
        );
        public static final ErosionModContent.ErosionItem CRACKED_STONE = new ErosionModContent.ErosionItem(
            RawRegistry.CRACKED_STONE, () -> new BlockItem(Blocks.CRACKED_STONE.get(), new Item.Properties())
        );
        public static final ErosionModContent.ErosionItem ALBITIZED_GRANITE = new ErosionModContent.ErosionItem(
            RawRegistry.ALBITIZED_GRANITE, () -> new BlockItem(Blocks.ALBITIZED_GRANITE.get(), new Item.Properties())
        );
        public static final ErosionModContent.ErosionItem QUARTZ_GRAVEL = new ErosionModContent.ErosionItem(
            RawRegistry.QUARTZ_GRAVEL, () -> new BlockItem(Blocks.QUARTZ_GRAVEL.get(), new Item.Properties())
        );
        public static final ErosionModContent.ErosionItem PROPYLITIZED_DIORITE = new ErosionModContent.ErosionItem(
            RawRegistry.PROPYLITIZED_DIORITE, () -> new BlockItem(
                Blocks.PROPYLITIZED_DIORITE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem CRACKED_CALCITE = new ErosionModContent.ErosionItem(
            RawRegistry.CRACKED_CALCITE, () -> new BlockItem(
                Blocks.CRACKED_CALCITE.get(), new Item.Properties()
            )
        );

        public static final ErosionModContent.ErosionItem LIMONITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.LIMONITE_ORE, () -> new BlockItem(
                Blocks.LIMONITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem HEMATITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.HEMATITE_ORE, () -> new BlockItem(
                Blocks.HEMATITE_ORE.get(), new Item.Properties()
            )
        );

        public static final ErosionModContent.ErosionItem BORAX_DEPOSIT = new ErosionModContent.ErosionItem(
            RawRegistry.BORAX_DEPOSIT, () -> new BlockItem(
                Blocks.BORAX_DEPOSIT.get(), new Item.Properties()
            )
        );

        public static final ErosionModContent.ErosionItem MAGNETITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.MAGNETITE_ORE, () -> new BlockItem(
                Blocks.MAGNETITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem AZURITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.AZURITE_ORE, () -> new BlockItem(
                Blocks.AZURITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem GOETHITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.GOETHITE_ORE, () -> new BlockItem(
                Blocks.GOETHITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem TETRAHEDRITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.TETRAHEDRITE_ORE, () -> new BlockItem(
                Blocks.TETRAHEDRITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem ARSENOPYRITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.ARSENOPYRITE_ORE, () -> new BlockItem(
                Blocks.ARSENOPYRITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem PYRITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.PYRITE_ORE, () -> new BlockItem(
                Blocks.PYRITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem ANGLESITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.ANGLESITE_ORE, () -> new BlockItem(
                Blocks.ANGLESITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem HALITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.HALITE_ORE, () -> new BlockItem(
                Blocks.HALITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem GALENA_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.GALENA_ORE, () -> new BlockItem(
                Blocks.GALENA_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem RUBY_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.RUBY_ORE, () -> new BlockItem(
                Blocks.RUBY_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem SAPPHIRE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.SAPPHIRE_ORE, () -> new BlockItem(
                Blocks.SAPPHIRE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem CALCITE_MALACHITE_ORE = new ErosionModContent.ErosionItem(
            RawRegistry.CALCITE_MALACHITE_ORE, () -> new BlockItem(
                Blocks.CALCITE_MALACHITE_ORE.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem NATIVE_GOLD_DEPOSIT = new ErosionModContent.ErosionItem(
            RawRegistry.NATIVE_GOLD_DEPOSIT, () -> new BlockItem(
                Blocks.NATIVE_GOLD_DEPOSIT.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem CASSITERITE_DEPOSIT = new ErosionModContent.ErosionItem(
            RawRegistry.CASSITERITE_DEPOSIT, () -> new BlockItem(
                Blocks.CASSITERITE_DEPOSIT.get(), new Item.Properties()
            )
        );
        public static final ErosionModContent.ErosionItem NATIVE_SILVER_DEPOSIT = new ErosionModContent.ErosionItem(
            RawRegistry.NATIVE_SILVER_DEPOSIT, () -> new BlockItem(
                Blocks.NATIVE_SILVER_DEPOSIT.get(), new Item.Properties()
            )
        );
        
        // SIMPLE ITEMS
        public static final ErosionModContent.ErosionItem FELDSPAR_POWDER = new ErosionModContent.ErosionItem(
            RawRegistry.FELDSPAR_POWDER, () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final ErosionModContent.ErosionItem FLUX = new ErosionModContent.ErosionItem(
            RawRegistry.FLUX, () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final ErosionModContent.ErosionItem SALT = new ErosionModContent.ErosionItem(
            RawRegistry.SALT, () -> new Item(new Item.Properties().stacksTo(64))
        );
        public static final ErosionModContent.ErosionItem GAS_FILTER = new ErosionModContent.ErosionItem(
            RawRegistry.GAS_FILTER, () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final ErosionModContent.ErosionItem RUBY = new ErosionModContent.ErosionItem(
            RawRegistry.RUBY, () -> new Item(new Item.Properties().stacksTo(64))
        );
        public static final ErosionModContent.ErosionItem SAPPHIRE = new ErosionModContent.ErosionItem(
            RawRegistry.SAPPHIRE, () -> new Item(new Item.Properties().stacksTo(64))
        );
        public static final ErosionModContent.ErosionItem SULFUR_SLAG = new ErosionModContent.ErosionItem(
            RawRegistry.SULFUR_SLAG, () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final ErosionModContent.ErosionItem ANTIMONY_SLAG = new ErosionModContent.ErosionItem(
            RawRegistry.ANTIMONY_SLAG, () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final ErosionModContent.ErosionItem DEBRIS = new ErosionModContent.ErosionItem(
            RawRegistry.DEBRIS, () -> new Item(new Item.Properties().stacksTo(16))
        );
        public static final ErosionModContent.ErosionItem CRUSHED_EGG_SHELL = new ErosionModContent.ErosionItem(
            RawRegistry.CRUSHED_EGG_SHELL, () -> new Item(new Item.Properties().stacksTo(16))
        );

        public static final ErosionModContent.ErosionItem BORAX = new ErosionModContent.ErosionItem(
            RawRegistry.BORAX, () -> new Item(new Item.Properties().stacksTo(32))
        );
        public static final ErosionModContent.ErosionItem DEHYDRATED_BORAX = new ErosionModContent.ErosionItem(
            RawRegistry.DEHYDRATED_BORAX, () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final ErosionModContent.ErosionItem BUCKET_OF_SULFURIC_ACID = new ErosionModContent.ErosionItem(
            RawRegistry.BUCKET_OF_SULFURIC_ACID, () -> new Item(new Item.Properties().stacksTo(1))
        );

        public static final ErosionModContent.ErosionItem BORIC_ACID_CRYSTAL = new ErosionModContent.ErosionItem(
            RawRegistry.BORIC_ACID_CRYSTAL, () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final ErosionModContent.ErosionItem RAW_LIMONITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_LIMONITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_LIMONITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_MAGNETITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_MAGNETITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_MAGNETITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_HEMATITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_HEMATITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_HEMATITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_MALACHITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_MALACHITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_MALACHITE.get()
            )
        );

        public static final ErosionModContent.ErosionItem NATIVE_GOLD = new ErosionModContent.ErosionItem(
            RawRegistry.NATIVE_GOLD, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.NATIVE_GOLD.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_CASSITERITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_CASSITERITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_CASSITERITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem TIN_CHUNK = new ErosionModContent.ErosionItem(
            RawRegistry.TIN_CHUNK, () -> new Item(new Item.Properties().stacksTo(32))
        );
        public static final ErosionModContent.ErosionItem LEAD_CHUNK = new ErosionModContent.ErosionItem(
            RawRegistry.LEAD_CHUNK, () -> new Item(new Item.Properties().stacksTo(32))
        );
        public static final ErosionModContent.ErosionItem NATIVE_SILVER = new ErosionModContent.ErosionItem(
            RawRegistry.NATIVE_SILVER, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.NATIVE_SILVER.get()
            )
        );
        public static final ErosionModContent.ErosionItem SILVER_CHUNK = new ErosionModContent.ErosionItem(
            RawRegistry.SILVER_CHUNK, () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final ErosionModContent.ErosionItem RAW_BISMUTHINITE= new ErosionModContent.ErosionItem(
            RawRegistry.RAW_BISMUTHINITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_BISMUTHINITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem BISMUTH_CHUNK = new ErosionModContent.ErosionItem(
            RawRegistry.BISMUTH_CHUNK, () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final ErosionModContent.ErosionItem RAW_SPHALERITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_SPHALERITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_SPHALERITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem ZINC_CHUNK = new ErosionModContent.ErosionItem(
            RawRegistry.ZINC_CHUNK, () -> new Item(new Item.Properties().stacksTo(32))
        );

        public static final ErosionModContent.ErosionItem RAW_AZURITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_AZURITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_AZURITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_GOETHITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_GOETHITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_GOETHITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_TETRAHEDRITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_TETRAHEDRITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_TETRAHEDRITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_ARSENOPYRITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_ARSENOPYRITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_ARSENOPYRITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_PYRITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_PYRITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_PYRITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_ANGLESITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_ANGLESITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_ANGLESITE.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_GALENA = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_GALENA, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_GALENA.get()
            )
        );
        public static final ErosionModContent.ErosionItem RAW_HALITE = new ErosionModContent.ErosionItem(
            RawRegistry.RAW_HALITE, () -> new ErosionSimpleItems.ErosionRockBlockItem(
                Blocks.RAW_HALITE.get()
            )
        );
    }

    public class BlockEntities
    {
        //MACHINES
        public static final ErosionModContent.ErosionBlockEntity<MaterialPurifierBlockEntity> MATERIAL_PURIFIER = new ErosionModContent
        .ErosionBlockEntity<MaterialPurifierBlockEntity>(
            RawRegistry.MATERIAL_PURIFIER, MaterialPurifierBlockEntity::new,
            ErosionRegistry.Blocks.MATERIAL_PURIFIER
        );
        
        public static final ErosionModContent.ErosionBlockEntity<CrucibleBlockEntity> CRUCIBLE = new ErosionModContent
        .ErosionBlockEntity<CrucibleBlockEntity>(
            RawRegistry.CRUCIBLE, CrucibleBlockEntity::new,
            ErosionRegistry.Blocks.CRUCIBLE
        );

        public static final ErosionModContent.ErosionBlockEntity<ChemicalReactorBlockEntity> CHEMICAL_REACTOR = new ErosionModContent
        .ErosionBlockEntity<ChemicalReactorBlockEntity>(
            RawRegistry.CHEMICAL_REACTOR,
            ChemicalReactorBlockEntity::new,
            ErosionRegistry.Blocks.CHEMICAL_REACTOR
        );
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EROSION_TAB = ErosionModContentManager.EROSION_MOD_CREATIVE_MODE_TABS.register(
        ErosionConfig.CREATIVE_TAB_NAME, () -> CreativeModeTab.builder()
        .title(Component.translatable(ErosionConfig.CREATIVE_TAB_ID))
        .icon(() -> ErosionRegistry.Items.KAOLINIZED_GRANITE.getItemHolder().get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            //ALL ITEMS GO HERE
            //SIMPLE BLOCKS
            output.accept(ErosionRegistry.Items.DRIED_DIRT.getItemHolder().get());

            output.accept(ErosionRegistry.Items.KAOLINIZED_GRANITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CRACKED_STONE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.ALBITIZED_GRANITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.QUARTZ_GRAVEL.getItemHolder().get());
            output.accept(ErosionRegistry.Items.PROPYLITIZED_DIORITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CRACKED_CALCITE.getItemHolder().get());

            output.accept(ErosionRegistry.Items.LIMONITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CALCITE_MALACHITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.HEMATITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.BORAX_DEPOSIT.getItemHolder().get());
            output.accept(ErosionRegistry.Items.MAGNETITE_ORE.getItemHolder().get());

            output.accept(ErosionRegistry.Items.RAW_HEMATITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_LIMONITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_MAGNETITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_MALACHITE.getItemHolder().get());

            output.accept(ErosionRegistry.Items.NATIVE_GOLD_DEPOSIT.getItemHolder().get());
            output.accept(ErosionRegistry.Items.NATIVE_GOLD.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_CASSITERITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.TIN_CHUNK.getItemHolder().get());
            output.accept(ErosionRegistry.Items.LEAD_CHUNK.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CASSITERITE_DEPOSIT.getItemHolder().get());

            output.accept(ErosionRegistry.Items.NATIVE_SILVER.getItemHolder().get());
            output.accept(ErosionRegistry.Items.NATIVE_SILVER_DEPOSIT.getItemHolder().get());
            output.accept(ErosionRegistry.Items.SILVER_CHUNK.getItemHolder().get());

            output.accept(ErosionRegistry.Items.BISMUTH_CHUNK.getItemHolder().get());
            output.accept(ErosionRegistry.Items.BISMUTHINITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_BISMUTHINITE.getItemHolder().get());

            output.accept(ErosionRegistry.Items.ZINC_CHUNK.getItemHolder().get());
            output.accept(ErosionRegistry.Items.SPHALERITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_SPHALERITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.MINERAL_RICH_DIRT.getItemHolder().get());

            output.accept(ErosionRegistry.Items.RAW_AZURITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_GOETHITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.AZURITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.GOETHITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.TETRAHEDRITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.ARSENOPYRITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.PYRITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.ANGLESITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.HALITE_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.GALENA_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_TETRAHEDRITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_ARSENOPYRITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_PYRITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_ANGLESITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_GALENA.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RAW_HALITE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RUBY_ORE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.SAPPHIRE_ORE.getItemHolder().get());

            //SIMPLE ITEMS
            output.accept(ErosionRegistry.Items.FLUX.getItemHolder().get());
            output.accept(ErosionRegistry.Items.SALT.getItemHolder().get());
            output.accept(ErosionRegistry.Items.RUBY.getItemHolder().get());
            output.accept(ErosionRegistry.Items.BORAX.getItemHolder().get());
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
            output.accept(ErosionRegistry.Items.MATERIAL_PURIFIER.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CRUCIBLE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR_SCRUBBER.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR_MODULE.getItemHolder().get());
            output.accept(ErosionRegistry.Items.CHEMICAL_REACTOR_COOLING_SYSTEM.getItemHolder().get());
            output.accept(ErosionRegistry.Items.BASIC_MASK.getItemHolder().get());
            output.accept(ErosionRegistry.Items.GAS_MASK.getItemHolder().get());
            output.accept(ErosionRegistry.Items.GAS_FILTER.getItemHolder().get());
        })
        .build()
    );

    public static void init(IEventBus modEventBus)
    {
        ErosionModContentManager.loadClasses(
            ErosionRegistry.class,
            ErosionRegistry.Blocks.class,
            ErosionRegistry.Items.class,
            ErosionRegistry.BlockEntities.class,
            ErosionRegistry.Menus.class,
            ErosionRegistry.ArmorMaterials.class,
            ErosionRegistry.SoundEvents.class,
            ErosionRegistry.DataComponents.class
        );

        ErosionModContentManager.registerContent(modEventBus, () -> {});
    }

    @SubscribeEvent 
    public static void setup(FMLClientSetupEvent e)
    {
        ErosionUtils.Log("We're in the registry.");
    }
}