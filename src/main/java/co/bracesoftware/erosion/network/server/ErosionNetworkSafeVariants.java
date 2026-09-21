package co.bracesoftware.erosion.network.server;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.checkerframework.checker.units.qual.A;

import com.mojang.serialization.MapCodec;

import co.bracesoftware.erosion.ErosionExceptions.ErosionBlockExceptions.ErosionNetworkSafeBlockException;
import co.bracesoftware.erosion.ErosionExceptions.ErosionException;
import co.bracesoftware.erosion.world.blocks.material_purifier.MaterialPurifierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
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

        public boolean onBlockEntityTickOnServer(
            T e, ErosionBlockEntityTickPacket p
        ) throws ErosionException
        {
            return false;
        }

        public static void tick(Level l, BlockPos bp, BlockState bs, ErosionNetworkSafeBlockEntity e)
        {
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
    public static abstract class ErosionNetworkSafeBaseEntityBlock<T> extends ErosionNetworkSafeBlock implements EntityBlock
    {
        private final MapCodec<T> codecHolder;
        public final Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity>> networkSafeBlockEntityType;
        public ErosionNetworkSafeBaseEntityBlock(
            Block.Properties p, 
            Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity>> t,
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
        private boolean callUseItemOnOnlyFlag = false;
        public ErosionNetworkSafeBlock(Block.Properties p)
        {
            super(p);
        }

        public final void callUseItemOnOnly(boolean cfg)
        {
            this.callUseItemOnOnlyFlag = cfg;
        }
        // ====================API===================== // 
        public static final class ErosionBlockInteractionPacket
        {
            private final ItemStack itemStack;
            private final BlockState blockState;
            private final ServerLevel serverLevel;
            private final BlockPos blockPos;
            private final ServerPlayer serverPlayer;
            private final InteractionHand interactionHand;
            private final BlockHitResult blockHitResult;

            public ErosionBlockInteractionPacket(
                ItemStack is, BlockState bs, ServerLevel l, BlockPos bp,
                ServerPlayer p, InteractionHand hand, BlockHitResult hr
            )
            {
                this.itemStack = is;
                this.blockState = bs;
                this.serverLevel = l;
                this.blockPos = bp;
                this.serverPlayer = p;
                this.interactionHand = hand;
                this.blockHitResult = hr;
            }

            public ItemStack getItemStack() { return this.itemStack; }
            public BlockState getBlockState() { return this.blockState; }
            public ServerLevel getServerLevel() { return this.serverLevel; }
            public BlockPos getBlockPos() { return this.blockPos; }
            public ServerPlayer getServerPlayer() { return this.serverPlayer; }
            public InteractionHand getInteractionHand() { return this.interactionHand; }
            public BlockHitResult getBlockHitResult() { return this.blockHitResult; }
        }

        public boolean serverUseItemOn(ErosionBlockInteractionPacket p)
        {
            return false;
        }
        public boolean serverUseWithoutItem(ErosionBlockInteractionPacket p)
        {
            return false;
        }

        public void onInteractionFail(ErosionBlockInteractionPacket p)
        {
            return;
        }
        // ============================================ //
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
            if(!leva.isClientSide())
            {
                var p = (ServerPlayer) playa;
                var l = (ServerLevel) leva;
                boolean result = false;

                if(stack.isEmpty() && !this.callUseItemOnOnlyFlag) result = this.serverUseWithoutItem(new ErosionBlockInteractionPacket(
                    null, bs, l, bp, p, null, hr
                ));
                else result = this.serverUseItemOn(new ErosionBlockInteractionPacket(
                    stack, bs, l, bp, p, hand, hr
                ));
                
                if(!result)
                {
                    this.onInteractionFail(new ErosionBlockInteractionPacket(
                        null, bs, l, bp, p, null, null
                    ));
                }
            }
            //super.useItemOn(stack, s, l, bp, p, hand, hr);
            return ItemInteractionResult.SUCCESS;
        }
        /* 
        @Override protected final InteractionResult useWithoutItem(
            BlockState bs, Level leva, BlockPos bp,
            Player playa, BlockHitResult hr
        )
        {
            if(!leva.isClientSide())
            {
                var p = (ServerPlayer) playa;
                var l = (ServerLevel) leva;
                boolean result = this.serverUseWithoutItem(bs,l,bp, p, hr);
                if(!result)
                {
                    this.onInteractionFail(bs, l, bp, p);
                }
            }
            //super.useWithoutItem(bs, l, bp, p, hr);
            return InteractionResult.SUCCESS;
        }
        */
    }
}