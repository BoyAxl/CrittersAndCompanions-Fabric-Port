package com.github.eterdelta.crittersandcompanions.network;

import com.github.eterdelta.crittersandcompanions.platform.service.INetwork;
import com.github.eterdelta.crittersandcompanions.platform.Services;

public class CACPacketHandler {

    public static final INetwork.Sender<ClientboundBubbleStatePacket> BUBBLE_STATE =
            Services.NETWORK.createSender(ClientboundBubbleStatePacket.TYPE, ClientboundBubbleStatePacket::handle);

    public static final INetwork.Sender<ClientboundGrapplingStatePacket> GRAPPLING_STATE =
            Services.NETWORK.createSender(ClientboundGrapplingStatePacket.TYPE, ClientboundGrapplingStatePacket::handle);

    public static final INetwork.Sender<ClientboundSilkLeashStatePacket> SILK_LEASH_STATE =
            Services.NETWORK.createSender(ClientboundSilkLeashStatePacket.TYPE, ClientboundSilkLeashStatePacket::handle);

    public static void registerPackets() {
        // Load this class
    }
}
