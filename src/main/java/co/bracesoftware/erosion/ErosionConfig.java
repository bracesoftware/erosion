package co.bracesoftware.erosion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

public final class ErosionConfig
{
    public static final boolean CRUCIBLE_COPRODUCT_DEBUG = true;

    public static final Integer MAX_PENDING_SIZE = 30000;
    public static final Integer MAX_PENDING_FAST_SIZE = 20000;
    private static final Boolean DEBUG_MODE = false;
    public static final String CREATIVE_TAB_ID = "creativetab.erosion.erosion_tab";
    public static final String CREATIVE_TAB_NAME = "erosion_tab";
    public static final Integer MAX_PURIFIER_FUEL = 3; // Do NOT touch!
    public static final Integer PURIFIER_SECONDS = 10;
    public static final Integer CRUCIBLE_SECONDS = 15;
    public static final Integer CHUNK_SIZE = 16;

    public static final ResourceLocation MINI_FONT = ResourceLocation.withDefaultNamespace("uniform");

    public static final class Clusters
    {
        public static final Integer MIN_SPAWN_DISTANCE = 24;
        public static final Integer SIZE = 8;
        public static final Integer COUNT_PER_TICK = 5;
    }

    public static final class Libs
    {
        public static final Integer MAX_WORDS_PER_COMPONENT_LINE = 6;
        public static final Boolean COMPONENT_WORD_WRAP = true;
    }

    public static final class ServerConfig
    {
        public static final String CONFIG_FOLDER = "erosion_config/";
        public static final String CONFIG_FILE_EXT = ".sys_cfg";
        private static class BasicConfig
        {
            public String name;
            public String id;
            public BasicConfig(String i, String n)
            {
                this.id = i;
                this.name = n;
            }
        }

        public static class BooleanConfig extends BasicConfig
        {
            private Boolean defaultVal;
            private Boolean value;
            private String fileName;
            public BooleanConfig(String id, String name, Boolean defaultVal)
            {
                super(id, name);
                this.value = defaultVal;
                this.defaultVal = defaultVal;
                
                this.fileName = CONFIG_FOLDER + this.id + CONFIG_FILE_EXT;
            }

            public static void saveToFile(String f, Boolean b)
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

            public static Boolean readFromFile(String f, Boolean defaultVal)
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

            public Boolean get()
            {
                return this.value;
            }
            public void set(Boolean b)
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

        public static final List<BasicConfig> MOD_CONFIG = List.of(
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
    }

    public static final Integer MAX_EROSIONS_PER_TICK = 15;
    
    public static Boolean isDebugOn()
    {
        return DEBUG_MODE;
    }

    public ErosionConfig()
    {
        
    }
}