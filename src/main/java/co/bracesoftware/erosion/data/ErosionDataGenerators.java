package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.data.clientgen.ErosionBlockStateGen;
import co.bracesoftware.erosion.data.clientgen.ErosionItemModelGen;
import co.bracesoftware.erosion.data.clientgen.ErosionLang;
import co.bracesoftware.erosion.data.commongen.ErosionDataGenInternal;
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
    public static void gatherData(GatherDataEvent e) 
    {
        DataGenerator generator = e.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        
        //other stuff
        ErosionDataGenInternal.generateChemicalReactorAnim();

        //client provider
        generator.addProvider(e.includeClient(), new ErosionLang(packOutput));
        generator.addProvider(e.includeClient(), new ErosionBlockStateGen(packOutput, e.getExistingFileHelper()));
        generator.addProvider(e.includeClient(), new ErosionItemModelGen(packOutput, e.getExistingFileHelper()));

        //server providers
        generator.addProvider(e.includeServer(), new ErosionLootGen(packOutput, e.getLookupProvider()));
        generator.addProvider(e.includeServer(), new ErosionRecipeGen(packOutput, e.getLookupProvider()));
        generator.addProvider(e.includeServer(), new ErosionAdvGen(packOutput, e.getLookupProvider(), e.getExistingFileHelper()));

        //block and item
        var b = new ErosionBlockTagGen(packOutput, e.getLookupProvider(), e.getExistingFileHelper());
        generator.addProvider(e.includeServer(), b);

        generator.addProvider(e.includeServer(), new ErosionItemTagGen(
            packOutput, e.getLookupProvider(), b.contentsGetter(), e.getExistingFileHelper())
        );
        return;
    }
}