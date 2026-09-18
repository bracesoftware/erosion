package co.bracesoftware.erosion;
import java.text.NumberFormat;
import java.util.Locale;

import co.bracesoftware.erosion.network.client.ErosionClientData;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ErosionUtils
{
    public static void Log(String text)
    {
        System.out.println("\n\t{ErosionMod} -> System: " + text);
        return;
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
    public static void displayMessage(Player player, String text)
    {
        player.displayClientMessage(
            Component.literal(text)
            .withStyle(ChatFormatting.WHITE), true
        );
    }

    public static String getResourcesFolder()
    {
        return "../src/main/resources/";
    }

    public static String getGeneratedFolder()
    {
        return "__ErosionGenerated__/";
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