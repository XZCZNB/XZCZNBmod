package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.IDecoy;
import com.github.xzcznb.util.DecoyHelper;
import com.github.xzcznb.util.VIPHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPetEssence extends Item {

    public ItemPetEssence() {
        this.setTranslationKey("pet_essence");
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (player.world.isRemote) return true;
        if (!player.isSneaking()) return false;
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("StoredPet")) return false;
        if (!(entity instanceof EntityTameable) || entity instanceof IDecoy && ((IDecoy) entity).isDecoy()) return false;
        EntityTameable tameable = (EntityTameable) entity;
        if (!tameable.isTamed() || tameable.getOwner() != player) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagCompound petNbt = new NBTTagCompound();
        tameable.writeEntityToNBT(petNbt);
        tag.setTag("StoredPet", petNbt);
        tag.setString("StoredPetType", EntityList.getKey(tameable).toString());
        DecoyHelper.vanish(tameable);
        return true;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);
        pos = pos.offset(side);
        if (!player.canPlayerEdit(pos, side, stack)) return EnumActionResult.FAIL;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("StoredPet") || !tag.hasKey("StoredPetType")) return EnumActionResult.FAIL;
        ResourceLocation id = new ResourceLocation(tag.getString("StoredPetType"));
        Entity entity = EntityList.createEntityByIDFromName(id, world);
        if (!(entity instanceof EntityTameable)) return EnumActionResult.FAIL;
        EntityTameable pet = (EntityTameable) entity;
        pet.readEntityFromNBT(tag.getCompoundTag("StoredPet"));
        pet.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        world.spawnEntity(pet);
        tag.removeTag("StoredPet");
        tag.removeTag("StoredPetType");
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("StoredPet");
    }
}
