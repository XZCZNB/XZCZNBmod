package com.github.xzcznb.entity;

import net.minecraft.entity.EntityLivingBase;

public interface ISummon {
    EntityLivingBase getSummoner();
    boolean canSummon();
    EntityLivingBase summon();
}