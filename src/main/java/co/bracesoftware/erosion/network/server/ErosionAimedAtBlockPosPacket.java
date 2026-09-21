package co.bracesoftware.erosion.network.server;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.ErosionSimpleBlocks.IErosionBlockWithTip;
import co.bracesoftware.erosion.ErosionClient;
import co.bracesoftware.erosion.network.client.ErosionClientData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ErosionAimedAtBlockPosPacket(
    long pos
) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ErosionAimedAtBlockPosPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, ErosionAimedAtBlockPosPacket::pos,
        ErosionAimedAtBlockPosPacket::new
    );

    @Override
    public Type<ErosionAimedAtBlockPosPacket> type()
    {
        return ErosionRegistry.DataPackets.AIMED_AT_BLOCK_PACKET;
    }

    public static void handleData(ErosionAimedAtBlockPosPacket d, IPayloadContext c)
    {
        c.enqueueWork(() -> {
            if(c.player() instanceof ServerPlayer p)
            {
                var l = p.serverLevel();
                var pozz = BlockPos.of(d.pos);
                var s = l.getBlockState(pozz);
                if(s.getBlock() instanceof IErosionBlockWithTip b)
                {
                    b.onBlockAimedOn(p, s, pozz);
                }
            }    
        });
    }
}