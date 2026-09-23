package co.bracesoftware.erosion;

import org.slf4j.Logger;

import co.bracesoftware.erosion.ErosionRetrogen.RetrogenDataManager;
import co.bracesoftware.erosion.world.ErosionRegistry;
import com.mojang.logging.LogUtils;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import co.bracesoftware.erosion.api.eventbus.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/*

This huge comment is dedicated to:

    IN-GAME ITEMS:
        * Kaolinized Granite
        -                   for being THE FIRST BLOCK ADDED TO THE MOD!
        * Material Purifier
        -                   for being THE FIRST BLOCK ENTITY ADDED TO THE MOD!
        * Crucible
        -                   for being THE FIRST BLOCK WITH A CUSTOM MODEL
        * Chemical Reactor
        and its components
        such as Cooling System,
        Scrubber and Module
        -                   for being the FIRST BLOCK WITH A MULTIBLOCK SYSTEM
        -                   for being the FIRST BLOCK WITH AN ABSTRACTCOINTAINERMENU IMPLEMENTATION
        -                   for being the first block to be implemented using the custom Erosion network-safe block api
        * Hematite, Magnetite, Limonite
        and Malachite
        -                   for being THE FIRST 4 ORES TO BE ADDED TO THE mod
        * Gas Mask
        -                   for being the FIRST WEARABLE ITEM ADDED TO THE MOD
        * Sulfur Dioxide
        -                   for being the first gas to be added as a custom gas entity
        * Ore Mining Sound
        -                   for being the first custom sound added by the mod
        * ChemicalReactorMenu
        -                   for being the first menu to be added to the game

    SOURCE CODE:
        * ErosionNetworkSafeVariants, and the whole erosion.data package
        -                   for making my life easier
        * ErosionMod, ErosionCore, ErosionUtils
        -                   for being the first classes to be added to the source code
        * ErosionRegistry
        -                   for being the bravest class out of them all
        * ErosionClusterHandler, ErosionRetrogen
        -                   for bringing super cool retrogen mechanisms this mod would be impossible without
        * ErosionModCompat, ErosionSimpleItems, ErosionSplash, ErosionSimpleBlocks
        -                   for little funny things
        * ErosionClient
        -                   for being the first and only class to IMPLEMENT CLIENT-ONLY STUFF
        * ErosionBrandingText
        -                   for being first and almost last attempt at writing mixins
        * ErosionStatusSyncPacket
        -                   for being THE FIRST NETWORK PACKET TO BE ADDED
        * ChemicalReactorBlock.ChemicalReactorMultiBlockComponentPosPacket
        -                   for being an absolute gigachad class
        * IErosionBlockWithTip
        -                   for being a first very useful interface
*/

@Mod(Erosion.MODID)
@EventBusSubscriber(modid = Erosion.MODID)
public class Erosion
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "erosion";
    public static final String MODNAME = "Erosion";
    public static final int BUILD = 7;

    public static final String SUBTITLE = "Chemically Reimagined.";
    public static final String SNAPSHOT = ModList.get().getModFileById(Erosion.MODID).versionString();
    public static final String MOD_BRANDING = Erosion.MODNAME + " build " + Erosion.BUILD + " snapshot " + Erosion.SNAPSHOT;

    static
    {
        ErosionUtils.Log("Welcome to " + Erosion.MODNAME + ": " + Erosion.SUBTITLE);
    }

    public Erosion(IEventBus modEventBus, ModContainer modContainer)
    {
        ErosionSplash.class.getName();
        LOGGER.info("LOGGER test.");
        
        ErosionEventBus.registerListeners(ErosionCore.class);
        ErosionEventBus.registerListeners(ErosionModCompat.class);

        ErosionMod.SetupRegistry(modEventBus);
    }

    @SubscribeEvent 
    public static void onSetup(FMLCommonSetupEvent e)
    {
        ErosionUtils.Log("Common setup event called.");
    }

    public static class SML
    {
        public static class ModSide
        {
            private String name;
            public Runnable load;
            public Runnable unload;

            public ModSide(String n, Runnable l, Runnable u)
            {
                this.name = n;
                this.load = l;
                this.unload = u;
            }

            public String getName()
            {
                return this.name;
            }
        }
        public static class ModSides
        {
            public static final ModSide CLIENT = new ModSide(
                ErosionRegistry.RawRegistry.SMLModSides.CLIENT.getId(),
                () -> {
                    ErosionMod.LoadMod();
                    return;
                },
                () -> {
                    ErosionMod.UnloadMod();
                    return;
                }
            );

            public static final ModSide SERVER = new ModSide(
                ErosionRegistry.RawRegistry.SMLModSides.SERVER.getId(),
                () -> {
                    ErosionRetrogen.Load();
                    ErosionMod.LoadMod();
                    ErosionConfig.ServerConfig.LoadModConfig();
                    ErosionCommandProcessor.setupCommands();
                    
                    MinecraftServer s = ServerLifecycleHooks.getCurrentServer();
                    ErosionRegistry.DataAttachments.RETROGEN_DATA = ErosionRetrogen.RetrogenDataManager.loadRetrogenData(
                        s, ErosionRegistry.RawRegistry.RETROGEN_DATA.getId()
                    );
                    return;
                },
                () -> {
                    ErosionMod.UnloadMod();
                    ErosionConfig.ServerConfig.SaveModConfig();
                    ErosionCommandProcessor.discardCommands();

                    MinecraftServer s = ServerLifecycleHooks.getCurrentServer();
                    RetrogenDataManager.saveRetrogenData(
                        s, ErosionRegistry.RawRegistry.RETROGEN_DATA.getId(), 
                        ErosionRegistry.DataAttachments.RETROGEN_DATA
                    );

                    ErosionRetrogen.Unload();
                    return;
                }
            );
        }
        
        public static void LoadModFor(ModSide s)
        {
            ErosionUtils.Log("SML called for loading: " + s.getName());
            s.load.run();
        }
        public static void UnloadModFor(ModSide s)
        {
            ErosionUtils.Log("SML called for unloading: " + s.getName());
            s.unload.run();
        }
    }
}