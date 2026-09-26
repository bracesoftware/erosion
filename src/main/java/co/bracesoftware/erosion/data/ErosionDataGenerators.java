package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionExceptions.ErosionDataGenException;
import co.bracesoftware.erosion.data.clientgen.ErosionBlockStateGen;
import co.bracesoftware.erosion.data.clientgen.ErosionItemModelGen;
import co.bracesoftware.erosion.data.clientgen.ErosionLang;
import co.bracesoftware.erosion.data.clientgen.ErosionSoundGen;
import co.bracesoftware.erosion.data.commongen.ErosionBlockAnimGen;
import co.bracesoftware.erosion.data.servergen.ErosionAdvGen;
import co.bracesoftware.erosion.data.servergen.ErosionBlockTagGen;
import co.bracesoftware.erosion.data.servergen.ErosionItemTagGen;
import co.bracesoftware.erosion.data.servergen.ErosionLootGen;
import co.bracesoftware.erosion.data.servergen.ErosionRecipeGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionDataGenerators 
{
    public static ErosionBlockStateGen EROSION_BLOCK_STATE_GENERATOR;
    public static ErosionLang EROSION_LANG_GENERATOR;
    public static ErosionItemModelGen EROSION_ITEM_MODEL_GENERATOR;
    public static ErosionSoundGen EROSION_SOUND_GENERATOR;
    public static ErosionLootGen EROSION_LOOT_GENERATOR;
    public static ErosionRecipeGen EROSION_RECIPE_GENERATOR;
    public static ErosionAdvGen EROSION_ADVANCEMENT_GENERATOR;
    public static ErosionBlockTagGen EROSION_BLOCK_TAG_GENERATOR;
    public static ErosionItemTagGen EROSION_ITEM_TAG_GENERATOR;

    @SubscribeEvent
    public static void gatherData(
        GatherDataEvent e
    ) throws ErosionDataGenException
    {
        DataGenerator g = e.getGenerator();
        PackOutput p = g.getPackOutput();
        
        ErosionUtils.Log(ErosionMod.WELCOME_ASCII);
        ErosionUtils.Log("Doing data gen...");
        if(ErosionConfig.SUPER_SAFE_MODE)
        {
            throw new ErosionDataGenException("Safe mode is on!");
        }
        
        EROSION_BLOCK_STATE_GENERATOR = new ErosionBlockStateGen(p, e.getExistingFileHelper());
        EROSION_LANG_GENERATOR = new ErosionLang(p);
        EROSION_ITEM_MODEL_GENERATOR = new ErosionItemModelGen(p, e.getExistingFileHelper());
        EROSION_SOUND_GENERATOR = new ErosionSoundGen(p, e.getExistingFileHelper());
        EROSION_LOOT_GENERATOR = new ErosionLootGen(p, e.getLookupProvider());
        EROSION_RECIPE_GENERATOR = new ErosionRecipeGen(p, e.getLookupProvider());
        EROSION_ADVANCEMENT_GENERATOR = new ErosionAdvGen(p, e.getLookupProvider(), e.getExistingFileHelper());
        EROSION_BLOCK_TAG_GENERATOR = new ErosionBlockTagGen(p, e.getLookupProvider(), e.getExistingFileHelper());
        EROSION_ITEM_TAG_GENERATOR = new ErosionItemTagGen(p, e.getLookupProvider(), EROSION_BLOCK_TAG_GENERATOR.contentsGetter(), e.getExistingFileHelper());

        //other stuff
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateCustomTextures();
        ErosionBlockAnimGen.generateChemicalReactorAnims();

        //client provider
        g.addProvider(e.includeClient(), EROSION_LANG_GENERATOR);
        g.addProvider(e.includeClient(), EROSION_BLOCK_STATE_GENERATOR);
        g.addProvider(e.includeClient(), EROSION_ITEM_MODEL_GENERATOR);
        g.addProvider(e.includeClient(), EROSION_SOUND_GENERATOR);

        //server providers
        g.addProvider(e.includeServer(), EROSION_LOOT_GENERATOR);
        g.addProvider(e.includeServer(), EROSION_RECIPE_GENERATOR);
        g.addProvider(e.includeServer(), EROSION_ADVANCEMENT_GENERATOR);

        //block and item/server providers
        g.addProvider(e.includeServer(), EROSION_BLOCK_TAG_GENERATOR);
        g.addProvider(e.includeServer(), EROSION_ITEM_TAG_GENERATOR);
        return;
    }
}