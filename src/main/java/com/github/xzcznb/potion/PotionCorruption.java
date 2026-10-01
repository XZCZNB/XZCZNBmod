package com.github.xzcznb.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;

public class PotionCorruption extends Potion {

    public PotionCorruption() {
        super(true, 0x4A5A2A);
        this.setRegistryName("xzcznb", "corruption");
        this.setPotionName("effect.corruption");
        this.setIconIndex(1, 2);
        this.registerPotionAttributeModifier(
                SharedMonsterAttributes.MAX_HEALTH,
                "33550336-8128-4399-1729-787a637a6e62",
                -4.0,
                0
        );
        this.registerPotionAttributeModifier(
                SharedMonsterAttributes.MOVEMENT_SPEED,
                "33550336-1145-1413-7891-787a637a6e62",
                -0.2,
                1
        );
        this.registerPotionAttributeModifier(
                SharedMonsterAttributes.ATTACK_DAMAGE,
                "33550336-2333-2345-dead-787a637a6e62",
                -4.0,
                0
        );
    }
}