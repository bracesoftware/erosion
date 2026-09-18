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

@Mod(Erosion.MODID)
@EventBusSubscriber(modid = Erosion.MODID)
public class Erosion
{
    public static final String MODID = "erosion";
    public static final String MODNAME = "Erosion";
    public static final Integer BUILD = 7;

    public static final String SUBTITLE = "Chemically Reimagined.";
    public static final String SNAPSHOT = ModList.get().getModFileById(Erosion.MODID).versionString();
    public static final String MOD_BRANDING = Erosion.MODNAME + " build " + Erosion.BUILD + " snapshot " + Erosion.SNAPSHOT;
 
    static {
        ErosionUtils.Log("Welcome to Minecraft: Erosion Edition!");
    }
    
    public static final Logger LOGGER = LogUtils.getLogger();

    public Erosion(IEventBus modEventBus, ModContainer modContainer)
    {
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
                "client_side",
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
                "server_side",
                () -> {
                    ErosionRetrogen.Load();
                    ErosionMod.LoadMod();
                    ErosionConfig.ServerConfig.LoadModConfig();
                    
                    MinecraftServer s = ServerLifecycleHooks.getCurrentServer();
                    ErosionRegistry.DataAttachments.RETROGEN_DATA = ErosionRetrogen.RetrogenDataManager.loadRetrogenData(
                        s, ErosionRegistry.RawRegistry.RETROGEN_DATA.getId()
                    );
                    return;
                },
                () -> {
                    ErosionMod.UnloadMod();
                    ErosionConfig.ServerConfig.SaveModConfig();
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