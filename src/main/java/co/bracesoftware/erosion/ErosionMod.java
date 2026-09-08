package co.bracesoftware.erosion;

import co.bracesoftware.erosion.ErosionCore;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import co.bracesoftware.erosion.Erosion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import co.bracesoftware.erosion.ErosionConfig.*;
import co.bracesoftware.erosion.ErosionCore.CommandRegistry;
import co.bracesoftware.erosion.blocks.ErosionRegistry;
import co.bracesoftware.erosion.network.server.ErosionStatusSyncPacket;

import co.bracesoftware.libs.minecraft_text_formatter.Text;

@EventBusSubscriber(modid = Erosion.MODID)
public final class ErosionMod
{
    public static final String WELCOME_ASCII = Text.Format(Text.Col.DARK_RED) + """

        ▄▄▄▄▄▄▄                                   
      ███▀▀▀▀▀                    ▀▀ 
      ███▄▄     ████▄ ▄███▄ ▄█▀▀▀ ██  ▄███▄ ████▄ 
      ███       ██ ▀▀ ██ ██ ▀███▄ ██  ██ ██ ██ ██ 
       ▀███████ ██    ▀███▀ ▄▄▄█▀ ██▄ ▀███▀ ██ ██ """+
    Text.Format(Text.Col.RED)+"v"+Erosion.BUILD+"\n"+
    Text.Format(Text.Col.GOLD) +
     "         Geological Chemistry for Minecraft" +
    Text.Format(Text.Col.GRAY);

    private static final List<ChunkPos> LOADED_CHUNKS = new ArrayList<>();
    public static final RandomSource RANDOM = RandomSource.create();

    //setup
    @SubscribeEvent 
    public static void onServerStart(ServerAboutToStartEvent event)
    {
        ErosionMod.LoadMod();
        ErosionConfig.ServerConfig.LoadModConfig();
    }
    @SubscribeEvent 
    public static void onServerStop(ServerStoppingEvent event)
    {
        ErosionMod.UnloadMod();
        ErosionConfig.ServerConfig.SaveModConfig();
    }

    @SubscribeEvent 
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn e)
    {
        ErosionMod.LoadMod();
    }

    @SubscribeEvent 
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut e)
    {
        ErosionMod.UnloadMod();
    }

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event)
    {
        if(!ErosionConfig.ServerConfig.AGRESSIVE_GEOCHEMICAL_ALTERATION.get()) return;
        if(event.getLevel() instanceof ServerLevel level)
        {
            ErosionCore.Extra.bulkProcess(level);
        }
    }

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event)
    {
        if(!ErosionConfig.ServerConfig.AGRESSIVE_GEOCHEMICAL_ALTERATION.get()) return;
        if(event.getLevel() instanceof ServerLevel level)
        {
            ErosionCore.Extra.bulkProcess(level);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if(event.getEntity().level() instanceof ServerLevel level)
        {
            ErosionCore.Extra.bulkProcess(level);
        }
    }

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockPos liquidPos = event.getLiquidPos();

        ErosionCore.addCandidate(level, pos);
        ErosionCore.addCandidate(level, liquidPos);

        for(Direction direction : Direction.values())
        {
            ErosionCore.addCandidate(
                    level,
                    pos.relative(direction)
            );

            ErosionCore.addCandidate(
                    level,
                    liquidPos.relative(direction)
            );
        }
        return;
    }


    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event)
    {
        if(!(event.getLevel() instanceof ServerLevel level))
        {
            return;
        }

        if(level.dimension() != Level.OVERWORLD)
        {
            return;
        }

        BlockPos pos = event.getPos();

        ErosionCore.addCandidate(level, pos);

        for (Direction direction : Direction.values())
        {
            ErosionCore.addCandidate(
                level,
                pos.relative(direction)
            );
        }
        return;
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event)
    {
        event.registrar("1")
        .playToClient(
            ErosionStatusSyncPacket.TYPE,
            ErosionStatusSyncPacket.STREAM_CODEC,
            ErosionStatusSyncPacket::handleData
        );
        return;
    }

    @SubscribeEvent //USED TO PROCESS CANDIDATES WITH HIGH PRIORITY
    public static void onServerTick(ServerTickEvent.Post e)
    {
        int tick = e.getServer().getTickCount();
        ErosionCore.processPendingPriority(e.getServer().getLevel(Level.OVERWORLD));
        
        if (tick % 20 == 0)
        {
            ErosionStatusSyncPacket packet = new ErosionStatusSyncPacket(
                ErosionCore.getPendingSize(),
                ErosionCore.getPerformedAlterations(),
                ErosionCore.getPendingFastSize(),
                ErosionCore.getPerformedAlterationsPriority(),
                ErosionConfig.ServerConfig.AGRESSIVE_GEOCHEMICAL_ALTERATION.get()
            );

            for(var player : e.getServer().getPlayerList().getPlayers())
            {
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
        return;
    }

    @SubscribeEvent//USED TO PROCESS LOW PRIORITY CANDIDATES
    public static void onLevelTick(LevelTickEvent.Post event) {

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        long gameTime = level.getGameTime();
      
        ErosionCore.processPending(level);
        return;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        var erosionRoot = Commands.literal(Erosion.MODID);

        for (ErosionCore.Command cmd : ErosionCore.CommandRegistry.COMMANDS) {
            erosionRoot.then(
                Commands.literal(cmd.subcmd)
                    .executes(context -> {
                        cmd.execute(context.getSource());
                        return 1;
                    })
            );
        }

        dispatcher.register(erosionRoot);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ErosionRegistry.BlockEntities.MATERIAL_PURIFIER.get(),
            (blockEntity, side) -> blockEntity.getItemHandler(side)
        );
    }

    // =========================================================
    // SETUP
    // =========================================================

    private static final AtomicBoolean REGISTRY_SETUP = new AtomicBoolean(false);
    private static Boolean MOD_LOADED = false;

    public static void SetupRegistry(IEventBus modEventBus)
    {
        if(!REGISTRY_SETUP.compareAndSet(false, true))
        {
            throw new RuntimeException("Registry has to be set up only once.");
        }
        ErosionUtils.Log("Opening the Erosion registry...");
        ErosionRegistry.init(modEventBus);
        ErosionUtils.Log("Registry applied.");
    }
    public static void LoadMod()
    {
        if(MOD_LOADED)
        {
            ErosionUtils.Log("Mod is already loaded.");
            return;
        }

        MOD_LOADED = true;

        ErosionUtils.Log(WELCOME_ASCII);
        ErosionUtils.Log("Mod version: " + Erosion.BUILD);
        ErosionUtils.Log("Mod loading...");

        ErosionCore.Load();
        return;
    }
    public static void UnloadMod()
    {
        if(!MOD_LOADED)
        {
            ErosionUtils.Log("Mod is not loaded.");
            return;
        }

        MOD_LOADED = false;

        ErosionUtils.Log(WELCOME_ASCII);
        ErosionUtils.Log("Mod version: " + Erosion.BUILD);
        ErosionUtils.Log("Mod unloading...");

        ErosionCore.Unload();
        return;
    }
}
