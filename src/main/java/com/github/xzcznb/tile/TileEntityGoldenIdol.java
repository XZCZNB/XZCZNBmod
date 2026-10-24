package com.github.xzcznb.tile;

import com.github.xzcznb.util.GoldenIdolHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityGoldenIdol extends TileEntity {
    private NBTTagCompound entityNbt;
    private NBTTagCompound playerProfileTag;
    private String entityType;

    @SideOnly(Side.CLIENT)
    private EntityLivingBase cachedFakeEntity;

    public void setFromEntity(EntityLivingBase target) {
        this.entityNbt = new NBTTagCompound();
        target.writeToNBT(this.entityNbt);
        ResourceLocation key = EntityList.getKey(target);
        if (key == null) {
            this.entityNbt = null;
            return;
        }
        this.entityType = key.toString();
        this.cachedFakeEntity = null;
        markDirty();
    }

    public void setFromPlayer(EntityPlayer player) {
        this.entityNbt = new NBTTagCompound();
        player.writeToNBT(this.entityNbt);
        NBTTagList inventoryList = this.entityNbt.getTagList("Inventory", 10);
        NBTTagList filtered = new NBTTagList();
        for (int i = 0; i < inventoryList.tagCount(); i++) {
            NBTTagCompound slotTag = inventoryList.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot");
            boolean keep = (slot >= 100 && slot <= 103) || slot == -106 || slot == player.inventory.currentItem;
            if (keep) filtered.appendTag(slotTag);
        }
        this.entityNbt.setTag("Inventory", filtered);
        this.entityNbt.removeTag("EnderItems");
        NBTTagCompound forgeData = this.entityNbt.getCompoundTag("ForgeData");
        if (forgeData.hasKey("xzcznb_shop_cache")) {
            forgeData.removeTag("xzcznb_shop_cache");
        }
        this.entityType = player.getUniqueID().toString();
        this.playerProfileTag = new NBTTagCompound();
        NBTUtil.writeGameProfile(this.playerProfileTag, player.getGameProfile());
        this.cachedFakeEntity = null;
        markDirty();
    }

    public NBTTagCompound getEntityNbt() { return entityNbt; }
    public String getEntityType() { return entityType; }

    @SideOnly(Side.CLIENT)
    public EntityLivingBase getFakeEntity() {
        if (cachedFakeEntity != null) return cachedFakeEntity;
        if (entityNbt == null || entityType == null || world == null) return null;
        if (GoldenIdolHelper.isValidUUID(entityType)) {
            if (playerProfileTag != null && !playerProfileTag.isEmpty()) {
                GameProfile profile = NBTUtil.readGameProfileFromNBT(playerProfileTag);
                if (profile == null) {
                    profile = new GameProfile(UUID.fromString(entityType), "Unknown");
                }
                cachedFakeEntity = new EntityOtherPlayerMP(world, profile);
                cachedFakeEntity.readFromNBT(entityNbt);
            }
        } else {
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityType), world);
            if (entity instanceof EntityLivingBase) {
                cachedFakeEntity = (EntityLivingBase) entity;
                cachedFakeEntity.readFromNBT(entityNbt);
            }
        }
        return cachedFakeEntity;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        return writeCustomNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readCustomNBT(pkt.getNbtCompound());
        this.cachedFakeEntity = null;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.cachedFakeEntity = null;
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        this.cachedFakeEntity = null;
    }

    public NBTTagCompound writeCustomNBT(NBTTagCompound compound) {
        if (entityNbt != null) compound.setTag("EntityNbt", entityNbt);
        if (entityType != null) compound.setString("EntityType", entityType);
        if (playerProfileTag != null) compound.setTag("PlayerProfile", playerProfileTag);
        return compound;
    }

    public void readCustomNBT(NBTTagCompound compound) {
        if (compound.hasKey("EntityNbt")) entityNbt = compound.getCompoundTag("EntityNbt");
        if (compound.hasKey("EntityType")) entityType = compound.getString("EntityType");
        if (compound.hasKey("PlayerProfile")) playerProfileTag = compound.getCompoundTag("PlayerProfile");
        this.cachedFakeEntity = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        return writeCustomNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
    }
}