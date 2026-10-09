package com.github.xzcznb.util;

import java.util.UUID;

public class VIPHelper {

    private static final UUID VIP_UUID = UUID.fromString("f2c59e74-d0a6-48b2-b88a-e4512b2b003f");

    public static boolean isVip(UUID uuid) {
        return uuid != null && uuid.equals(VIP_UUID);
    }
}