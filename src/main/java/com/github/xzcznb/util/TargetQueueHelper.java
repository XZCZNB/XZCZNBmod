package com.github.xzcznb.util;

import com.github.xzcznb.entity.ITargetQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.UUID;

public class TargetQueueHelper {
    private static final int MAX_TARGETS = 64;

    public static void addTargetFront(ITargetQueue holder, EntityLivingBase target) {
        if (target == null) return;
        LinkedList<UUID> queue = holder.getTargetQueue();
        UUID id = target.getUniqueID();
        queue.remove(id);
        queue.addFirst(id);
        if (queue.size() > MAX_TARGETS) queue.removeLast();
        if (holder instanceof EntityCreature) ((EntityCreature) holder).setAttackTarget(target);
    }

    public static void addTargetBack(ITargetQueue holder, EntityLivingBase target) {
        if (target == null) return;
        LinkedList<UUID> queue = holder.getTargetQueue();
        UUID id = target.getUniqueID();
        if (queue.contains(id) || queue.size() >= MAX_TARGETS) return;
        queue.addLast(id);
    }

    public static boolean shouldAddTarget(EntityLivingBase attacker, EntityLivingBase target) {
        if (target == null || !target.isEntityAlive()) return false;
        if (target == attacker) return false;
        if (target instanceof EntityPlayer && ((EntityPlayer) target).isCreative()) return false;
        return !TeamHelper.isAlly(attacker, target);
    }

    public static void clearTarget(EntityCreature attacker) {
        if (attacker instanceof ITargetQueue) {
            ITargetQueue holder = (ITargetQueue) attacker;
            EntityLivingBase current = attacker.getAttackTarget();
            if (current != null) {
                holder.getTargetQueue().remove(current.getUniqueID());
                updateTargetList(attacker, true);
            }
        } else attacker.setAttackTarget(null);
    }

    public static void updateTargetList(EntityCreature attacker, boolean headOnly) {
        if (!(attacker instanceof ITargetQueue)) return;
        LinkedList<UUID> queue = ((ITargetQueue) attacker).getTargetQueue();
        if (headOnly) {
            while (!queue.isEmpty()) {
                UUID id = queue.peekFirst();
                EntityLivingBase head = findEntityLivingBaseByUUID(attacker.world, id);
                if (shouldAddTarget(attacker, head)) break;
                queue.removeFirst();
            }
        } else {
            queue.removeIf(id -> {
                EntityLivingBase target = findEntityLivingBaseByUUID(attacker.world, id);
                if (target == null) return false;
                return !shouldAddTarget(attacker, target);
            });
        }
        EntityLivingBase current = null;
        if (!queue.isEmpty()) {
            current = findEntityLivingBaseByUUID(attacker.world, queue.peekFirst());
        }
        if (attacker.getAttackTarget() != current) {
            attacker.setAttackTarget(current);
        }
    }

    public static EntityLivingBase findEntityLivingBaseByUUID(World world, UUID uuid) {
        for (Entity entity : world.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity.getUniqueID().equals(uuid)) {
                return (EntityLivingBase) entity;
            }
        }
        return null;
    }
}