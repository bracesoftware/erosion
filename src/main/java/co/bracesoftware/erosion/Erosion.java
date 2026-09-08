package co.bracesoftware.erosion;

import org.slf4j.Logger;

import co.bracesoftware.erosion.network.server.ErosionStatusSyncPacket;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Erosion.MODID)
public class Erosion
{
    public static final String MODID = "erosion";
    public static final String MODNAME = "Erosion";
    public static final Integer BUILD = 1;
    
    public static final Logger LOGGER = LogUtils.getLogger();

    public Erosion(IEventBus modEventBus, ModContainer modContainer)
    {
        ErosionMod.SetupRegistry(modEventBus);
    }
}