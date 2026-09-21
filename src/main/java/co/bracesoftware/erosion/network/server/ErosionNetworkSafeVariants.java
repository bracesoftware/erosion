package co.bracesoftware.erosion.network.server;

import java.lang.classfile.TypeAnnotation.TargetInfo;

import javax.annotation.Nullable;

import org.checkerframework.checker.units.qual.A;

import com.mojang.serialization.MapCodec;

import co.bracesoftware.erosion.ErosionExceptions;
import co.bracesoftware.erosion.ErosionExceptions.ErosionBlockExceptions.ErosionNetworkSafeBlockException;
import co.bracesoftware.erosion.ErosionExceptions.ErosionException;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.crucible.CrucibleBlockEntity;
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
import net.minecraft.world.level.block.BaseEntityBlock;
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

        public boolean onBlockEntityTickOnServer(
            ServerLevel l, BlockPos bp, BlockState bs, T e
        ) throws ErosionException
        {
            return false;
        }

        public static void tick(Level l, BlockPos bp, BlockState bs, ErosionNetworkSafeBlockEntity e)
        {
            if(!l.isClientSide())
            {
                boolean result = e.onBlockEntityTickOnServer((ServerLevel) l, bp, bs, e);
                if(!result)
                {
                    throw new ErosionNetworkSafeBlockException("Why false?");
                }
            }
            return;
        }
    }
    public static abstract class ErosionNetworkSafeBaseEntityBlock extends ErosionNetworkSafeBlock implements EntityBlock
    {
        public final BlockEntityType<? extends ErosionNetworkSafeBlockEntity> networkSafeBlockEntityType;
        public ErosionNetworkSafeBaseEntityBlock(Block.Properties p, BlockEntityType<? extends ErosionNetworkSafeBlockEntity> t)
        {
            super(p);
            this.networkSafeBlockEntityType = t;
        }

        @Nullable 
        @Override 
        public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState s, BlockEntityType<T> type)
        {
            return level.isClientSide() ? null : createTickerHelper(
                type, this.networkSafeBlockEntityType, ErosionNetworkSafeBlockEntity::tick
            );
        }

        // =========================== DO NOT TOUCH!
        //Theze are function overrides ported from BaseEntityBlock,put adapted to my network-safe variant
        @Override protected abstract MapCodec<? extends ErosionNetworkSafeBaseEntityBlock> codec();
        @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
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
        public ErosionNetworkSafeBlock(Block.Properties p)
        {
            super(p);
        }
        // ====================API===================== // 
        public boolean serverUseItemOn(ItemStack is, BlockState bs, ServerLevel l, BlockPos bp, ServerPlayer p, InteractionHand hand, BlockHitResult hr)
        {
            return false;
        }
        public boolean serverUseWithoutItem(BlockState bs, ServerLevel l, BlockPos bp, ServerPlayer p, BlockHitResult hr)
        {
            return false;
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
            ItemStack stack, BlockState s, Level l,
            BlockPos bp, Player p, InteractionHand hand,
            BlockHitResult hr
        )
        {
            if(!l.isClientSide())
            {
                boolean result = this.serverUseItemOn(stack, s, (ServerLevel) l,bp, (ServerPlayer) p, hand, hr);
                return booleanToInteractionResult(ItemInteractionResult.class, result);
            }
            return super.useItemOn(stack, s, l, bp, p, hand, hr);
        }

        @Override protected final InteractionResult useWithoutItem(
            BlockState bs, Level l, BlockPos bp,
            Player p, BlockHitResult hr
        )
        {
            if(!l.isClientSide())
            {
                boolean result = this.serverUseWithoutItem(bs,(ServerLevel) l,bp,(ServerPlayer) p, hr);
                return booleanToInteractionResult(InteractionResult.class, result);
            }
            return super.useWithoutItem(bs, l, bp, p, hr);
        }
    }
}