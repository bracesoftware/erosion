package co.bracesoftware.libs.chrono;

import java.util.ArrayList;
import java.util.List;

public class Task
{
    private static final List<Task> PENDING = new ArrayList<>();
    private static final List<Task> ACTIVE = new ArrayList<>();

    private int delay;
    private Runnable task;

    public Task(int d, Runnable t)
    {
        this.delay = d;
        this.task = t;
    }

    public final void complete()
    {
        this.delay = 0;
        this.task.run();
    }

    public final boolean isCompleted()
    {
        return this.delay <= 0;
    }

    public final int getDelay()
    {
        return this.delay;
    }

    public final void weAreAlmostThere()
    {
        --this.delay;
    }

    public static void schedule(int d, Runnable t)
    {
        PENDING.add(new Task(d,t));
        return;
    }

    //call dis on every tick
    public static void processPending()
    {
        if(!PENDING.isEmpty())
        {
            ACTIVE.addAll(PENDING);
            PENDING.clear();
        }

        for(int i = 0; i < ACTIVE.size(); i++)
        {
            var t = ACTIVE.get(i);
            if(t.isCompleted()) continue;
            t.weAreAlmostThere();
            if(t.getDelay() <= 0) t.complete();
        }

        ACTIVE.removeIf(Task::isCompleted);
        return;
    }
}