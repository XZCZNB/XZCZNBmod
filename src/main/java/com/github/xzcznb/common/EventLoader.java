package com.github.xzcznb.common;

import com.github.xzcznb.entity.EntityLoyalZombie;
import com.github.xzcznb.entity.EntityLoyalZombieDecoy;
import com.github.xzcznb.item.ItemSpear;
import com.github.xzcznb.network.PacketHandler;
import com.github.xzcznb.network.PacketSpearAction;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventLoader
{
    public EventLoader()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    private boolean wasLeftClickHeld = false;
    private int attackCooldown = 0;
    private int moveCooldown = 0;

    @SubscribeEvent
    public void onMobDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) event.getEntity();
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                villager.entityDropItem(new ItemStack(Items.SKULL, 1, 3), 0);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity attacker = event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        if (attacker instanceof EntityLivingBase && target != null) {
            if (TeamHelper.isAlly((EntityLivingBase) attacker, target)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        if (target instanceof EntityLoyalZombieDecoy) return;
        if (killer instanceof EntityLoyalZombie) {
            ((EntityLoyalZombie) killer).onKill();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        if (attackCooldown > 0) attackCooldown--;
        if (moveCooldown > 0) moveCooldown--;
        boolean isLeftClick = mc.gameSettings.keyBindAttack.isKeyDown();
        boolean isRightClick = mc.gameSettings.keyBindUseItem.isKeyDown();
        ItemStack held = mc.player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemSpear) {
            if (isRightClick && attackCooldown <= 0) {
                PacketHandler.INSTANCE.sendToServer(new PacketSpearAction(true));
                attackCooldown = 8;
            }
            if (isLeftClick && !wasLeftClickHeld && moveCooldown <= 0) {
                PacketHandler.INSTANCE.sendToServer(new PacketSpearAction(false));
                moveCooldown = 4;
            }
        }
        wasLeftClickHeld = isLeftClick;
    }
}