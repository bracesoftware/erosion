package co.bracesoftware.erosion.api.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class ErosionEvents
{
    //@annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ErosionEventSubscribe {}

    //different types
    public static abstract class BasicErosionEvent
    {
        public boolean cancelled = false;
        public void cancel()
        {
            this.cancelled = true;
        }
    }

    public static final class ErosionModLoading extends BasicErosionEvent
    {
        public final void sayHi()
        {
            ErosionUtils.Log("Someone said 'Hi'!");
        }
    }

    public static final class ErosionItemDescription extends BasicErosionEvent
    {
        private List<Component> list = null;
        private Item item = null;

        public ErosionItemDescription(Item i, List<Component> l)
        {
            this.item = i;
            this.list = l;
        }

        public final Item whatItem()
        {
            return this.item;
        }

        public final void addItemDescription(Component c)
        {
            this.list.add(c);
        }
    }

    public static final class ErosionBlockEntityRecipeRegistration extends BasicErosionEvent
    {
        public final void registerRefinableMaterial(ErosionCore.RefinableMaterial rm)
        {
            ErosionCore.add(rm);
        }

        public final void registerAlterableMaterial(ErosionCore.AlterableMaterial am)
        {
            ErosionCore.add(am);
        }

        public final void registerCrucibleCatalyst(ErosionCore.CrucibleCatalyst cc)
        {
            ErosionCore.add(cc);
        }

        public final void registerChemicalReaction(ErosionCore.ChemicalReaction cr)
        {
            ErosionCore.add(cr);
        }

        public final void registerChemicalReactorCoolingFluid(ErosionCore.ChemicalReactorCoolingFluid f)
        {
            ErosionCore.add(f);
        }

        public final void registerSaltableFood(ErosionCore.SaltableFood f)
        {
            ErosionCore.add(f);
        }
    }
}