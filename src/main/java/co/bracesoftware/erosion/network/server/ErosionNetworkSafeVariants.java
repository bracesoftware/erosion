package co.bracesoftware.erosion.network.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.checkerframework.checker.units.qual.A;

import com.mojang.serialization.MapCodec;

import co.bracesoftware.erosion.ErosionExceptions.ErosionBlockExceptions.ErosionNetworkSafeBlockException;
import co.bracesoftware.erosion.ErosionExceptions.ErosionException;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.libs.chrono.Task;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ErosionNetworkSafeVariants
{
    public static abstract class ErosionNetworkSafeBlockEntity<T> extends BlockEntity
    {
        private int tickAge = 0;
        public ErosionNetworkSafeBlockEntity(BlockEntityType<?> b, BlockPos pos, BlockState state)
        {
            super(b,pos,state);
        }

        public static final class ErosionBlockEntityTickPacket
        {
            private final ServerLevel serverLevel;
            private final BlockPos blockPos;
            private final BlockState blockState;

            public ErosionBlockEntityTickPacket(
                ServerLevel l, BlockPos bp, BlockState bs
            )
            {
                this.serverLevel = l;
                this.blockPos = bp;
                this.blockState = bs;
            }

            public ServerLevel getServerLevel() { return this.serverLevel; }
            public BlockPos getBlockPos() { return this.blockPos; }
            public BlockState getBlockState() { return this.blockState; }
        }

        public int getEntityAgeInTicks() { return this.tickAge; }

        public boolean onBlockEntityTickOnServer(
            T e, ErosionBlockEntityTickPacket p
        ) throws ErosionException
        {
            return false;
        }

        @SuppressWarnings("all")
        public static void tick(Level l, BlockPos bp, BlockState bs, ErosionNetworkSafeBlockEntity e)
        {
            e.tickAge++;
            if(!l.isClientSide())
            {
                boolean result = e.onBlockEntityTickOnServer(e, new ErosionBlockEntityTickPacket((ServerLevel) l, bp, bs));
                if(!result)
                {
                    throw new ErosionNetworkSafeBlockException("Why false?");
                }
            }
            return;
        }
    }
    @SuppressWarnings("all")
    public static abstract class ErosionNetworkSafeBaseEntityBlock<T> extends ErosionNetworkSafeBlock implements EntityBlock
    {
        private final MapCodec<T> codecHolder;
        public final Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>> networkSafeBlockEntityType;
        public ErosionNetworkSafeBaseEntityBlock(
            Block.Properties p, 
            Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>> t,
            Function<Properties, T> codecBuilder
        )
        {
            super(p);
            this.networkSafeBlockEntityType = t;
            this.codecHolder = MapCodec.unit(() -> codecBuilder.apply(p));
        }

        @Nullable 
        @Override 
        public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState s, BlockEntityType<T> type)
        {
            return level.isClientSide() ? null : createTickerHelper(
                type, this.networkSafeBlockEntityType.get(), ErosionNetworkSafeBlockEntity::tick
            );
        }

        @Override protected final MapCodec<? extends ErosionNetworkSafeBaseEntityBlock<T>> codec()
        {
            return (MapCodec) this.codecHolder;
        }

        // =========================== DO NOT TOUCH!
        //Theze are function overrides ported from BaseEntityBlock,put adapted to my network-safe variant
        @Override protected final RenderShape getRenderShape(BlockState state)
        {
            return RenderShape.MODEL;
        }
        @Override protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
            super.triggerEvent(state, level, pos, id, param);
            BlockEntity blockentity = level.getBlockEntity(pos);
            return blockentity == null ? false : blockentity.triggerEvent(id, param);
        }
        @Nullable @Override protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            return blockentity instanceof MenuProvider ? (MenuProvider)blockentity : null;
        }
        @Nullable protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
            return clientType == serverType ? (BlockEntityTicker<A>)ticker : null;
        }
    }

    public static abstract class ErosionNetworkSafeBlock extends Block
    {
        public static enum RandomTickFrequency
        {
            VERY_LOW(1000), LOW(500), MEDIUM(250),
            HIGH(100), VERY_HIGH(20);

            private final int delay;
            RandomTickFrequency(int when)
            {
                this.delay = when;
            }

            public int getDelay() { return this.delay; }
        }

        private record RandomTickSetupPacket(ResourceKey<Level> level, long pos)
        {
            public RandomTickSetupPacket(ServerLevel level, BlockPos pos)
            {
                this(level.dimension(), pos.asLong());
            }
        }

        private static final Map<RandomTickSetupPacket, Boolean> RANDOM_TICK_SET_UP = new HashMap<>();
        private boolean callUseItemOnOnlyFlag = false;
        public static final List<Class<? extends Block>> PLACEABLE_BLOCKS = new ArrayList<>();
        private RandomTickFrequency randomTickFrequency = RandomTickFrequency.VERY_LOW;

        private ErosionNetworkSafeBlockSidedLogic SERVER = new ErosionNetworkSafeBlockSidedLogic();
        private ErosionNetworkSafeBlockSidedLogic CLIENT = new ErosionNetworkSafeBlockSidedLogic();
        private ErosionNetworkSafeBlockSidedLogic COMMON = new ErosionNetworkSafeBlockSidedLogic();
        public ErosionNetworkSafeBlock(Block.Properties p)
        {
            super(p);
        }

        private final boolean isRandomTickSysSetUpFor(ServerLevel l, BlockPos bp)
        {
            return RANDOM_TICK_SET_UP.getOrDefault(new RandomTickSetupPacket(l,bp), false);
        }

        private final void discardRandomTickSysFor(ServerLevel l, BlockPos bp)
        {
            var p = new RandomTickSetupPacket(l, bp);
            if(RANDOM_TICK_SET_UP.containsKey(p)) RANDOM_TICK_SET_UP.remove(p);
        }

        private final void setRandomTickSysSetUpFor(ServerLevel l, BlockPos bp, boolean what)
        {
            var p = new RandomTickSetupPacket(l, bp);
            RANDOM_TICK_SET_UP.put(p, what);
        }
        
        // ====================API===================== // 
        public static final class ErosionBlockInteractionPacket
        {
            private final ItemStack itemStack;
            private final BlockState blockState;
            private final Level level;
            private final BlockPos blockPos;
            private final Player player;
            private final InteractionHand interactionHand;
            private final BlockHitResult blockHitResult;
            private final Block block;
            private final ErosionNetworkSafeBlock thisPtr;

            public ErosionBlockInteractionPacket(
                ErosionNetworkSafeBlock tp,
                ItemStack is, BlockState bs, Level l, BlockPos bp,
                Player p, InteractionHand hand, BlockHitResult hr,
                Block b
            )
            {
                this.thisPtr = tp;
                this.itemStack = is;
                this.blockState = bs;
                this.level = l;
                this.blockPos = bp;
                this.player = p;
                this.interactionHand = hand;
                this.blockHitResult = hr;
                this.block = b;
            }

            public ErosionBlockInteractionPacket toServerPacket()
            {
                return this;
            }

            public ErosionBlockInteractionPacket toCommonOrClientPacket()
            {
                return this;
            }

            public Block getThisPtr() { return this.thisPtr; }
            public Block getBlockClassInfo() { return this.block; }
            public ItemStack getItemStack() { return this.itemStack; }
            public BlockState getBlockState() { return this.blockState; }
            public BlockPos getBlockPos() { return this.blockPos; }
            public InteractionHand getInteractionHand() { return this.interactionHand; }
            public BlockHitResult getBlockHitResult() { return this.blockHitResult; }
            public Player getPlayer() { return this.player; }
            public Level getLevel() { return this.level; }
            public ServerLevel getServerLevel() throws ErosionNetworkSafeBlockException
            {
                if(this.level instanceof ServerLevel l)
                {
                    return l;
                }
                throw new ErosionNetworkSafeBlockException("Tried to access the server memory from the client");
            }
            public ServerPlayer getServerPlayer() throws ErosionNetworkSafeBlockException
            {
                if(this.player instanceof ServerPlayer p)
                {
                    return p;
                }
                throw new ErosionNetworkSafeBlockException("Tried to access the server memory from the client");
            }
        }

        public static class ErosionNetworkSafeBlockSidedLogic
        {
            public ErosionNetworkSafeBlockSidedLogic() {}
            // -------------------------------------------------------------------- //
            public boolean useItemOn(ErosionBlockInteractionPacket p)
            {
                return false;
            }
            // -------------------------------------------------------------------- //
            public boolean useWithoutItem(ErosionBlockInteractionPacket p)
            {
                return false;
            }
            // -------------------------------------------------------------------- //
            public boolean onAttemptToPlaceBlock(ErosionBlockInteractionPacket p)
            {
                return false;
            }
            // -------------------------------------------------------------------- //
            public void onInteractionFail(ErosionBlockInteractionPacket p)
            {
                return;
            }
            // -------------------------------------------------------------------- //
            public void onRandomTick(ErosionBlockInteractionPacket p)
            {
                return;
            }
        }

        public final void callUseItemOnOnly(boolean cfg)
        {
            this.callUseItemOnOnlyFlag = cfg;
        }

        public final void setRandomTickFrequency(RandomTickFrequency f)
        {
            this.randomTickFrequency = f;
        }

        public final RandomTickFrequency getRandomTickFrequency()
        {
            return this.randomTickFrequency;
        }

        public final void setServerLogic(ErosionNetworkSafeBlockSidedLogic bl)
        {
            this.SERVER = bl;
        }
        public final void setCommonLogic(ErosionNetworkSafeBlockSidedLogic bl)
        {
            this.COMMON = bl;
        }
        public final void setClientLogic(ErosionNetworkSafeBlockSidedLogic bl)
        {
            this.CLIENT = bl;
        }

        @SuppressWarnings("all") 
        private final void setPlaceableBlocks(Class<? extends Block>... l)
        {
            PLACEABLE_BLOCKS.addAll(Arrays.asList(l));
        }
        // ============================================ //
        @SuppressWarnings("all")
        private static <T> T booleanToInteractionResult(
            Class<T> c, boolean s
        ) throws ErosionNetworkSafeBlockException
        {
            if(c == ItemInteractionResult.class) return (T) (s ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME);
            else if(c == InteractionResult.class) return (T) (s ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
            
            throw new ErosionNetworkSafeBlockException("Unsupported class to convert boolean to: " + c.getName());
        }

        @Override protected final ItemInteractionResult useItemOn(
            ItemStack stack, BlockState bs, Level leva,
            BlockPos bp, Player playa, InteractionHand hand,
            BlockHitResult hr
        )
        {
            if(stack.getItem() instanceof BlockItem it)
            {
                boolean result = false;
                var packet = new ErosionBlockInteractionPacket(this,stack, bs, leva, bp, playa, hand, hr, it.getBlock());
                result = this.COMMON.onAttemptToPlaceBlock(
                    packet.toCommonOrClientPacket()
                );
                if(result)
                {
                    if(leva.isClientSide()) this.CLIENT.onAttemptToPlaceBlock(
                        packet.toCommonOrClientPacket()
                    );
                    else this.SERVER.onAttemptToPlaceBlock(
                        packet.toServerPacket()
                    );
                }
                if(result) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if(!leva.isClientSide())
            {
                boolean result = false;

                if(stack.isEmpty() && !this.callUseItemOnOnlyFlag) result = this.SERVER.useWithoutItem(
                    new ErosionBlockInteractionPacket(
                        this,null, bs, leva, bp, playa, null, hr,this
                    ).toServerPacket()
                );
                else result = this.SERVER.useItemOn(new ErosionBlockInteractionPacket(
                    this,stack, bs, leva, bp, playa, hand, hr,this
                ));
                
                if(!result)
                {
                    this.SERVER.onInteractionFail(
                        new ErosionBlockInteractionPacket(
                            this,null, bs, leva, bp, playa, null, null,this
                        ).toServerPacket()
                    );
                }
            }
            //super.useItemOn(stack, s, l, bp, p, hand, hr);
            return ItemInteractionResult.SUCCESS;
        }

        @Override
        public final void tick(BlockState bs, ServerLevel l, BlockPos bp, RandomSource r) {}
        @Override public final void randomTick(BlockState bs, ServerLevel l, BlockPos bp, RandomSource r)
        {
            super.tick(bs, l, bp, r);
            if(!this.isRandomTickSysSetUpFor(l, bp))
            {
                this.setRandomTickSysSetUpFor(l,bp,true);
                tickManager(bs, l, bp);
            }
            return;
        }

        private final void tickManager(BlockState bs, ServerLevel l, BlockPos bp)
        {
            if(!l.isLoaded(bp))
            {
                this.discardRandomTickSysFor(l, bp);
                return;
            }

            var cbs = l.getBlockState(bp);
            if(!cbs.is(this))
            {
                this.discardRandomTickSysFor(l, bp);
                return;
            }
            this.SERVER.onRandomTick(
                new ErosionBlockInteractionPacket(this,null, cbs, l, bp, null, null, null, this)
                .toServerPacket()
            );
            var d = this.getRandomTickFrequency().getDelay();
            Task.schedule(d + ErosionMod.RANDOM.nextInt(d), () -> {
                tickManager(cbs,l,bp);
            });
        }

        
        @Override protected final InteractionResult useWithoutItem(
            BlockState bs, Level leva, BlockPos bp,
            Player playa, BlockHitResult hr
        )
        {
            return super.useWithoutItem(bs, leva, bp, playa, hr);
        }
        
    }
}