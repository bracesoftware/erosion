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
        
        //other stuff
        ErosionDataGeneratorsProgInterface.ErosionBlockState.generateCustomTextures();
        ErosionBlockAnimGen.generateChemicalReactorAnims();

        //client provider
        g.addProvider(e.includeClient(), new ErosionLang(p));
        g.addProvider(e.includeClient(), new ErosionBlockStateGen(p, e.getExistingFileHelper()));
        g.addProvider(e.includeClient(), new ErosionItemModelGen(p, e.getExistingFileHelper()));
        g.addProvider(e.includeClient(), new ErosionSoundGen(p, e.getExistingFileHelper()));

        //server providers
        g.addProvider(e.includeServer(), new ErosionLootGen(p, e.getLookupProvider()));
        g.addProvider(e.includeServer(), new ErosionRecipeGen(p, e.getLookupProvider()));
        g.addProvider(e.includeServer(), new ErosionAdvGen(p, e.getLookupProvider(), e.getExistingFileHelper()));

        //block and item
        var b = new ErosionBlockTagGen(p, e.getLookupProvider(), e.getExistingFileHelper());
        g.addProvider(e.includeServer(), b);

        g.addProvider(e.includeServer(), new ErosionItemTagGen(
            p, e.getLookupProvider(), b.contentsGetter(), e.getExistingFileHelper())
        );
        return;
    }
}