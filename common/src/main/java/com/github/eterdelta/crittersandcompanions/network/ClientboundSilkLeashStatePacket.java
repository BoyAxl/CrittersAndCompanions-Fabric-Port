package com.github.eterdelta.crittersandcompanions.network;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.extension.ISilkLeashState;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record ClientboundSilkLeashStatePacket(List<LeashData> leashDataList) implements CustomPacketPayload {

    public record LeashData(int leashOwner, IntList leashingEntities, IntList leashedByEntities) {
    }

    public static CustomPacketPayload.TypeAndCodec<FriendlyByteBuf, ClientboundSilkLeashStatePacket> TYPE = new CustomPacketPayload.TypeAndCodec<>(
            new CustomPacketPayload.Type<>(CrittersAndCompanions.createId("silk_leash_state")),
            StreamCodec.of(ClientboundSilkLeashStatePacket::write, ClientboundSilkLeashStatePacket::read)
    );

    public ClientboundSilkLeashStatePacket(LeashData... leashData) {
        this(Arrays.asList(leashData));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE.type();
    }

    private static ClientboundSilkLeashStatePacket read(FriendlyByteBuf byteBuf) {
        int size = byteBuf.readInt();
        var leashDataList = new ArrayList<LeashData>(size);

        for (int i = 0; i < size; i++) {
            int leashOwner = byteBuf.readInt();
            IntList leashingEntities = byteBuf.readIntIdList();
            IntList leashedByEntities = byteBuf.readIntIdList();
            leashDataList.add(new LeashData(leashOwner, leashingEntities, leashedByEntities));
        }

        return new ClientboundSilkLeashStatePacket(leashDataList);
    }

    private static void write(FriendlyByteBuf byteBuf, ClientboundSilkLeashStatePacket packet) {
        byteBuf.writeInt(packet.leashDataList.size());
        for (LeashData data : packet.leashDataList) {
            byteBuf.writeInt(data.leashOwner);
            byteBuf.writeIntIdList(data.leashingEntities);
            byteBuf.writeIntIdList(data.leashedByEntities);
        }
    }

    public void handle() {
        ClientLevel level = Minecraft.getInstance().level;

        for (LeashData data : leashDataList) {
            Entity entity = level.getEntity(data.leashOwner());

            if (entity instanceof ISilkLeashState leashState) {
                leashState.getLeashingEntities().clear();
                leashState.getLeashedByEntities().clear();

                data.leashingEntities().forEach(id -> {
                    Entity leashingEntity = level.getEntity(id);
                    if (leashingEntity instanceof LivingEntity) {
                        leashState.getLeashingEntities().add((LivingEntity) leashingEntity);
                    }
                });
                data.leashedByEntities().forEach(id -> {
                    Entity leashedByEntity = level.getEntity(id);
                    if (leashedByEntity instanceof LivingEntity) {
                        leashState.getLeashedByEntities().add(((LivingEntity) leashedByEntity));
                    }
                });
            }
        }
    }
}
