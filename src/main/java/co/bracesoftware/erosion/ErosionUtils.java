package co.bracesoftware.erosion;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.*;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.ErosionExceptions.ErosionAPIExceptions;
import co.bracesoftware.erosion.ErosionExceptions.ErosionAPIExceptions.ErosionDisplayMessageException;
import co.bracesoftware.erosion.network.client.ErosionClientData;
import co.bracesoftware.erosion.network.client.ErosionDebugOverlay;
import co.bracesoftware.erosion.network.server.ErosionScreenMessagePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class ErosionUtils
{
    public static int getTicksRemainingUntil(long ct, int interval)
    {
        return (int) (interval - (ct % interval)) % interval;
    }

    public static String tickToFormattedTime(int tick)
    {
        int totalSeconds = tick / 20;

        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        var result = new StringBuilder();

        if(days > 0) result.append(days).append(" day(s) ");
        if(hours > 0) result.append(hours).append(" hr(s) ");
        if(minutes > 0) result.append(minutes).append(" min(s) ");
        if(seconds > 0 || result.isEmpty()) result.append(seconds).append(" sec(s)");

        return result.toString().trim();
    }

    public static int minutesToTick(int min)
    {
        return min * 60 * 20;
    }
    public static boolean isPlayerNearby(ServerLevel l, BlockPos p, int radius)
    {
        double radiusSq = (double) radius * radius;

        for(var player : l.players())
        {
            if(player.isSpectator() || !player.isAlive()) continue;
            if(player.distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) <= radiusSq) return true;
        }

        return false;
    }
    public static void spawnGasParticle(ServerLevel l, BlockPos p)
    {
        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();
        ClientboundLevelParticlesPacket pp = new ClientboundLevelParticlesPacket(
            ParticleTypes.CAMPFIRE_COSY_SMOKE, true, x,y,z,
            0.0f,0.0f,0.0f,0.005f,5
        );
        l.getChunkSource().chunkMap.getPlayers(
            new ChunkPos(BlockPos.containing(x, y, z)), false
        ).forEach(pl -> pl.connection.send(pp));
    }
    public record ErosionPair<A, B>(A first, B second) {}
    public static void Log(String text)
    {
        Erosion.LOGGER.info("\n\t{" + Erosion.MODNAME + "} :: [System]: " + text);
        return;
    }
    public static <T> T compute(Supplier<T> supplier)
    {
        return supplier.get();
    }

    public static String getStatus()
    {
        Log(ErosionClientData.formatModStatusString(
            ErosionCore.getPendingSize(),
            ErosionCore.getPerformedAlterations(),
            ErosionCore.getPendingFastSize(),
            ErosionCore.getPerformedAlterationsPriority(),
            ErosionCore.getPendingAgainSize()
        ));
        return ErosionClientData.formatModStatusString(
            ErosionCore.getPendingSize(),
            ErosionCore.getPerformedAlterations(),
            ErosionCore.getPendingFastSize(),
            ErosionCore.getPerformedAlterationsPriority(),
            ErosionCore.getPendingAgainSize()
        );
    }
    public static String formatCompact(double value)
    {
        NumberFormat nf = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        nf.setMaximumFractionDigits(1);
        return nf.format(value).toLowerCase();
    }

    @Deprecated 
    public static void displayMessageVanilla(
        Player player, String text
    ) throws ErosionDisplayMessageException
    {
        if(ErosionConfig.SUPER_SAFE_MODE)
        {
            throw new ErosionDisplayMessageException("Safe mode is on -> cannot call this function");
        }
        player.displayClientMessage(
            Component.literal(text)
            .withStyle(ChatFormatting.WHITE), true
        );
    }

    public static void displayMessage(Player pl, String text, ErosionScreenMessage.Color col)
    {
        var pk = new ErosionScreenMessagePacket(text, col.getColor());
        if(pl instanceof ServerPlayer p) PacketDistributor.sendToPlayer(p, pk);
    }

    public static void displayMessage(Player pl, String text)
    {
        var pk = new ErosionScreenMessagePacket(text, ErosionScreenMessage.Color.WHITE.getColor());
        if(pl instanceof ServerPlayer p) PacketDistributor.sendToPlayer(p, pk);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> isInstanceOf(Object o, List<Class<?>> l)
    {
        return l.stream()
        .filter(c -> c.isInstance(o))
        .findFirst()
        .map(c -> (T) c.cast(o));
    }

    public static String getResourcesFolder()
    {
        return "../src/main/resources/";
    }

    public static String getGeneratedFolder()
    {
        return "__" + Erosion.MODID + "_generated__/";
    }

    public static class Misc
    {
        public static boolean randomWithChanceToBe(boolean res, int chance)
        {
            return res == (ErosionMod.RANDOM.nextInt(100) < chance);
        }
        public static void grantAdvancement(ServerPlayer p, ResourceLocation a)
        {
            AdvancementHolder ad = p.getServer()
            .getAdvancements().get(a);

            if(ad != null)
            {
                var padv = p.getAdvancements();
                var prog = padv.getOrStartProgress(ad);

                if(!prog.isDone())
                {
                    for(var c : prog.getRemainingCriteria())
                    {
                        padv.award(ad, c);
                    }
                }
            }
            return;
        }
        public static void sendMsg(
            Object s, String text
        ) throws ErosionAPIExceptions.ErosionDisplayMessageException
        {
            ErosionUtils.Log(getCurrentMethodName() + " says -> " + text);
            if(s == null) throw new ErosionDisplayMessageException("Object is null!");

            Component component = Component.literal(ErosionDebugOverlay.MAIN_STYLE + text);

            try
            {
                var m = s.getClass().getMethod("sendSystemMessage", Component.class);
                m.invoke(s, component);
            }
            catch(NoSuchMethodException e)
            {
                e.printStackTrace();
                throw new ErosionDisplayMessageException("Incompatible object for `" + getCurrentMethodName() + "` (no method found) -> " + s.getClass().getName());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                throw new ErosionDisplayMessageException("Reflection circus crashed: " + e.getMessage());
            }
            return;
        }
        public static void sendMsg(
            Object s, Component text
        ) throws ErosionAPIExceptions.ErosionDisplayMessageException
        {
            ErosionUtils.Log(getCurrentMethodName() + " says -> " + text.getString());
            if(s == null) throw new ErosionDisplayMessageException("Object is null!");

            try
            {
                var m = s.getClass().getMethod("sendSystemMessage", Component.class);
                m.invoke(s, text);
            }
            catch(NoSuchMethodException e)
            {
                e.printStackTrace();
                throw new ErosionDisplayMessageException("Incompatible object for `" + getCurrentMethodName() + "` (no method found) -> " + s.getClass().getName());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                throw new ErosionDisplayMessageException("Reflection circus crashed: " + e.getMessage());
            }
            return;
        }
    }
    public static String getCurrentMethodName()
    {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}