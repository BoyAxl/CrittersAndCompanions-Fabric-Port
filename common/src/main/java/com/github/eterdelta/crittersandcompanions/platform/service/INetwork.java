package com.github.eterdelta.crittersandcompanions.platform.service;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public interface INetwork {

    interface Sender<T> {
        void sendToPlayer(ServerPlayer player, T packet);

        void sendToTracking(Entity entity, T packet);
    }

    <T extends CustomPacketPayload> Sender<T> createSender(CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, T> type, Consumer<T> handler);

}
