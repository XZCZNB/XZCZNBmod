package com.github.xzcznb.util;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DIYMobsHelper {

    private static final Map<String, MobEntry> CONFIGS = new HashMap<>();
    private static final Map<EntityLiving, Integer> PENDING = new HashMap<>();
    private static final int DELAY_TICKS = 2;
    private static File configFile;

    public static void load(File file) {
        configFile = file;
        Configuration cfg = new Configuration(file);
        cfg.load();
        CONFIGS.clear();
        String[] mobs = {"zombie", "skeleton", "spider", "creeper", "zombie_pigman", "husk", "witch", "enderman", "vindicator", "evoker", "wither_skeleton", "stray", "zombie_villager", "cave_spider", "illusion_illager"};
        for (String mob : mobs) {
            String attributes = cfg.getString("attributes", mob, "", "");
            if (attributes.isEmpty()) continue;
            MobEntry entry = new MobEntry();
            parseAttributes(entry, attributes);
            entry.slots.put("HEAD", parseSlot(cfg, mob, "helmet", false));
            entry.slots.put("CHEST", parseSlot(cfg, mob, "chest", false));
            entry.slots.put("LEGS", parseSlot(cfg, mob, "legs", false));
            entry.slots.put("FEET", parseSlot(cfg, mob, "boots", false));
            entry.slots.put("MAINHAND", parseSlot(cfg, mob, "mainhand", false));
            entry.slots.put("OFFHAND", parseSlot(cfg, mob, "offhand", true));
            entry.dropChance = cfg.getFloat("dropchance", mob, 0.0f, 0.0f, 1.0f, "");
            CONFIGS.put("minecraft:" + mob, entry);
        }
        if (cfg.hasChanged()) cfg.save();
    }

    public static void reload() {
        if (configFile != null) load(configFile);
    }

    private static void parseAttributes(MobEntry entry, String s) {
        String trimmed = strip(s);
        for (String part : trimmed.split("},\\{")) {
            String[] kv = part.split(":");
            if (kv.length != 2) continue;
            String[] range = kv[1].split(",");
            try {
                double min;
                double max;
                if (range.length == 1) {
                    min = Double.parseDouble(range[0].trim());
                    max = min;
                } else if (range.length == 2) {
                    min = Double.parseDouble(range[0].trim());
                    max = Double.parseDouble(range[1].trim());
                } else {
                    continue;
                }
                switch (kv[0].trim()) {
                    case "health": entry.healthMin = min; entry.healthMax = max; break;
                    case "speed":  entry.speedMin  = min; entry.speedMax  = max; break;
                    case "damage": entry.damageMin = min; entry.damageMax = max; break;
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private static List<GearEntry> parseSlot(Configuration cfg, String mob, String key, boolean allowStack) {
        String value = cfg.getString(key, mob, "", "");
        List<GearEntry> list = new ArrayList<>();
        if (value.isEmpty()) return list;
        String trimmed = strip(value);
        for (String item : trimmed.split("},\\{")) {
            String[] parts = item.split(",");
            if (allowStack) {
                if (parts.length < 3 || parts.length > 5) continue;
            } else {
                if (parts.length != 3) continue;
            }
            try {
                String id = parts[0].trim();
                int prob = Integer.parseInt(parts[1].trim());
                int ench = Integer.parseInt(parts[2].trim());
                int minCount = 1;
                int maxCount = 1;
                if (allowStack) {
                    if (parts.length == 4) {
                        int v = Integer.parseInt(parts[3].trim());
                        minCount = v;
                        maxCount = v;
                    } else if (parts.length == 5) {
                        minCount = Integer.parseInt(parts[3].trim());
                        maxCount = Integer.parseInt(parts[4].trim());
                    }
                }
                String fullId = id.contains(":") ? id : "minecraft:" + id;
                list.add(new GearEntry(fullId, prob, ench, minCount, maxCount));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    private static String strip(String s) {
        String t = s.trim();
        if (t.startsWith("{")) t = t.substring(1);
        if (t.endsWith("}")) t = t.substring(0, t.length() - 1);
        return t;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (!(event.getEntity() instanceof EntityLiving)) return;
        EntityLiving mob = (EntityLiving) event.getEntity();
        if (mob.getEntityData().getBoolean("DIYMobs")) return;
        PENDING.put(mob, DELAY_TICKS);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Iterator<Map.Entry<EntityLiving, Integer>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<EntityLiving, Integer> entry = it.next();
            int ticks = entry.getValue() - 1;
            if (ticks <= 0) {
                EntityLiving mob = entry.getKey();
                if (mob.isEntityAlive() && !mob.getEntityData().getBoolean("DIYMobs")) {
                    mob.getEntityData().setBoolean("DIYMobs", true);
                    applyRandomMobs(mob);
                }
                it.remove();
            } else {
                entry.setValue(ticks);
            }
        }
    }

    private void applyRandomMobs(EntityLiving mob) {
        ResourceLocation id = EntityList.getKey(mob);
        if (id == null) return;
        MobEntry entry = CONFIGS.get(id.toString());
        if (entry == null) return;
        Random rand = mob.getRNG();
        if (entry.healthMax > 0) {
            double health = entry.healthMin + rand.nextDouble() * (entry.healthMax - entry.healthMin);
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
            mob.setHealth((float) health);
        }
        if (entry.speedMax > 0) {
            double speed = entry.speedMin + rand.nextDouble() * (entry.speedMax - entry.speedMin);
            mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
        }
        if (entry.damageMax > 0) {
            double damage = entry.damageMin + rand.nextDouble() * (entry.damageMax - entry.damageMin);
            mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
        }
        for (Map.Entry<String, List<GearEntry>> slotEntry : entry.slots.entrySet()) {
            EntityEquipmentSlot slot;
            try {
                slot = EntityEquipmentSlot.valueOf(slotEntry.getKey());
            } catch (IllegalArgumentException e) {
                continue;
            }
            GearEntry chosen = pickWeighted(slotEntry.getValue(), rand);
            if (chosen == null || chosen.isAir()) continue;
            Item item = Item.REGISTRY.getObject(new ResourceLocation(chosen.itemId));
            if (item == null) continue;
            int count = chosen.minCount;
            if (chosen.maxCount > chosen.minCount) {
                count = chosen.minCount + rand.nextInt(chosen.maxCount - chosen.minCount + 1);
            }
            ItemStack stack = new ItemStack(item, count);
            applyRandomEnchant(stack, chosen.enchantLevel, rand);
            mob.setItemStackToSlot(slot, stack);
            mob.setDropChance(slot, entry.dropChance);
        }
    }

    private GearEntry pickWeighted(List<GearEntry> list, Random rand) {
        int total = 0;
        for (GearEntry e : list) total += e.probability;
        if (total <= 0) return null;
        int roll = rand.nextInt(total);
        int acc = 0;
        for (GearEntry e : list) {
            acc += e.probability;
            if (roll < acc) return e;
        }
        return null;
    }

    private void applyRandomEnchant(ItemStack stack, int enchantLevel, Random rand) {
        if (enchantLevel <= 0) return;
        if (!stack.isItemEnchantable()) return;
        List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(rand, stack, enchantLevel, true);
        for (EnchantmentData data : list) {
            stack.addEnchantment(data.enchantment, data.enchantmentLevel);
        }
    }

    public static class MobEntry {
        public double healthMin, healthMax;
        public double speedMin, speedMax;
        public double damageMin, damageMax;
        public float dropChance = 0.0f;
        public final Map<String, List<GearEntry>> slots = new HashMap<>();
    }

    public static class GearEntry {
        public final String itemId;
        public final int probability;
        public final int enchantLevel;
        public final int minCount;
        public final int maxCount;

        public GearEntry(String itemId, int probability, int enchantLevel, int minCount, int maxCount) {
            this.itemId = itemId;
            this.probability = probability;
            this.enchantLevel = enchantLevel;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        public boolean isAir() {
            return "minecraft:air".equals(itemId);
        }
    }
}