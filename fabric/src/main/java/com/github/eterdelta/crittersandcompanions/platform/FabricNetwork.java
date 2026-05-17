package com.github.eterdelta.crittersandcompanions.platform;

import com.github.eterdelta.crittersandcompanions.platform.service.INetwork;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class FabricNetwork implements INetwork {

    @Override
    public <T extends CustomPacketPayload> Sender<T> createSender(CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, T> type, Consumer<T> handler) {
        PayloadTypeRegistry.serverboundPlay().register(type.type(), type.codec());
        PayloadTypeRegistry.clientboundPlay().register(type.type(), type.codec());

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(type.type(), (packet, context) -> handler.accept(packet));
        } else {
            ServerPlayNetworking.registerGlobalReceiver(type.type(), (packet, context) -> handler.accept(packet));
        }

        return new Sender<>() {
            @Override
            public void sendToPlayer(ServerPlayer player, T packet) {
                ServerPlayNetworking.send(player, packet);
            }

            @Override
            public void sendToTracking(Entity entity, T packet) {
                for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                    ServerPlayNetworking.send(player, packet);
                }
                if (entity instanceof ServerPlayer player) {
                    ServerPlayNetworking.send(player, packet);
                }
            }
        };
    }

}
