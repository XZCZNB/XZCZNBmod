package com.github.xzcznb.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Scoreboard;

import java.util.UUID;

public class TeamHelper {
    public static boolean isAlly(EntityLivingBase attacker, EntityLivingBase target) {
        if (attacker == null || target == null) return false;
        if (target == attacker || attacker.isOnSameTeam(target)) return true;
        if (target instanceof EntityTameable) {
            EntityTameable tameable = (EntityTameable) target;
            UUID targetOwnerId = tameable.getOwnerId();
            if (targetOwnerId != null) {
                if (attacker instanceof EntityPlayer && tameable.isOwner(attacker)) return true;
                if (attacker instanceof EntityTameable) {
                    EntityTameable attackerTameable = (EntityTameable) attacker;
                    UUID attackerOwnerId = attackerTameable.getOwnerId();
                    if (attackerOwnerId != null && attackerOwnerId.equals(targetOwnerId)) return true;
                }
            }
        }
        if (target instanceof EntityPlayer && attacker instanceof EntityTameable) {
            EntityTameable attackerTameable = (EntityTameable) attacker;
            return attackerTameable.isOwner(target);
        }
        return false;
    }

    public static void joinTeam(EntityLivingBase entity, String team) {
        if (entity == null || entity.world.isRemote) return;
        String teamName = "xzcznb_team_" + team;
        Scoreboard scoreboard = entity.world.getScoreboard();
        if (scoreboard.getTeam(teamName) == null) {
            scoreboard.createTeam(teamName);
        }
        scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), teamName);
    }
}