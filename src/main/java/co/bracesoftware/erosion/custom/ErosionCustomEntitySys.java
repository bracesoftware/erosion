package co.bracesoftware.erosion.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.libs.minecraft_text_formatter.Text;

@EventBusSubscriber(modid = Erosion.MODID)
public class ErosionCustomEntitySys
{
    private static final List<Gas> GAS_LIST = new ArrayList<>();

    public static abstract class ErosionCustomEntity
    {
        public String id;
        public String name;
    }

    public static class GasType extends ErosionCustomEntity
    {
        private final int initialdur;
        private final boolean toxic;
        private final int radius;
        private final SimpleParticleType particles;
        private final int particleCount;

        public GasType(
            String i, String n, int in, boolean t,
            int r, SimpleParticleType p, int pc
        )
        {
            this.id = i;
            this.name = n;
            this.initialdur = in;
            this.toxic = t;
            this.radius = r;
            this.particles = p;
            this.particleCount = pc;
        }

        public int getGasParticleCount()
        {
            return this.particleCount;
        }

        public int getInitialDiffusionDuration()
        {
            return this.initialdur;
        }

        public boolean isToxic()
        {
            return this.toxic;
        }

        public int getGasDiffusionRadius()
        {
            return this.radius;
        }

        public SimpleParticleType getParticleType()
        {
            return this.particles;
        }
    }

    public static class Gas
    {
        private final ServerLevel level;
        private final BlockPos pos;
        private final GasType type;

        private int remaining = 100;

        public Gas(ServerLevel l, BlockPos p, GasType t)
        {
            this.pos = p;
            this.type = t;
            this.level = l;

            this.remaining = t.initialdur;
        }

        public BlockPos getPos()
        {
            return this.pos;
        }

        public ServerLevel getLevel()
        {
            return this.level;
        }

        public GasType getType()
        {
            return this.type;
        }

        public int getRemaining()
        {
            return this.remaining;
        }

        public void decreaseRemainingByTick()
        {
            --this.remaining;
        }

        //STATIC METHODS
        public static void createGas(ServerLevel l, BlockPos p, GasType t)
        {
            var g = new Gas(l, p, t);
            GAS_LIST.add(g);

            ErosionUtils.Log(
                "Created gas `" + t.name + "` at -> " + p
            );
            return;
        }

        private static void removeFinishedGases()
        {
            GAS_LIST.removeIf(g -> g.getRemaining() <= 0);
            return;
        }

        private static void spawnGasParticles(ServerLevel l, BlockPos p, GasType t)
        {
            ErosionUtils.Log("Spawning gas particles -> " + p);
            for(int i = 0; i < t.getGasParticleCount(); i++)
            {
                double ox = (l.random.nextDouble() * 2.0 - 1.0) * t.getGasDiffusionRadius();
                double oy = (l.random.nextDouble() * 2.0 - 1.0) * (t.getGasDiffusionRadius() * 0.5);
                double oz = (l.random.nextDouble() * 2.0 - 1.0) * t.getGasDiffusionRadius();

                double x = p.getX() + 0.5 + ox;
                double y = p.getY() + 0.5 + oy;
                double z = p.getZ() + 0.5 + oz;

                l.sendParticles(
                    t.getParticleType(),
                    x, y, z, 1,
                    //whatever these numberz are xd
                    0.0, 0.01, 0.0,0.005
                );
            }
        }
    }

    @SubscribeEvent 
    public static void tick(ServerTickEvent.Post e)
    {
        if(GAS_LIST.isEmpty())
        {
            ErosionUtils.Log("Gas list empty.");
            return;
        }

        var s = e.getServer();

        for(var g : GAS_LIST)
        {
            if(g.getRemaining() <= 0) continue;
            g.decreaseRemainingByTick();

            var l = g.getLevel();
            var pos = g.getPos();
            var ty = g.getType();

            if(l.isLoaded(pos)) continue;

            if(g.getRemaining() % 10 == 0 && ty.isToxic())
            {
                double radius = (double) ty.getGasDiffusionRadius() * ty.getGasDiffusionRadius();
                for(var p : l.players())
                {
                    if(!p.isCreative() && !p.isSpectator())
                    {
                        if(p.blockPosition().distSqr(pos) <= radius)
                        {
                            p.addEffect(new MobEffectInstance(MobEffects.POISON, 200,0));
                            p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200,0));
                            ErosionUtils.displayMessage(
                                p, Text.Format(Text.Col.RED) + "You're being poisoned with " + ty.name
                            );
                        }
                    }
                }
            }
            if(g.getRemaining() % 5 == 0)
            {
                Gas.spawnGasParticles(l, pos, ty);
            }
        }

        Gas.removeFinishedGases();
        return;
    }
}