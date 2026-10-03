package com.github.xzcznb.inventory;

import com.github.xzcznb.util.TradeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class ShopContainer extends Container {

    private final List<ShopCategory> categories;
    private final InventoryPlayer playerInventory;
    private int categoryIndex;
    private final ShopInventory shopInventory;
    private static final int PAGE_SIZE = 45;
    private static final int MENU_SIZE = 54;

    public ShopContainer(InventoryPlayer playerInventory, EntityPlayer player, int categoryIndex) {
        this.playerInventory = playerInventory;
        this.categories = ShopCategories.getDefault();
        if (categoryIndex < 0 || categoryIndex >= categories.size()) {
            categoryIndex = 0;
        }
        this.categoryIndex = categoryIndex;
        this.shopInventory = new ShopInventory();
        buildSlots();
        updateShopInventory();
    }

    private void buildSlots() {
        for (int i = 0; i < PAGE_SIZE; i++) {
            int row = i / 9;
            int col = i % 9;
            this.addSlotToContainer(new ShopSlot(shopInventory, i, 8 + col * 18, 18 + row * 18));
        }
        for (int i = 0; i < 9; i++) {
            int x = 8 + i * 18;
            int y = 18 + 5 * 18;
            if (i < categories.size()) {
                this.addSlotToContainer(new NavigateSlot(shopInventory, PAGE_SIZE + i, categories.get(i), x, y));
            } else {
                this.addSlotToContainer(new NavigateSlot(shopInventory, PAGE_SIZE + i, null, x, y));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 139 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 197));
        }
    }

    private void updateShopInventory() {
        boolean showPriceFirst = (categoryIndex == 3 || categoryIndex == 4);
        ShopCategory category = categories.get(categoryIndex);
        List<Trade> trades = category.trades;
        for (int i = 0; i < PAGE_SIZE; i++) {
            if (i < trades.size()) {
                if (category.isSoldOut()) {
                    shopInventory.setInventorySlotContents(i, ShopSlot.createSoldOut());
                } else {
                    shopInventory.setInventorySlotContents(i, ShopSlot.createDisplay(trades.get(i), showPriceFirst));
                }
            } else {
                shopInventory.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < 9; i++) {
            if (i < categories.size()) {
                ShopCategory cat = categories.get(i);
                boolean selected = (i == categoryIndex);
                shopInventory.setInventorySlotContents(PAGE_SIZE + i, NavigateSlot.createNavDisplay(cat, selected));
            } else {
                shopInventory.setInventorySlotContents(PAGE_SIZE + i, ItemStack.EMPTY);
            }
        }
    }

    private void switchCategory(int index) {
        if (index == categoryIndex) return;
        categoryIndex = index;
        updateShopInventory();
        this.detectAndSendChanges();
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < PAGE_SIZE) {
            if (player.world.isRemote) return ItemStack.EMPTY;
            if (clickType != ClickType.PICKUP || dragType != 0) return ItemStack.EMPTY;
            ShopCategory category = categories.get(categoryIndex);
            if (category.isSoldOut()) {
                player.sendMessage(new TextComponentString("\u00A7cThis Category Is Sold Out"));
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return ItemStack.EMPTY;
            }
            List<Trade> trades = category.trades;
            if (slotId >= trades.size()) return ItemStack.EMPTY;
            Trade trade = trades.get(slotId);
            if (trade.product.isEmpty()) return ItemStack.EMPTY;
            if (TradeHelper.trade(player, trade.price, trade.product)) {
                category.decrement();
                player.sendMessage(new TextComponentString("\u00A7aPurchase Successful"));
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                updateShopInventory();
                this.detectAndSendChanges();
            } else {
                player.sendMessage(new TextComponentString("\u00A7cNot Enough Materials"));
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            return ItemStack.EMPTY;
        }
        if (slotId >= PAGE_SIZE && slotId < MENU_SIZE) {
            if (player.world.isRemote) return ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(slotId);
            if (slot instanceof NavigateSlot) {
                NavigateSlot nav = (NavigateSlot) slot;
                if (nav.getCategory() != null) {
                    int index = categories.indexOf(nav.getCategory());
                    switchCategory(index);
                }
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}