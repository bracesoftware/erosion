package co.bracesoftware.erosion;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import co.bracesoftware.erosion.ErosionCommandProcessor.ErosionCommand;
import co.bracesoftware.erosion.ErosionConfig.ServerConfig;
import co.bracesoftware.erosion.ErosionCore.ErosionDynamicItem;
import co.bracesoftware.erosion.ErosionExceptions.ErosionCommandExceptions.ErosionCommandParserException;
import co.bracesoftware.erosion.ErosionExceptions.ErosionCommandExceptions.ErosionCommandSetupException;
import co.bracesoftware.erosion.network.client.ErosionDebugOverlay;
import co.bracesoftware.erosion.world.ErosionRegistry;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionCommandProcessor
{
    @SubscribeEvent
    public static void entry(RegisterCommandsEvent e)
    {
        e.getDispatcher().register(
            Commands.literal(Erosion.MODID)
            .then(
                Commands.argument("arguments", StringArgumentType.greedyString())
                .executes(
                    c -> {
                        String msg = StringArgumentType.getString(c, "arguments");
                        List<String> list = new ArrayList<>(List.of(msg.split(" ")));
                        process(c.getSource(), list);
                        return 1;
                    }
                )
            )
            .executes(
                c -> {
                    process(c.getSource(), new ArrayList<>());
                    return 1;
                }
            )
        );
        return;
    }

    // ==================INTERNAL IMPL==================== //
    public static class ErosionCommand extends ErosionDynamicItem
    {
        private final BiConsumer<CommandSourceStack, List<String>> what;
        private final String helpInfo;

        public ErosionCommand(
            String n, BiConsumer<CommandSourceStack, List<String>> w,
            String h
        ) throws ErosionCommandSetupException
        {
            this.name = n;
            this.what = w;
            this.helpInfo = h;

            if(h.isEmpty() || h.isBlank())
            {
                throw new ErosionCommandSetupException("Help info cannot be blank.");
            }
            if(n.isEmpty() || n.isBlank())
            {
                throw new ErosionCommandSetupException("Command name cannot be blank.");
            }
        }

        public void call(CommandSourceStack s, List<String> args)
        {
            this.what.accept(s, args);
        }

        public String getHelpDescription()
        {
            return this.helpInfo;
        }

        @Override 
        public void setup()
        {
            ErosionUtils.Log("Setting up command -> " + this.name);
            this.preventDuplication(antiDuplicator);
            return;
        }

        @Override 
        public void discard()
        {
            ErosionUtils.Log("Discarding command -> " + this.name);
            this.discardDuplicationPreventionSys(antiDuplicator);
            return;
        }
    }

    public static final ErosionCommand MOD_STATUS = new ErosionCommand(
        ErosionRegistry.RawRegistry.CommandNames.MOD_STATUS.getId(),
        ErosionCommandProcessor::handleStatus,
        "<>"
    );
    public static final ErosionCommand RELOAD_CONFIG = new ErosionCommand(
        ErosionRegistry.RawRegistry.CommandNames.MOD_STATUS.getId(),
        ErosionCommandProcessor::reloadCfg,
        "<>"
    );

    public static final ErosionCommand VIEW_CONFIG = new ErosionCommand(
        ErosionRegistry.RawRegistry.CommandNames.VIEW_CONFIG.getId(),
        ErosionCommandProcessor::viewCfg,
        "<>"
    );
    public static final ErosionCommand SET_CONFIG = new ErosionCommand(
        ErosionRegistry.RawRegistry.CommandNames.SET_CONFIG.getId(),
        ErosionCommandProcessor::setCfg,
        "<config_key, value>"
    );

    public static final List<ErosionCommand> COMMAND_LIST = List.of(
        MOD_STATUS, RELOAD_CONFIG,
        VIEW_CONFIG, SET_CONFIG
    );

    ///////////////////////////

    public static void process(
        CommandSourceStack s, List<String> args
    ) throws ErosionCommandParserException
    {
        if(args.isEmpty())
        {
            ErosionUtils.Misc.sendMsg(s,"List of commands:");

            for(var cmd : COMMAND_LIST)
            {
                var c = Component.literal("   ").append(Component.literal(cmd.name).withStyle(ChatFormatting.YELLOW)).append(" ")
                .append(Component.literal(cmd.getHelpDescription()).withStyle(ChatFormatting.GRAY));
                s.sendSystemMessage(c);
            }
            return;
        }

        args.removeIf(str -> (
            str == null ||
            str.isBlank() ||
            str.isEmpty()
        ));

        for(var cmd : COMMAND_LIST)
        {
            if(args.get(0).equals(cmd.name))
            {
                args.remove(0);
                cmd.call(s, args);
                return;
            }
        }

        ErosionUtils.Misc.sendMsg(s, "No such command!");
        return;
    }

    // ================== ACTUAL COMMANDS ==================== //

    public static void handleStatus(CommandSourceStack s, List<String> args)
    {
        if(!args.isEmpty())
        {
            ErosionUtils.Misc.sendMsg(s, "This command takes in no arguments!");
            return;
        }
        ErosionUtils.Misc.sendMsg(s, ErosionUtils.getStatus());
        return;
    }
    
    public static void reloadCfg(CommandSourceStack s, List<String> args)
    {
        if(!args.isEmpty())
        {
            ErosionUtils.Misc.sendMsg(s, "This command takes in no arguments!");
            return;
        }
        if(s.getEntity() instanceof Player p)
        {
            if(!p.hasPermissions(2))
            {
                ErosionUtils.Misc.sendMsg(p, "You are not allowed to use this command.");
                return;
            }
        }
        ErosionUtils.Misc.sendMsg(s, "Reloading mod configuration...");
        ErosionConfig.ServerConfig.LoadModConfig();
        ErosionUtils.Misc.sendMsg(s, "Configuration reloaded.");
        
        return;
    }

    public static void viewCfg(CommandSourceStack s, List<String> args)
    {
        ErosionUtils.Misc.sendMsg(s, "Configuration:");

        printConfig(s);
        return;
    }

    public static void printConfig(CommandSourceStack s)
    {
        for(var c : ErosionConfig.ServerConfig.viewConfiguration())
        {
            ErosionUtils.Misc.sendMsg(s,c);
        }
    }

    public static void setCfg(CommandSourceStack s, List<String> args)
    {
        if(args.size() != 2)
        {
            ErosionUtils.Log("Expected 2 arguments after the command name!");
            return;
        }

        String config = args.get(0);
        String value = args.get(1);

        for(var c : ErosionConfig.ServerConfig.MOD_CONFIG)
        {
            if(c.id.equals(config))
            {
                if(c.getConfigClass().equals(Boolean.class))
                {
                    if(
                        !(value.equals("true")) &&
                        !(value.equals("false"))
                    )
                    {
                        ErosionUtils.Misc.sendMsg(s, "This configuration is a boolean, can be either `true` or `false`.");
                        return;
                    }
                    c.setBoolean(Boolean.parseBoolean(value));
                    ErosionUtils.Misc.sendMsg(s, "Value of `" + config + "` successfully changed to: " + c.getBoolean());
                    return;
                }
            }
        }

        ErosionUtils.Misc.sendMsg(s,"Invalid configuration identifier! View the configuration for an identifier list.");
        return;
    }
}