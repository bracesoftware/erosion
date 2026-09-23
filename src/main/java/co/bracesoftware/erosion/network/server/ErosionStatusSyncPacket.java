package co.bracesoftware.erosion.network.server;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.network.client.ErosionClientData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ErosionStatusSyncPacket(
    int pending,
    long performed,
    int pendingfast,
    long performed2,
    boolean agal,
    int retrogen,
    int pendingagain
) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ErosionStatusSyncPacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            ByteBufCodecs.INT.encode(buf, packet.pending());
            ByteBufCodecs.VAR_LONG.encode(buf, packet.performed());
            ByteBufCodecs.INT.encode(buf, packet.pendingfast());
            ByteBufCodecs.VAR_LONG.encode(buf, packet.performed2());
            ByteBufCodecs.BOOL.encode(buf, packet.agal());
            ByteBufCodecs.INT.encode(buf, packet.retrogen());
            ByteBufCodecs.INT.encode(buf, packet.pendingagain());
        },
        buf -> new ErosionStatusSyncPacket(
            ByteBufCodecs.INT.decode(buf),
            ByteBufCodecs.VAR_LONG.decode(buf),
            ByteBufCodecs.INT.decode(buf),
            ByteBufCodecs.VAR_LONG.decode(buf),
            ByteBufCodecs.BOOL.decode(buf),
            ByteBufCodecs.INT.decode(buf),
            ByteBufCodecs.INT.decode(buf)
        )
    );

    @Override
    public Type<ErosionStatusSyncPacket> type()
    {
        return ErosionRegistry.DataPackets.MOD_STATUS_SYNC;
    }

    public static void handleData(ErosionStatusSyncPacket data, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ErosionClientData.updateModStatus(
                data.pending(),
                data.performed(),
                data.pendingfast(),
                data.performed2(),
                data.agal(),
                data.retrogen(),
                data.pendingagain()
            );
        });
    }
}