package com.github.xzcznb.util;

import com.github.xzcznb.entity.IBlockValue;

public class BlockValueHelper {
    public static float absorb(IBlockValue entity, float damage, float ratio) {
        float blockValue = entity.getBlockValue();
        if (blockValue < 64.0f) return damage;
        if (blockValue > damage) {
            entity.setBlockValue(blockValue - damage);
            return 0;
        }
        damage -= blockValue;
        entity.setBlockValue(0);
        if (ratio < 0.3f) damage *= 0.2f;
        else damage *= 0.5f;
        return damage;
    }
}
