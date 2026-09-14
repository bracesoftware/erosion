package co.bracesoftware.erosion.eventbus;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import co.bracesoftware.erosion.*;

public class ErosionEventBus
{
    private static final List<Consumer<ErosionEvents.ErosionBlockEntityRecipeRegistration>> EROSION_RECIPE_REG_LISTENERS = new ArrayList<>();

    public static void registerListeners(Class<?> c) throws RuntimeException
    {
        ErosionUtils.Log("Registering class -> " + c.getName());
        for(var m : c.getDeclaredMethods())
        {
            if(
                m.getParameterCount() == 1 &&
                m.isAnnotationPresent(ErosionEvents.ErosionEventSubscribe.class)
            )
            {
                if(!Modifier.isStatic(m.getModifiers()))
                {
                    throw new RuntimeException("Event subscriber has to be a static method.");
                }
                var par = m.getParameterTypes()[0];
                //BLOCK ENTITY RECIPE REGISTRATION
                if(par == ErosionEvents.ErosionBlockEntityRecipeRegistration.class)
                {
                    EROSION_RECIPE_REG_LISTENERS.add(
                        p -> {
                            try { m.invoke(null, p); }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                            ErosionUtils.Log("Successfully subscribed method to `" + par.getName() + "`: " + m.getName());
                        }
                    );
                    continue;
                }                
            }
        }
        return;
    }

    public static class ErosionEventInvocation
    {
        public static void CALL_BE_RECIPE_REG(
            ErosionEvents.ErosionBlockEntityRecipeRegistration p
        ) throws RuntimeException
        {
            if(EROSION_RECIPE_REG_LISTENERS.isEmpty()) return;
            for(var e : EROSION_RECIPE_REG_LISTENERS)
            {
                if(p.cancelled)
                {
                    p.cancelled = false;
                    break;
                }
                e.accept(p);
            }
            return;
        }
    }
}