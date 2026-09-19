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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class ErosionUtils
{
    public static void Log(String text)
    {
        System.out.println("\n\t{ErosionMod} -> System: " + text);
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
            ErosionCore.getPerformedAlterationsPriority()
        ));
        return ErosionClientData.formatModStatusString(
            ErosionCore.getPendingSize(),
            ErosionCore.getPerformedAlterations(),
            ErosionCore.getPendingFastSize(),
            ErosionCore.getPerformedAlterationsPriority()
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
                throw new ErosionDisplayMessageException("Incompatible object for `sendMsg` (no method found) -> " + s.getClass().getName());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                throw new ErosionDisplayMessageException("Reflection circus crashed: " + e.getMessage());
            }
            return;
        }
    }
}