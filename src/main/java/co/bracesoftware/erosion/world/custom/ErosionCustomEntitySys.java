package co.bracesoftware.erosion.world.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.ArrayList;
import java.util.List;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionExceptions.ErosionCustomEntityExceptions.ErosionGasInitException;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionClient.ErosionScreenMessage;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.items.ErosionSimpleItems.GasMask;
import co.bracesoftware.libs.minecraft_text_formatter.Text;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

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
        private final List<Holder<MobEffect>> effects;

        public GasType(
            String i, String n, int in, boolean t,
            int r, SimpleParticleType p, int pc,
            List<Holder<MobEffect>> e
        ) throws ErosionGasInitException
        {
            this.id = i;
            this.name = n;
            this.initialdur = in;
            this.toxic = t;
            this.radius = r;
            this.particles = p;
            this.effects = e;
            this.particleCount = pc;

            this.validateGas();
        }

        private void validateGas() throws ErosionGasInitException
        {
            if(this.isToxic() && this.getGasEffects().isEmpty())
            {
                throw new ErosionGasInitException("Gas marked as toxic but effect list is empty -> " + this.name);
            }
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
        
        public List<Holder<MobEffect>> getGasEffects()
        {
            return this.effects;
        }
    }

    public static class Gas
    {
        public static final LongSet USED_POSITIONS = new LongOpenHashSet();

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
            if(!USED_POSITIONS.add(p.asLong()))
            {
                return;
            }
            var g = new Gas(l, p, t);
            GAS_LIST.add(g);

            if(ErosionConfig.ErosionDebugger.CRAZY_DEBUG_MODE)
            {
                ErosionUtils.Log(
                    "Created gas `" + t.name + "` at -> " + p
                );
            }
            return;
        }

        private static void removeFinishedGases()
        {
            GAS_LIST.removeIf(g -> {
                boolean what = g.getRemaining() <= 0;
                if(what)
                {
                    USED_POSITIONS.remove(g.getPos().asLong());
                }
                return what;
            });
            return;
        }

        public static void applyGasEffects(LivingEntity entity, GasType ty)
        {
            var s = entity.getItemBySlot(EquipmentSlot.HEAD);
            var blockEffects = false;
            if(s.getItem() instanceof GasMask git)
            {
                blockEffects = ErosionUtils.Misc.randomWithChanceToBe(
                    true, git.getQuality().getSuccessRate()
                );
            }
            //=======================////////
            if(blockEffects) return;
            //any entity in radius gets effect
            if(ty.isToxic()) for(var f : ty.getGasEffects())
            {
                if(entity instanceof ServerPlayer p)
                {
                    if(p.isCreative() || p.isSpectator()) return;
                }
                entity.addEffect(new MobEffectInstance(f, 200, 0));
            }
            //if it is a player, send messages to warn
            if(entity instanceof ServerPlayer p)
            {
                if(p.isCreative() || p.isSpectator()) return;
                if(ty.isToxic())
                {
                    ErosionUtils.displayMessage(
                        p, "You're being poisoned with " + ty.name,
                        ErosionScreenMessage.Colors.RED
                    );

                    ErosionUtils.Misc.grantAdvancement(
                        p, ResourceLocation.fromNamespaceAndPath(
                            Erosion.MODID, ErosionRegistry.RawRegistry.ManualAdvancements.INVISIBLE_FIRE.getId()
                        )
                    );
                }
                else
                {
                    ErosionUtils.displayMessage(
                        p, "You're inhaling " + ty.name,
                        ErosionScreenMessage.Colors.GRAY
                    );
                }
                return;
            }
            
            return;
        }

        private static void renderGasParticles(ServerLevel l, BlockPos p, GasType t)
        {
            if(ErosionConfig.ErosionDebugger.CRAZY_DEBUG_MODE)
            {
                ErosionUtils.Log("Spawning gas particles -> " + p);
            }
            
            for(int i = 0; i < t.getGasParticleCount(); i++)
            {
                double ox = (l.random.nextDouble() * 2.0 - 1.0) * t.getGasDiffusionRadius();
                double oy = (l.random.nextDouble() * 2.0 - 1.0) * (t.getGasDiffusionRadius() * 0.5);
                double oz = (l.random.nextDouble() * 2.0 - 1.0) * t.getGasDiffusionRadius();

                double x = p.getX() + 0.5 + ox;
                double y = p.getY() + 0.5 + oy;
                double z = p.getZ() + 0.5 + oz;

                ClientboundLevelParticlesPacket pp = new ClientboundLevelParticlesPacket(
                    t.getParticleType(), true, x,y,z,
                    0.0f,0.0f,0.0f,0.005f,1
                );

                l.getChunkSource().chunkMap.getPlayers(
                    new ChunkPos(BlockPos.containing(x, y, z)), false
                ).forEach(pl -> pl.connection.send(pp));
            }
        }
    }

    @SubscribeEvent 
    public static void tick(ServerTickEvent.Post e)
    {
        if(GAS_LIST.isEmpty())
        {
            if(ErosionConfig.ErosionDebugger.CRAZY_DEBUG_MODE)
            {
                ErosionUtils.Log("Gas list empty.");
            }
            return;
        }

        var s = e.getServer();

        if(ErosionConfig.ErosionDebugger.CRAZY_DEBUG_MODE) ErosionUtils.Log(
            "Processing gases; total of -> " + GAS_LIST.size()
        );

        for(var g : GAS_LIST)
        {
            if(g.getRemaining() <= 0) continue;
            g.decreaseRemainingByTick();

            var l = g.getLevel();
            var pos = g.getPos();
            var ty = g.getType();

            if(!l.isLoaded(pos)) continue;

            if(g.getRemaining() % 10 == 0)
            {
                var ents = l.getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(pos).inflate(ty.getGasDiffusionRadius())
                );
                for(var p : ents)
                {
                    Gas.applyGasEffects(p, ty);
                }
            }
            if(g.getRemaining() % 5 == 0)
            {
                Gas.renderGasParticles(l, pos, ty);
            }
        }

        Gas.removeFinishedGases();
        return;
    }
}