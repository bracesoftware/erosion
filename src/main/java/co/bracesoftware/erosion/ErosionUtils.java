package co.bracesoftware.erosion;
import java.text.NumberFormat;
import java.util.Locale;

import co.bracesoftware.erosion.network.client.ErosionClientData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
}