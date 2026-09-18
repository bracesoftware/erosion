package co.bracesoftware.erosion.network.server;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.ErosionClient;
import co.bracesoftware.erosion.network.client.ErosionClientData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ErosionScreenMessagePacket(
    String text, int col
) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ErosionScreenMessagePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ErosionScreenMessagePacket::text,
        ByteBufCodecs.INT, ErosionScreenMessagePacket::col,
        ErosionScreenMessagePacket::new
    );

    @Override
    public Type<ErosionScreenMessagePacket> type()
    {
        return ErosionRegistry.DataPackets.SCREEN_MESSAGE_PACKET;
    }

    public static void handleData(ErosionScreenMessagePacket data, IPayloadContext context)
    {
        if(!context.flow().isClientbound()) return;
        context.enqueueWork(() -> {
            ErosionClientData.sendMessage(data.text(), data.col());
        });
    }
}