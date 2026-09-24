package com.github.xzcznb.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel("xzcznb");
    private static int id = 0;
    public static void init() {
        INSTANCE.registerMessage(
                PacketParticle.Handler.class,
                PacketParticle.class,
                id++,
                Side.CLIENT
        );
        INSTANCE.registerMessage(
                PacketSpearAction.Handler.class,
                PacketSpearAction.class,
                id++,
                Side.SERVER
        );
    }
}