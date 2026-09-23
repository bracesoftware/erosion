package co.bracesoftware.erosion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import co.bracesoftware.erosion.ErosionExceptions.ErosionConfigException.ErosionWrongConfigGetterOrSetterMethodCalledException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ErosionConfig
{
    public static final boolean SUPER_SAFE_MODE = false;
    public static class ErosionDebugger
    {
        public static final boolean CRAZY_DEBUG_MODE = false;
    }
    public static class ErosionDataGen
    {
        public static class ErosionTextureGen
        {
            public static final String OUTPUT_FORMAT = "png";
        }
    }
    public static final boolean CRUCIBLE_COPRODUCT_DEBUG = true;
    public static final int MAX_GEOCHEMICAL_ALTERATIONS_PER_TICK = 15;

    public static final int MAX_PENDING_SIZE = 30000;
    public static final int MAX_PENDING_FAST_SIZE = 20000;
    public static final int MAX_PENDING_AGAIN_SIZE = 1000;
    private static final boolean DEBUG_MODE = false;
    public static final String CREATIVE_TAB_ID = "creativetab.erosion.erosion_tab";
    public static final String CREATIVE_TAB_NAME = "erosion_tab";
    public static final int MAX_PURIFIER_FUEL = 3; // Do NOT touch!
    public static final int PURIFIER_SECONDS = 10;
    public static final int CRUCIBLE_SECONDS = 15;
    public static final int CHUNK_SIZE = 16;

    public static final boolean SOMETHING_WENT_WRONG = false;

    public static final ResourceLocation MINI_FONT = ResourceLocation.withDefaultNamespace("uniform");

    public static final class ForCommands
    {
        public static final String EMPTY_ARGUMENTS = "</>";
    }

    public static final class Clusters
    {
        public static final int MIN_SPAWN_DISTANCE = 24;
        public static final int SIZE = 8;
        public static final int COUNT_PER_TICK = 5;
    }

    public static final class Libs
    {
        public static final int MAX_WORDS_PER_COMPONENT_LINE = 6;
        public static final boolean COMPONENT_WORD_WRAP = true;
    }

    public static final class ServerConfig
    {
        public static final String CONFIG_FOLDER = "erosion_config/";
        public static final String CONFIG_FILE_EXT = ".sys_cfg";

        public interface ErosionConfigGettersAndSetters
        {
            default int getInteger()
            {
                throw new ErosionWrongConfigGetterOrSetterMethodCalledException("Not an integer!");
            }
           
            default boolean getBoolean()
            {
                throw new ErosionWrongConfigGetterOrSetterMethodCalledException("Not a boolean!");
            }

            default String getString()
            {
                throw new ErosionWrongConfigGetterOrSetterMethodCalledException("Not a string!");
            }

            default void setInteger(int value)
            {
                throw new ErosionWrongConfigGetterOrSetterMethodCalledException("Not an integer!");
            }

            default void setBoolean(boolean value)
            {
                throw new ErosionWrongConfigGetterOrSetterMethodCalledException("Not a boolean!");
            }

            default void setString(String value)
            {
                throw new ErosionWrongConfigGetterOrSetterMethodCalledException("Not a string!");
            }
        }

        public static abstract class BasicConfig<T> implements ErosionConfigGettersAndSetters
        {
            public String name;
            public String id;
            private final Class<T> type;
            
            public BasicConfig(String i, String n, Class<T> t)
            {
                this.id = i;
                this.name = n;
                this.type = t;
            }

            public Class<?> getConfigClass()
            {
                return this.type;
            }
        }

        public static class BooleanConfig extends BasicConfig<Boolean>
        {
            private final boolean defaultVal;
            private boolean value;
            private String fileName;

            public BooleanConfig(String id, String name, boolean defaultVal)
            {
                super(id, name, Boolean.class);
                this.value = defaultVal;
                this.defaultVal = defaultVal;
                
                this.fileName = CONFIG_FOLDER + this.id + CONFIG_FILE_EXT;
            }

            public static void saveToFile(String f, boolean b)
            {
                try(FileWriter writer = new FileWriter(f))
                {
                    writer.write(Boolean.toString(b));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            public static boolean readFromFile(String f, boolean defaultVal)
            {
                try(BufferedReader reader = new BufferedReader(new FileReader(f)))
                {
                    return Boolean.parseBoolean(reader.readLine());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                return defaultVal;
            }

            public void save()
            {
                ErosionUtils.Log("Saving configuration: " + this.id);
                saveToFile(this.fileName, this.value);
                ErosionUtils.Log("Attempt finished.");
            }

            public void load()
            {
                ErosionUtils.Log("Loading configuration: " + this.id);
                this.value = readFromFile(this.fileName, this.defaultVal);
                this.save();
                ErosionUtils.Log("Loaded `" + this.id + "` as -> " + this.value);
            }

            public boolean getBoolean()
            {
                return this.value;
            }

            public void setBoolean(boolean b)
            {
                this.value = b;
            }

            public String getName()
            {
                return this.name;
            }
        }

        public static final BooleanConfig AGRESSIVE_GEOCHEMICAL_ALTERATION = new BooleanConfig(
            "aggressive_geochemical_alteration",
            "Aggressive geochemical alteration/bulk processing on different events",
            false
        );

        public static final List<? extends BasicConfig<?>> MOD_CONFIG = List.of(
            AGRESSIVE_GEOCHEMICAL_ALTERATION
        );

        public static void LoadModConfig()
        {
            try
            {
                Files.createDirectories(Path.of(ErosionConfig.ServerConfig.CONFIG_FOLDER));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            ErosionUtils.Log(ErosionMod.WELCOME_ASCII);
            ErosionUtils.Log("Loading mod config...");
            for(int i = 0; i < MOD_CONFIG.size(); i++)
            {
                var c = MOD_CONFIG.get(i);
                if(c instanceof BooleanConfig bc)
                {
                    bc.load();
                }
            }
            return;
        }
        public static void SaveModConfig()
        {
            try
            {
                Files.createDirectories(Path.of(ErosionConfig.ServerConfig.CONFIG_FOLDER));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            ErosionUtils.Log(ErosionMod.WELCOME_ASCII);
            ErosionUtils.Log("Saving mod config...");
            for(int i = 0; i < MOD_CONFIG.size(); i++)
            {
                var c = MOD_CONFIG.get(i);
                if(c instanceof BooleanConfig bc)
                {
                    bc.save();
                }
            }
            return;
        }

        public static List<Component> viewConfiguration()
        {
            var l = new ArrayList<Component>();
            for(var g : ErosionConfig.ServerConfig.MOD_CONFIG)
            {
                if(g.getConfigClass().equals(Boolean.class)) l.add(
                    Component.literal("   ")
                    .append(Component.literal(g.id).withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(Boolean.toString(g.getBoolean())).withStyle(ChatFormatting.GOLD))
                );
                l.add(
                    Component.literal("   :: " + g.name).withStyle(ChatFormatting.GRAY)
                );
            }
            return l;
        }
    }
    
    public static boolean isDebugOn()
    {
        return DEBUG_MODE;
    }
}