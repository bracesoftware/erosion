package co.bracesoftware.erosion.data.clientgen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.world.ErosionModContentManager;
import co.bracesoftware.erosion.world.ErosionRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ErosionSoundGen extends SoundDefinitionsProvider
{
    public ErosionSoundGen(PackOutput o, ExistingFileHelper efh)
    {
        super(o, Erosion.MODID, efh);
    }

    @Override
    public void registerSounds()
    {
        ErosionUtils.Log("Generating -> sounds.json");

        for(var r : ErosionModContentManager.EROSION_SOUND_GEN_TASKS)
        {
            r.run();
        }

        this.add(ErosionRegistry.SoundEvents.ORE_MINE.get(), definition()
            .with(sound(ResourceLocation.fromNamespaceAndPath(
                Erosion.MODID, ErosionRegistry.RawRegistry.ORE_MINE.getId()
            )))
            .subtitle(ErosionRegistry.RawRegistry.ORE_MINE.getName())
        );

        this.add(ErosionRegistry.SoundEvents.ORE_PLACE.get(), definition()
            .with(sound(ResourceLocation.fromNamespaceAndPath(
                Erosion.MODID, ErosionRegistry.RawRegistry.ORE_PLACE.getId()
            )))
            .subtitle(ErosionRegistry.RawRegistry.ORE_PLACE.getName())
        );

        this.add(ErosionRegistry.SoundEvents.CRUCIBLE_MELTING.get(), definition()
            .with(sound(ResourceLocation.fromNamespaceAndPath(
                Erosion.MODID, ErosionRegistry.RawRegistry.CRUCIBLE_MELTING.getId()
            )))
            .subtitle(ErosionRegistry.RawRegistry.CRUCIBLE_MELTING.getName())
        );

        this.add(ErosionRegistry.SoundEvents.ROCK.get(), definition()
            .with(sound(ResourceLocation.fromNamespaceAndPath(
                Erosion.MODID, ErosionRegistry.RawRegistry.ROCK.getId()
            )))
            .subtitle(ErosionRegistry.RawRegistry.ROCK.getName())
        );

        return;
    }
}