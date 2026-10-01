package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import java.util.List;

public class ItemScythe extends ItemSword {

    public static ToolMaterial Scythe = EnumHelper.addToolMaterial("scythe", 3, 8000, 30.0f, 4.0f, 30);
    private static final int SWEEP_COOLDOWN = 160;
    private static final float SWEEP_DAMAGE = 4.0f;
    private static final int PURIFICATION_DURATION = 320;

    public ItemScythe() {
        super(Scythe);
        this.setTranslationKey("scythe");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.2, 0));
        }
        return multimap;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!isSelected) return;
        if (world.isRemote || world.getTotalWorldTime() % 4 != 0) return;
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase attacker = (EntityLivingBase) entity;
        attacker.addPotionEffect(new PotionEffect(PotionLoader.purification, PURIFICATION_DURATION, 0));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        player.swingArm(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, held);
        long now = world.getTotalWorldTime();
        NBTTagCompound data = player.getEntityData();
        long last = data.getLong("ScytheCooldown");
        if (last != 0 && now - last < SWEEP_COOLDOWN) {
            return new ActionResult<>(EnumActionResult.FAIL, held);
        }
        data.setLong("ScytheCooldown", now);
        double rad = Math.toRadians(player.rotationYaw);
        this.sweepingMove(held, player, rad, 1.0, 1.0);
        this.sweepingAttack(held, player, 1.0f);
        player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + held.getDisplayName() + TextFormatting.WHITE + " : " + TextFormatting.BLUE + "Gods " + TextFormatting.GOLD + "Do Not " + TextFormatting.RED + "Bleed"));
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        int duration = 16 + 16 * attacker.getRNG().nextInt(16);
        int amplifier = attacker.getRNG().nextInt(4);
        target.addPotionEffect(new PotionEffect(PotionLoader.corruption, duration, amplifier));
        return super.hitEntity(stack, target, attacker);
    }

    public void sweepingAttack(ItemStack stack, EntityLivingBase attacker, float damage) {
        World world = attacker.world;
        if (world.isRemote) return;
        AxisAlignedBB bb = AttackBoundingBoxHelper.getAttackBB1(attacker, 6.0, 1.0, 1.0);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, bb, input -> input != attacker && input.isEntityAlive() && input.canBeCollidedWith());
        double rad = Math.toRadians(attacker.rotationYaw);
        ParticleHelper.spawnArcParticles(attacker, EnumParticleTypes.SMOKE_LARGE, 6.0, 8, attacker.getEyeHeight() * 0.5, Math.toRadians(30), 0, 0, 0);
        float attackDamage = SWEEP_DAMAGE + damage;
        float bonus = 1.0f + ItemHelper.getTotalEnchantLevel(stack) * 0.1f;
        for (EntityLivingBase target : list) {
            if (attacker instanceof EntityPlayer && target instanceof EntityVillager || TeamHelper.isAlly(attacker, target)) continue;
            float speedBonus = (float) CombatHelper.getRelativeSpeed(attacker, target, 0.5, 0.1, 0.5) * 0.25f + 1.0f;
            float totalDamage = bonus * attackDamage * speedBonus;
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), totalDamage);
            target.addVelocity(
                    -Math.sin(rad) * 1.5,
                    0.25,
                    Math.cos(rad) * 1.5
            );
        }
        if (attacker instanceof EntityPlayer) stack.damageItem(1, attacker);
    }

    public void sweepingMove(ItemStack stack, EntityLivingBase attacker, double rad, double ratioX, double ratioZ) {
        double forwardX = -Math.sin(rad) * ratioX;
        double forwardZ = Math.cos(rad) * ratioZ;
        double bonus = 1.0 + ItemHelper.getTotalEnchantLevel(stack) * 0.1;
        attacker.addVelocity(forwardX * bonus, 0.2, forwardZ * bonus);
        attacker.velocityChanged = true;
    }
}