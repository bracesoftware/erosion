package co.bracesoftware.erosion.network.server;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.network.client.ErosionClientData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ErosionStatusSyncPacket(
    int pending, long performed, int pendingfast, long performed2,
    boolean agal,
    int retrogen
) implements CustomPacketPayload
{
    public static final Type<ErosionStatusSyncPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(Erosion.MODID, "status_sync")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ErosionStatusSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, ErosionStatusSyncPacket::pending,
        ByteBufCodecs.VAR_LONG, ErosionStatusSyncPacket::performed,
        ByteBufCodecs.INT, ErosionStatusSyncPacket::pendingfast,
        ByteBufCodecs.VAR_LONG, ErosionStatusSyncPacket::performed2,
        ByteBufCodecs.BOOL, ErosionStatusSyncPacket::agal,
        ByteBufCodecs.INT, ErosionStatusSyncPacket::retrogen,
        ErosionStatusSyncPacket::new
    );

    @Override
    public Type<ErosionStatusSyncPacket> type()
    {
        return TYPE;
    }

    public static void handleData(ErosionStatusSyncPacket data, IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ErosionClientData.update(
                data.pending(),
                data.performed(),
                data.pendingfast(),
                data.performed2(),
                data.agal(),
                data.retrogen()
            );
        });
    }
}