package co.bracesoftware.erosion.data;

import co.bracesoftware.erosion.Erosion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Erosion.MODID)//, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators 
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) 
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        //client provider
        generator.addProvider(event.includeClient(), new ModLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, event.getExistingFileHelper()));

        //server providers
        generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, event.getLookupProvider()));

        //block and item
        ModBlockTagProvider b = new ModBlockTagProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), b);

        generator.addProvider(event.includeServer(), new ModItemTagProvider(
            packOutput, event.getLookupProvider(), b.contentsGetter(), event.getExistingFileHelper())
        );
        return;
    }
}