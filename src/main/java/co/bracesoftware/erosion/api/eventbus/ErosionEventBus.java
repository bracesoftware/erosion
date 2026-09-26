package co.bracesoftware.erosion.api.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionExceptions.ErosionEventBusException;
import co.bracesoftware.erosion.api.eventbus.ErosionEvents.BasicErosionEvent;

public class ErosionEventBus
{
    private static final Map<
        Class<? extends BasicErosionEvent>,
        List<Consumer<BasicErosionEvent>
    >> LISTENERS = new ConcurrentHashMap<>();

    public static void registerListeners(
        Class<?> c
    ) throws ErosionEventBusException
    {
        ErosionUtils.Log("Registering class -> " + c.getName());

        for(Method m : c.getDeclaredMethods())
        {
            if(
                m.getParameterCount() == 1 &&
                m.isAnnotationPresent(ErosionEvents.ErosionEventSubscribe.class)
            )
            {
                if(!Modifier.isStatic(m.getModifiers()))
                {
                    throw new ErosionEventBusException("Event subscriber has to be a static method: " + m.getName());
                }

                Class<?> p = m.getParameterTypes()[0];

                if(!BasicErosionEvent.class.isAssignableFrom(p))
                {
                    throw new ErosionEventBusException("Parameter must extend BasicErosionEvent in method: " + m.getName());
                }

                @SuppressWarnings("unchecked")
                Class<? extends BasicErosionEvent> ec = (Class<? extends BasicErosionEvent>) p;

                m.setAccessible(true);

                LISTENERS.computeIfAbsent(
                    ec, k -> new ArrayList<>()
                ).add(a -> {
                        try { m.invoke(null, a); }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                );

                ErosionUtils.Log("Successfully subscribed method `" + m.getName() + "` to event `" + ec.getName() + "`");
            }
        }
        return;
    }

    public static class ErosionEventInvocation
    {
        public static<T extends BasicErosionEvent> void CALL_EVENT_LISTENERS(
            T e
        ) throws ErosionEventBusException
        {
            if(e == null) return;

            List<Consumer<BasicErosionEvent>> l = LISTENERS.get(e.getClass());
            if(l == null || l.isEmpty())
            {
                ErosionUtils.Log("There are no listeners found for -> " + e.getClass().getName());
                return;
            }

            for(var ll : l)
            {
                if(e.cancelled)
                {
                    e.cancelled = false;
                    break;
                }

                ll.accept(e);
            }
            return;
        }
    }
}