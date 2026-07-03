package com.github.eterdelta.crittersandcompanions.network;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.entity.GrapplingHookEntity;
import com.github.eterdelta.crittersandcompanions.extension.IGrapplingState;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record ClientboundGrapplingStatePacket(OptionalInt hook, int playerId) implements CustomPacketPayload {

    private static StreamCodec<FriendlyByteBuf, OptionalInt> OPTIONAL_INT = StreamCodec.of(
            (buffer, value) -> {
                buffer.writeBoolean(value.isPresent());
                value.ifPresent(buffer::writeInt);
            },
            buffer -> {
                if (buffer.readBoolean()) return OptionalInt.of(buffer.readInt());
                return OptionalInt.empty();
            }
    );

    public static CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, ClientboundGrapplingStatePacket> TYPE = new CustomPacketPayload.TypeAndCodec<>(
            new CustomPacketPayload.Type<>(CrittersAndCompanions.createId("grappling_state")),
            StreamCodec.composite(
                    OPTIONAL_INT,
                    ClientboundGrapplingStatePacket::hook,
                    ByteBufCodecs.INT,
                    ClientboundGrapplingStatePacket::playerId,
                    ClientboundGrapplingStatePacket::new
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }

    public void handle() {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        if (level.getEntity(playerId) instanceof Player player && player instanceof IGrapplingState grappleState) {
            hook.ifPresentOrElse(id -> {
                var entity = level.getEntity(id);
                grappleState.setHook(entity instanceof GrapplingHookEntity grapplingHook ? grapplingHook : null);
            }, () -> {
                grappleState.setHook(null);
            });
        }
    }

}
