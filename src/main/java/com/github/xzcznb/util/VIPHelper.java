package com.github.xzcznb.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VIPHelper {

    private static final Set<UUID> VIP_UUIDS = new HashSet<>(Arrays.asList(
            UUID.fromString("f2c59e74-d0a6-48b2-b88a-e4512b2b003f")
    ));

    public static boolean isVip(UUID uuid) {
        if (uuid == null) return false;
        return VIP_UUIDS.contains(uuid);
    }
}
