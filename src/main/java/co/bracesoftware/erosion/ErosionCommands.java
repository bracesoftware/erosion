package co.bracesoftware.erosion;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import co.bracesoftware.erosion.network.client.ErosionDebugOverlay;
import co.bracesoftware.libs.minecraft_text_formatter.Text;

public class ErosionCommands
{
    public static void handleStatus(CommandSourceStack s)
    {
        String status = ErosionUtils.getStatus();
        s.sendSystemMessage(Component.literal(ErosionDebugOverlay.MAIN_STYLE + status));
    }
    
    public static void reloadCfg(CommandSourceStack s)
    {
        if(s.getEntity() instanceof Player p)
        {
            if(!p.hasPermissions(2))
            {
                s.sendSystemMessage(Component.literal(ErosionDebugOverlay.MAIN_STYLE + "You are not allowed to use this command."));
                return;
            }
        }
        s.sendSystemMessage(Component.literal(ErosionDebugOverlay.MAIN_STYLE + "Reloading Erosion config..."));
        ErosionConfig.ServerConfig.LoadModConfig();
        s.sendSystemMessage(Component.literal(ErosionDebugOverlay.MAIN_STYLE + "Config reloaded."));
    }
}