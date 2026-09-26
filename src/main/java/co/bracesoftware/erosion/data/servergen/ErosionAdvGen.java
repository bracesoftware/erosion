package co.bracesoftware.erosion.data.servergen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import co.bracesoftware.erosion.world.ErosionModContentManager;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface;
import co.bracesoftware.erosion.data.ErosionDataGeneratorsProgInterface.ErosionAdvancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ErosionAdvGen extends AdvancementProvider
{
    public ErosionAdvGen(
        PackOutput o, CompletableFuture<HolderLookup.Provider> r,
        ExistingFileHelper efh
    )
    {
        super(o, r, efh, List.of(new Generator()));
    }

    @SuppressWarnings("all")
    public static class Generator implements AdvancementProvider.AdvancementGenerator
    {
        public ExistingFileHelper efh = null;
        public Consumer<AdvancementHolder> k = null;

        @Override
        public void generate(HolderLookup.Provider r, Consumer<AdvancementHolder> s, ExistingFileHelper efh)
        {
            //DO NOT TOUCH
            this.efh = efh;
            this.k = s;

            for(var rrr : ErosionModContentManager.EROSION_ADVANCEMENT_GEN_TASKS)
            {
                rrr.run();
            }

            //GENERATE
            var root = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateParentAdvancement(this);

            // --------------------------- OTHER ADVANCEMENTS
            var salt = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateAdvancement(
                this, "White Stuff",
                "Acquire Salt.",
                ErosionRegistry.Items.SALT.get(),
                ErosionRegistry.RawRegistry.SALT.getId(),
                root
            );
            
            //------------------------- CRUCIBLE ADVANCEMENTS ---------------------------
            var crucible = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateAdvancement(
                this, "A cook!",
                "Craft a Crucible.",
                ErosionRegistry.Items.CRUCIBLE.get(),
                ErosionRegistry.RawRegistry.CRUCIBLE.getId(),
                root
            );

            var crushed_eggz = ErosionAdvancement.generateAdvancement(
                this, "Crushing Eggs",
                "Acquire Crushed Egg Shell.",
                ErosionRegistry.Items.CRUSHED_EGG_SHELL.get(),
                ErosionRegistry.RawRegistry.CRUSHED_EGG_SHELL.getId(),
                crucible
            );

            var invizible_fire = ErosionAdvancement.generateSimpleAdvancement(
                this, ErosionRegistry.RawRegistry.ManualAdvancements.INVISIBLE_FIRE.getName(),
                "Inhale toxic gases.",
                ErosionRegistry.Items.CRUCIBLE.get(),
                ErosionRegistry.RawRegistry.ManualAdvancements.INVISIBLE_FIRE.getId(),
                crucible
            );

            var basic_protection = ErosionAdvancement.generateAdvancement(
                this, "White Cloth over my Face",
                "Acquire a Basic Mask.",
                ErosionRegistry.Items.BASIC_MASK.get(),
                ErosionRegistry.RawRegistry.BASIC_MASK.getId(),
                invizible_fire
            );
            var better_gas_mask = ErosionAdvancement.generateAdvancement(
                this, "A Netherite Ingot for this!?",
                "Acquire a Gas Mask.",
                ErosionRegistry.Items.GAS_MASK.get(),
                ErosionRegistry.RawRegistry.GAS_MASK.getId(),
                basic_protection
            );

            var flux = ErosionAdvancement.generateAdvancement(
                this, "Getting Flux",
                "Acquire Flux.",
                ErosionRegistry.Items.FLUX.get(),
                ErosionRegistry.RawRegistry.FLUX.getId(),
                crushed_eggz
            );

            var borax = ErosionAdvancement.generateAdvancement(
                this, "Better than Flux?",
                "Acquire Borax.",
                ErosionRegistry.Items.BORAX.get(),
                ErosionRegistry.RawRegistry.BORAX.getId(),
                flux
            );

            //------------------------- MATERIAL PURIFIER ADVANCEMENTS ---------------------------

            var purifier = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateAdvancement(
                this, "Purifying Dirt",
                "Craft a Material Purifier.",
                ErosionRegistry.Items.MATERIAL_PURIFIER.get(),
                ErosionRegistry.RawRegistry.MATERIAL_PURIFIER.getId(),
                root
            );

            //------------------------- CHEMICAL REACTOR ADVANCEMENTS ---------------------------

            var reactor = ErosionDataGeneratorsProgInterface.ErosionAdvancement.generateAdvancement(
                this, "Breaking Bad!",
                "Craft a Chemical Reactor.",
                ErosionRegistry.Items.CHEMICAL_REACTOR.get(),
                ErosionRegistry.RawRegistry.CHEMICAL_REACTOR.getId(),
                root
            );

            var boric_acid = ErosionAdvancement.generateAdvancement(
                this, "Is that crystal?!",
                "Synthesize Boric Acid crystals.",
                ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get(),
                ErosionRegistry.RawRegistry.BORIC_ACID_CRYSTAL.getId(),
                reactor
            );

            var reactor_scrubber = ErosionAdvancement.generateAdvancement(
                this, "Better than a Gas Mask?",
                "Acquire a Chemical Reactor Scrubber.",
                ErosionRegistry.Items.CHEMICAL_REACTOR_SCRUBBER.get(),
                ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_SCRUBBER.getId(),
                reactor
            );
        
            var gas_filter = ErosionAdvancement.generateAdvancement(
                this, "Gotta have a supply of this...",
                "Acquire a Gas Filter.",
                ErosionRegistry.Items.GAS_FILTER.get(),
                ErosionRegistry.RawRegistry.GAS_FILTER.getId(),
                reactor_scrubber
            );

            var reactor_module = ErosionAdvancement.generateAdvancement(
                this, "Expanding the Empire!",
                "Acquire a Chemical Reactor Module.",
                ErosionRegistry.Items.CHEMICAL_REACTOR_MODULE.get(),
                ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId(),
                reactor
            );
            var reactor_cooling_sys = ErosionAdvancement.generateAdvancement(
                this, "Have to Cool Things Down",
                "Acquire a Chemical Reactor Cooling System.",
                ErosionRegistry.Items.CHEMICAL_REACTOR_COOLING_SYSTEM.get(),
                ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId(),
                reactor_scrubber
            );
            return;
        }
    }
}