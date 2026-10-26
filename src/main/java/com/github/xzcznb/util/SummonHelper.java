package com.github.xzcznb.util;

import com.github.xzcznb.entity.ISummon;
import net.minecraft.entity.EntityLivingBase;

public class SummonHelper {
    public static EntityLivingBase getBeneficiary(EntityLivingBase attacker) {
        if (attacker instanceof ISummon) {
            EntityLivingBase summoner = ((ISummon) attacker).getSummoner();
            if (summoner != null && summoner.isEntityAlive()) {
                return summoner;
            }
        }
        return attacker;
    }
}
