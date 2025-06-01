package com.github.eterdelta.crittersandcompanions.platform;

import com.github.eterdelta.crittersandcompanions.platform.service.INetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgeNetwork implements INetwork {

    private static final List<Consumer<PayloadRegistrar>> CALLBACKS = new ArrayList<>();

    public static void register(IEventBus modBus) {
        modBus.addListener((RegisterPayloadHandlersEvent event) -> {
            var registrar = event.registrar("1");
            CALLBACKS.forEach(it -> it.accept(registrar));
            CALLBACKS.clear();
        });
    }

    @Override
    public <T extends CustomPacketPayload> Sender<T> createSender(CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, T> type, Consumer<T> handler) {
        CALLBACKS.add(registrar -> {
            registrar.playBidirectional(type.type(), type.codec(), (payload, context) -> handler.accept(payload));
        });

        return new Sender<>() {
            @Override
            public void sendToPlayer(ServerPlayer player, T packet) {
                PacketDistributor.sendToPlayer(player, packet);
            }

            @Override
            public void sendToTracking(Entity entity, T packet) {
                if (entity.level().isClientSide()) return;
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet);
            }
        };
    }
}
