package co.bracesoftware.erosion;
import java.text.NumberFormat;
import java.util.Locale;

import java.util.function.*;

import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.network.client.ErosionClientData;
import co.bracesoftware.erosion.network.server.ErosionScreenMessagePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
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
    public static void displayMessageOld(Player player, String text)
    {
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
    }
}