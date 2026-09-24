package com.github.xzcznb.entity;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.entity.ai.*;
import com.github.xzcznb.item.*;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.ExperienceRepairHelper;
import com.github.xzcznb.util.ItemHelper;
import com.github.xzcznb.util.TeamHelper;
import com.google.common.base.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static com.github.xzcznb.item.ItemEnchantedGoldenCarrot.EGCEffects;
import static com.github.xzcznb.util.CombatHelper.healByMissingHealth;
import static com.github.xzcznb.util.ParticleHelper.spawnParticles;

public class EntityLoyalZombie extends EntityTameable implements IRangedAttackMob {

    protected boolean isDecoy = false;
    protected int killCount = 0;
    private long lastEatTime = 0;
    private long lastMessageTime = 0;
    private long lastInteractTime = 0;
    private static final int INTERACT_COOLDOWN = 5;
    private static final int EAT_COOLDOWN = 200;
    private static final int MESSAGE_COOLDOWN = 40;
    private UUID lastTargetId = null;
    private float initialDamage = 0;
    private float aToCount = 1;
    private float lastComboDamage = 0;
    private final NonNullList<ItemStack> totemStorage = NonNullList.withSize(9, ItemStack.EMPTY);
    private final NonNullList<ItemStack> weaponStorage = NonNullList.withSize(5, ItemStack.EMPTY);
    private static final DataParameter<Integer> SWING_TICKS =
            EntityDataManager.createKey(EntityLoyalZombie.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS =
            EntityDataManager.createKey(EntityLoyalZombie.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> USING_SPEAR =
            EntityDataManager.createKey(EntityLoyalZombie.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SPEAR_AIM_PITCH =
            EntityDataManager.createKey(EntityLoyalZombie.class, DataSerializers.FLOAT);
    private static final Potion[] BUFF = {
            MobEffects.REGENERATION,
            MobEffects.REGENERATION,
            MobEffects.REGENERATION,
            MobEffects.REGENERATION,
            MobEffects.SPEED,
            MobEffects.SPEED,
            MobEffects.SPEED,
            MobEffects.STRENGTH,
            MobEffects.STRENGTH,
            MobEffects.STRENGTH,
            MobEffects.RESISTANCE,
            MobEffects.RESISTANCE,
            MobEffects.HEALTH_BOOST,
            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.JUMP_BOOST,
    };
    private static final Potion[] DEBUFF = {
            MobEffects.WITHER,
            MobEffects.WITHER,
            MobEffects.WITHER,
            MobEffects.WITHER,
            MobEffects.SLOWNESS,
            MobEffects.SLOWNESS,
            MobEffects.SLOWNESS,
            MobEffects.WEAKNESS,
            MobEffects.WEAKNESS,
            MobEffects.WEAKNESS,
            MobEffects.MINING_FATIGUE,
            MobEffects.MINING_FATIGUE,
            MobEffects.POISON,
            MobEffects.POISON,
            MobEffects.BLINDNESS,
            MobEffects.LEVITATION,
    };

    public boolean isDecoy() {
        return this.isDecoy;
    }

    private float getComboDamage(EntityLivingBase target, float damage, float a, float b) {
        if (a < 1.0f) return damage;
        UUID targetId = target.getUniqueID();
        if (lastTargetId != null && lastTargetId.equals(targetId)) {
            aToCount *= a;
            if (damage == initialDamage) {
                lastComboDamage = a * lastComboDamage + b;
            } else {
                if (a == 1.0f) lastComboDamage = damage + lastComboDamage - initialDamage + b;
                else lastComboDamage = damage * aToCount + b * (aToCount - 1.0f) / (a - 1.0f);
                initialDamage = damage;
            }
            lastComboDamage = Math.max(lastComboDamage, damage);
        } else {
            lastTargetId = targetId;
            aToCount = 1.0f;
            lastComboDamage = damage;
            initialDamage = damage;
        }
        return lastComboDamage;
    }

    private float getComboDamage(EntityLivingBase target, float damage, float a) {
        return getComboDamage(target, damage, a, 0);
    }

    public int getWeaponStorageSize() {
        return this.weaponStorage.size();
    }

    public boolean hasWeapon(int slot) {
        return slot >= 0 && slot < this.weaponStorage.size() && !this.weaponStorage.get(slot).isEmpty();
    }

    public void switchWeapon(int slot) {
        if (slot < 0 || slot >= this.weaponStorage.size()) return;
        ItemStack stored = this.weaponStorage.get(slot);
        if (stored.isEmpty()) return;
        ItemStack mainHand = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        int mainSlot = getWeaponSlot(mainHand);
        if (mainSlot != -1) this.weaponStorage.set(mainSlot, mainHand.copy());
        else if (!mainHand.isEmpty()) this.entityDropItem(mainHand.copy(), 0);
        this.weaponStorage.set(slot, ItemStack.EMPTY);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stored.copy());
        this.setDropChance(EntityEquipmentSlot.MAINHAND, 2);
    }

    protected EntityLoyalZombie(World world) {
        super(world);
        this.setSize(0.6f, 1.8f);
        this.setCanPickUpLoot(true);
        this.setCustomNameTag("\u00A70\u00A7k\u00A7l666\u00A7r\u00A7d\u2665\u00A7l\u00A7cx\u00A76z\u00A7ec\u00A7az\u00A73n\u00A7bb\u00A7f\u00A7k\u00A7l666");
        this.setAlwaysRenderNameTag(true);
        this.isImmuneToFire = true;

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackTeleport(this));
        this.tasks.addTask(2, new EntityAIAttackSpear(this));
        this.tasks.addTask(3, new EntityAIAttackMace(this));
        this.tasks.addTask(4, new EntityAIAttackBow(this, 20, 30, 24.0f));
        this.tasks.addTask(5, new EntityAIAttackOnItem(this));
        this.tasks.addTask(6, new EntityAIAttackLeap(this, 0.4f));
        this.tasks.addTask(7, new EntityAIDealMagicDamage(this));
        this.tasks.addTask(8, new EntityAISwitchWeapon(this));
        this.tasks.addTask(9, new EntityAIFollowOwner(this, 1.0, 16.0f, 8.0f));
        this.tasks.addTask(10, new EntityAIMate(this, 1.0));
        this.tasks.addTask(11, new EntityAIWander(this, 1.0));
        this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(13, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(4, new EntityAIAttackByTarget(this, false));
        Predicate<EntityLivingBase> targetPredicate = input -> input instanceof IMob || input instanceof EntityGolem;
        this.targetTasks.addTask(10, new EntityAIAttackNearTarget<>(this, EntityLivingBase.class, 0, true, true, targetPredicate));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(33550336.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(36.0);
    }

//    protected void updateAttributes() {
//        double attack = 8 + this.killCount * 0.1;
//        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attack);
//        double speed = 0.5 + this.killCount * 0.01;
//        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
//    }

    public void onKill() {
        this.killCount++;
    }

    public static EntityLoyalZombie create(World world) {
        return new EntityLoyalZombie(world);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage() ||
                source == DamageSource.FALL ||
                source == DamageSource.DROWN ||
                source == DamageSource.CACTUS ||
                source == DamageSource.IN_WALL ||
                source == DamageSource.ANVIL ||
                source == DamageSource.FALLING_BLOCK ||
                source == DamageSource.STARVE ||
                source == DamageSource.LIGHTNING_BOLT ||
                source == DamageSource.WITHER ||
                source == DamageSource.DRAGON_BREATH ||
                source == DamageSource.CRAMMING ||
                source == DamageSource.FLY_INTO_WALL
        ) {
            return false;
        }
        if (source.isExplosion() || source.isMagicDamage()) {
            amount *= 0.2f;
        }
        ItemStack held = this.getHeldItemMainhand();
        boolean heldObsidianSword = (!held.isEmpty() && held.getItem() instanceof ItemObsidianSword);
        if (heldObsidianSword && this.getRNG().nextFloat() < 0.5f) {
            Entity attacker = source.getTrueSource();
            if (attacker instanceof EntityLivingBase && attacker != this && !TeamHelper.isAlly((EntityLivingBase) attacker, this)) {
                attacker.attackEntityFrom(DamageSource.MAGIC, amount);
            }
            amount *= 0.2f;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (target == null || !target.isEntityAlive()) {
            this.setAttackTarget(null);
            return;
        }
        float damage = distanceFactor * 6.0f + 1.0f;
        float distance = this.getDistance(target);
        float chance = 0.4f - distance / 64.0f;
        ItemStack bow = this.getHeldItemMainhand();
        int punch = 0;
        int flame = 0;
        if (!bow.isEmpty()) {
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
            punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
            flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow);
            if (power > 0) damage *= (1.0f + power * 0.25f);
        }
        float baseDamage = damage;
        if (this.getRNG().nextFloat() < 0.2f) damage *= 5.0f;
        EntityAIAttackBow.EntityCustomArrow base = new EntityAIAttackBow.EntityCustomArrow(this.world, this, target, 3.6f, 0);
        base.setDamage(damage);
        if (punch > 0) base.setKnockbackStrength(punch);
        if (flame > 0) base.setFire(flame * 100);
        this.world.spawnEntity(base);
        if (this.getRNG().nextFloat() < chance) {
            int extraCount = 2 + this.getRNG().nextInt(8);
            for (int i = 0; i < extraCount; i++) {
                EntityAIAttackBow.EntityCustomArrow extra = new EntityAIAttackBow.EntityCustomArrow(this.world, base);
                extra.setDamage(baseDamage / 2.0f);
                if (punch > 0) extra.setKnockbackStrength(punch / 2);
                if (flame > 0) extra.setFire(flame * 50);
                this.world.spawnEntity(extra);
            }
        }
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, getSoundPitch());
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (entity == null || !entity.isEntityAlive() || !(entity instanceof EntityLivingBase)) return false;
        EntityLivingBase target = (EntityLivingBase) entity;
        ItemStack held = this.getHeldItemMainhand();
        boolean heldObsidianSword = (!held.isEmpty() && held.getItem() instanceof ItemObsidianSword);
        boolean heldBow = (!held.isEmpty() && held.getItem() instanceof ItemBow);
        if (heldObsidianSword && CombatHelper.tryExecute(target, this)) {
            healByMissingHealth(this, 0.2f);
            return true;
        }
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        damage += EnchantmentHelper.getModifierForCreature(held, target.getCreatureAttribute());
        if (heldObsidianSword && this.getRNG().nextFloat() < 0.2f) damage *= 5;
        if (heldBow && !isDecoy) damage = getComboDamage(target, damage, 1.3f, 1.0f);
        boolean success = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        if (success) {
            this.swingArm(EnumHand.MAIN_HAND);
            this.setSwingTicks(6);
            target.addVelocity(this.motionX * 0.5, 0.1, this.motionZ * 0.5);
            if (!held.isEmpty()) {
                int knock_back = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, held);
                if (knock_back > 0) {
                    target.addVelocity(
                            -Math.sin(Math.toRadians(this.rotationYaw)) * knock_back * 0.5,
                            0.1,
                            Math.cos(Math.toRadians(this.rotationYaw)) * knock_back * 0.5
                    );
                }
                int fire_Aspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, held);
                if (fire_Aspect > 0) {
                    target.setFire(fire_Aspect * 80);
                }
            }
            if (this.getRNG().nextFloat() < 0.5f) {
                Potion id = DEBUFF[this.getRNG().nextInt(DEBUFF.length)];
                int duration = this.getRNG().nextInt(64) + 16;
                int amplifier = this.getRNG().nextInt(8);
                target.addPotionEffect(new PotionEffect(id, duration, amplifier));
            }
            if (this.getRNG().nextFloat() < 0.2f) {
                Potion id = BUFF[this.getRNG().nextInt(BUFF.length)];
                int duration = this.getRNG().nextInt(256) + 64;
                int amplifier = this.getRNG().nextInt(8);
                this.addPotionEffect(new PotionEffect(id, duration, amplifier));
            }
        }
        return success;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SWING_TICKS, 0);
        this.dataManager.register(SWINGING_ARMS, false);
        this.dataManager.register(USING_SPEAR, false);
        this.dataManager.register(SPEAR_AIM_PITCH, 0.0f);
    }

    public int getSwingTicks() {
        return this.dataManager.get(SWING_TICKS);
    }

    public void setSwingTicks(int ticks) {
        this.dataManager.set(SWING_TICKS, ticks);
    }

    public boolean isSwingingArms() {
        return this.dataManager.get(SWINGING_ARMS);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    }

    public boolean isUsingSpear() {
        return this.dataManager.get(USING_SPEAR);
    }

    public void setUsingSpear(boolean thrusting) {
        this.dataManager.set(USING_SPEAR, thrusting);
    }

    public float getSpearAimPitch() {
        return this.dataManager.get(SPEAR_AIM_PITCH);
    }

    public void setSpearAimPitch(float pitch) {
        this.dataManager.set(SPEAR_AIM_PITCH, pitch);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getSwingTicks() > 0) {
            this.setSwingTicks(this.getSwingTicks() - 1);
        }
        if (this.world.isRemote) return;
        EntityLivingBase target = this.getAttackTarget();
        if (target != null) {
            if (!target.isEntityAlive()) {
                this.setAttackTarget(null);
            }
        }
        if (this.canPickUpLoot() && !this.dead) {
            List<Entity> entities = this.world.getEntitiesWithinAABB(
                    Entity.class,
                    this.getEntityBoundingBox().grow(1.0, 0, 1.0),
                    input -> input instanceof EntityItem || input instanceof EntityXPOrb
            );
            for (Entity entity : entities) {
                if (entity instanceof EntityItem) {
                    EntityItem itemEntity = (EntityItem) entity;
                    if (!itemEntity.isDead && !itemEntity.getItem().isEmpty() && !itemEntity.cannotPickup()) {
                        this.updateEquipmentIfNeeded(itemEntity);
                    }
                }
                else if (entity instanceof EntityXPOrb) {
                    EntityXPOrb xpOrb = (EntityXPOrb) entity;
                    if (!xpOrb.isDead) {
                        ExperienceRepairHelper.repairHeldItemWithXp(this, xpOrb);
                    }
                }
            }
        }
        if (this.getHeldItemMainhand().isEmpty()) {
            this.refillWeapons();
        }
        if (this.getHeldItemOffhand().isEmpty()) {
            this.refillTotem();
        }
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> target) {
        if (target == EntityGhast.class) return true;
        return super.canAttackClass(target);
    }

    @Override
    public void setAttackTarget(EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (player.capabilities.isCreativeMode) {
                super.setAttackTarget(null);
                return;
            }
        }
        super.setAttackTarget(target);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() instanceof ItemEnchantedGoldenCarrot;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        if (otherAnimal == this || !(otherAnimal instanceof EntityLoyalZombie)) return false;
        EntityLoyalZombie other = (EntityLoyalZombie) otherAnimal;
        if (!TeamHelper.isAlly(this, other)) return false;
        return this.isInLove() && other.isInLove();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (this.world.isRemote) return false;
        if (!this.isOwner(player) && !this.isOnSameTeam(player)) return false;
        ItemStack held = player.getHeldItemMainhand();
        long currentTime = this.world.getTotalWorldTime();
        if (currentTime - this.lastInteractTime < INTERACT_COOLDOWN) {
            return false;
        }
        this.lastInteractTime = currentTime;
        if (held.isEmpty()) {
            if (player.isSneaking()) {
                ItemStack xzcznbHeld = this.getHeldItemMainhand();
                if (!xzcznbHeld.isEmpty()) {
                    player.setHeldItem(hand, xzcznbHeld.copy());
                    this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                    return true;
                }
                return false;
            }
            if (!this.isChild()) {
                if (player.isRiding()) player.dismountRidingEntity();
                player.startRiding(this);
                return true;
            }
        }
        if (held.getItem() instanceof ItemAppleGold || held.getItem() instanceof ItemEnchantedGoldenCarrot) {
            this.heal(8.0f);
            if (currentTime - this.lastEatTime < EAT_COOLDOWN) {
                if (currentTime - this.lastMessageTime >= MESSAGE_COOLDOWN) {
                    String name = this.getCustomNameTag();
                    player.sendMessage(new TextComponentString(
                            name + TextFormatting.BLUE + TextFormatting.BOLD + " : " + TextFormatting.GOLD + TextFormatting.ITALIC + "What can I say..."
                    ));
                    this.lastMessageTime = currentTime;
                    spawnParticles(this, EnumParticleTypes.VILLAGER_ANGRY, this.posX, this.posY + this.height / 2, this.posZ, 1.2, 1.5, 1.2, 0, -0.1, 0, 10);
                    this.playSound(SoundLoader.LOYAL_ZOMBIE_FULL, 1.0f, getSoundPitch());
                }
                return true;
            }
            if (held.getItem() == ItemLoader.EnchantedGoldenCarrot) {
                EGCEffects(this);
                if (!this.isInLove()) {
                    this.setInLove(player);
                }
            }
            else {
                int damage = held.getItemDamage();
                if (damage == 0) {
                    this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 1));
                    this.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
                } else if (damage == 1) {
                    this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 2400, 4));
                    this.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 6000, 4));
                    this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
                    this.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
                }
            }
            spawnParticles(this, EnumParticleTypes.HEART, this.posX, this.posY + this.height / 2, this.posZ, 1.5, 1.5, 1.5, 0, -0.1, 0, 12);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            this.playSound(SoundLoader.LOYAL_ZOMBIE_EAT, 1.0f, getSoundPitch());
            this.lastEatTime = currentTime;
            return true;
        }
        return false;
    }

    protected void refillTotem() {
        for (int i = 0; i < this.totemStorage.size(); i++) {
            ItemStack stored = this.totemStorage.get(i);
            if (!stored.isEmpty() && stored.getItem() == Items.TOTEM_OF_UNDYING) {
                this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stored.copy());
                this.totemStorage.set(i, ItemStack.EMPTY);
                return;
            }
        }
        if (this.killCount > 0) {
            this.killCount--;
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
        }
    }

    protected void refillWeapons() {
        for (int i = 0; i < this.weaponStorage.size(); i++) {
            if (!this.weaponStorage.get(i).isEmpty()) {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, this.weaponStorage.get(i).copy());
                this.setDropChance(EntityEquipmentSlot.MAINHAND, 2);
                this.weaponStorage.set(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    public int getWeaponSlot(ItemStack stack) {
        if (ItemHelper.getWeaponDamage(stack) < 0.5) return -1;
        Item item = stack.getItem();
        if (item instanceof ItemBow) return 0;
        if (item instanceof ItemSpear) return 1;
        if (item instanceof ItemMace) return 2;
        if (item instanceof ItemObsidianSword) return 3;
        return 4;
    }

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) return;
        if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
            ItemStack offhand = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            if (offhand.isEmpty()) {
                this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack.copy());
                this.setDropChance(EntityEquipmentSlot.OFFHAND, 2);
                this.onItemPickup(itemEntity, 1);
                itemEntity.setDead();
                return;
            }
            for (int i = 0; i < this.totemStorage.size(); i++) {
                if (this.totemStorage.get(i).isEmpty()) {
                    this.totemStorage.set(i, stack.copy());
                    this.onItemPickup(itemEntity, 1);
                    itemEntity.setDead();
                    return;
                }
            }
        }
        if (stack.getItem() instanceof ItemArmor) {
            EntityEquipmentSlot armorSlot = EntityLiving.getSlotForItemStack(stack);
            ItemStack currentArmor = this.getItemStackFromSlot(armorSlot);
            if (ItemHelper.isBetterItem(currentArmor, stack)) {
                if (!currentArmor.isEmpty()) this.entityDropItem(currentArmor.copy(), 0);
                this.setItemStackToSlot(armorSlot, stack.copy());
                this.setDropChance(armorSlot, 2);
                this.onItemPickup(itemEntity, 1);
                itemEntity.setDead();
            }
            return;
        }
        ItemStack mainHand = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        int weaponSlot = getWeaponSlot(stack);
        int mainSlot = getWeaponSlot(mainHand);
        if (weaponSlot == -1) return;
        if (mainSlot != -1) {
            if (mainSlot == weaponSlot) {
                if (ItemHelper.isBetterItem(mainHand, stack)) {
                    this.entityDropItem(mainHand.copy(), 0);
                    this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
                    this.setDropChance(EntityEquipmentSlot.MAINHAND, 2);
                    this.onItemPickup(itemEntity, 1);
                    itemEntity.setDead();
                }
            }
            else {
                ItemStack stored = this.weaponStorage.get(weaponSlot);
                if (ItemHelper.isBetterItem(stored, stack)) {
                    if (!stored.isEmpty()) this.entityDropItem(stored.copy(), 0);
                    this.weaponStorage.set(weaponSlot, stack.copy());
                    this.onItemPickup(itemEntity, 1);
                    itemEntity.setDead();
                }
            }
            return;
        }
        if (ItemHelper.isBetterItem(mainHand, stack)) {
            if (!mainHand.isEmpty()) this.entityDropItem(mainHand.copy(), 0);
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
            this.setDropChance(EntityEquipmentSlot.MAINHAND, 2);
            this.onItemPickup(itemEntity, 1);
            itemEntity.setDead();
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        super.dropEquipment(wasRecentlyHit, lootingModifier);
        for (int i = 0; i < this.totemStorage.size(); i++) {
            ItemStack stack = this.totemStorage.get(i);
            if (!stack.isEmpty()) {
                this.entityDropItem(stack.copy(), 0);
                this.totemStorage.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < this.weaponStorage.size(); i++) {
            ItemStack stack = this.weaponStorage.get(i);
            if (!stack.isEmpty()) {
                this.entityDropItem(stack.copy(), 0);
                this.weaponStorage.set(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagList totemList = new NBTTagList();
        NBTTagList weaponList = new NBTTagList();
        for (int i = 0; i < this.totemStorage.size(); i++) {
            ItemStack stack = this.totemStorage.get(i);
            if (!stack.isEmpty()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Slot", i);
                stack.writeToNBT(tag);
                totemList.appendTag(tag);
            }
        }
        for (int i = 0; i < this.weaponStorage.size(); i++) {
            ItemStack stack = this.weaponStorage.get(i);
            if (!stack.isEmpty()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Slot", i);
                stack.writeToNBT(tag);
                weaponList.appendTag(tag);
            }
        }
        compound.setTag("TotemStorage", totemList);
        compound.setTag("WeaponStorage", weaponList);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.totemStorage.clear();
        this.weaponStorage.clear();
        NBTTagList totemList = compound.getTagList("TotemStorage", 10);
        NBTTagList weaponList = compound.getTagList("WeaponStorage", 10);
        for (int i = 0; i < totemList.tagCount(); i++) {
            NBTTagCompound tag = totemList.getCompoundTagAt(i);
            int slot = tag.getInteger("Slot");
            if (slot >= 0 && slot < this.totemStorage.size()) {
                this.totemStorage.set(slot, new ItemStack(tag));
            }
        }
        for (int i = 0; i < weaponList.tagCount(); i++) {
            NBTTagCompound tag = weaponList.getCompoundTagAt(i);
            int slot = tag.getInteger("Slot");
            if (slot >= 0 && slot < this.weaponStorage.size()) {
                this.weaponStorage.set(slot, new ItemStack(tag));
            }
        }
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        EntityLoyalZombie child = EntityLoyalZombie.create(this.world);
        UUID ownerId = this.getOwnerId();
        if (ownerId != null) {
            child.setOwnerId(ownerId);
            child.setTamed(true);
        }
        return child;
    }

    @Override
    protected boolean canDespawn() {
        return !this.isTamed();
    }

    @Override
    public int getTotalArmorValue() {
        return 3;
    }

    @Override
    public boolean getAlwaysRenderNameTag() {
        return true;
    }

    @Override
    protected float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.5f : super.getSoundPitch();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundLoader.LOYAL_ZOMBIE_SAY;
    }

    protected SoundEvent getFallSound(int heightIn) {
        return heightIn > 4 ? SoundLoader.LOYAL_ZOMBIE_FALL_BIG : SoundLoader.LOYAL_ZOMBIE_FALL_SMALL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundLoader.LOYAL_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundLoader.LOYAL_ZOMBIE_DEATH;
    }

    @Override
    public double getMountedYOffset() {
        return 1.8;
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }
}