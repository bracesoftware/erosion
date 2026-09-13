package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Erosion.MODID)
public class DataGenerators 
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) 
    {
        DataGenerator generator = e.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        //client provider
        generator.addProvider(e.includeClient(), new ModLanguageProvider(packOutput));
        generator.addProvider(e.includeClient(), new ModBlockStateProvider(packOutput, e.getExistingFileHelper()));
        generator.addProvider(e.includeClient(), new ModItemModelProvider(packOutput, e.getExistingFileHelper()));

        //server providers
        generator.addProvider(e.includeServer(), new ModLootTableProvider(packOutput, e.getLookupProvider()));
        generator.addProvider(e.includeServer(), new ModRecipeProvider(packOutput, e.getLookupProvider()));
        generator.addProvider(e.includeServer(), new ModAdvancementProvider(packOutput, e.getLookupProvider(), e.getExistingFileHelper()));

        //block and item
        ModBlockTagProvider b = new ModBlockTagProvider(packOutput, e.getLookupProvider(), e.getExistingFileHelper());
        generator.addProvider(e.includeServer(), b);

        generator.addProvider(e.includeServer(), new ModItemTagProvider(
            packOutput, e.getLookupProvider(), b.contentsGetter(), e.getExistingFileHelper())
        );
        return;
    }
}