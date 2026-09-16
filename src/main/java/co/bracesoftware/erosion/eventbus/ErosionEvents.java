package co.bracesoftware.erosion.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.bracesoftware.erosion.ErosionCore;

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

    public static class ErosionBlockEntityRecipeRegistration extends BasicErosionEvent
    {
        public void registerRefinableMaterial(ErosionCore.RefinableMaterial rm)
        {
            ErosionCore.add(rm);
        }

        public void registerAlterableMaterial(ErosionCore.AlterableMaterial am)
        {
            ErosionCore.add(am);
        }

        public void registerCrucibleCatalyst(ErosionCore.CrucibleCatalyst cc)
        {
            ErosionCore.add(cc);
        }

        public void registerChemicalReaction(ErosionCore.ChemicalReaction cr)
        {
            ErosionCore.add(cr);
        }
    }
}