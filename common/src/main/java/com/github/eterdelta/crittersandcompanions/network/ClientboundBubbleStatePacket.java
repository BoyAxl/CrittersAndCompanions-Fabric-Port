package com.github.eterdelta.crittersandcompanions.network;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.extension.IBubbleState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record ClientboundBubbleStatePacket(boolean state, int playerId) implements CustomPacketPayload {

    public static TypeAndCodec<FriendlyByteBuf, ClientboundBubbleStatePacket> TYPE = new TypeAndCodec<>(
            new Type<>(CrittersAndCompanions.createId("bubble_state")),
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    ClientboundBubbleStatePacket::state,
                    ByteBufCodecs.INT,
                    ClientboundBubbleStatePacket::playerId,
                    ClientboundBubbleStatePacket::new
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }

    public void handle() {
        Player player = (Player) Minecraft.getInstance().level.getEntity(playerId);

        if (player instanceof IBubbleState bubbleState) {
            bubbleState.setBubbleActive(state);
        }
    }

}
