package com.github.xzcznb.inventory;

import com.github.xzcznb.util.TradeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
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
    private int pageIndex;
    private final ShopInventory shopInventory;
    private static final int PAGE_SIZE = 45;
    private static final int MENU_SIZE = 54;
    private static final int NAVIGATE_SIZE = 7;

    public ShopContainer(InventoryPlayer playerInventory, EntityPlayer player, int categoryIndex) {
        this.playerInventory = playerInventory;
        this.categories = ShopCategories.getDefault();
        if (categoryIndex < 0 || categoryIndex >= categories.size()) {
            categoryIndex = 0;
        }
        this.categoryIndex = categoryIndex;
        this.pageIndex = this.categoryIndex / NAVIGATE_SIZE;
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
            int slotIndex = PAGE_SIZE + i;
            if (i == 0) {
                this.addSlotToContainer(new NavigateSlot(shopInventory, slotIndex, null, x, y, NavigateSlot.TARGET_PREV_PAGE));
            } else if (i == 8) {
                this.addSlotToContainer(new NavigateSlot(shopInventory, slotIndex, null, x, y, NavigateSlot.TARGET_NEXT_PAGE));
            } else {
                int catIndex = this.pageIndex * NAVIGATE_SIZE + i - 1;
                if (catIndex < categories.size()) {
                    this.addSlotToContainer(new NavigateSlot(shopInventory, slotIndex, categories.get(catIndex), x, y, catIndex + NavigateSlot.TARGET_CATEGORY_OFFSET));
                } else {
                    this.addSlotToContainer(new NavigateSlot(shopInventory, slotIndex, null, x, y, 0));
                }
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
        int totalPages = (categories.size() + NAVIGATE_SIZE - 1) / NAVIGATE_SIZE;
        for (int i = 0; i < 9; i++) {
            int slotIndex = PAGE_SIZE + i;
            if (i == 0) {
                if (pageIndex > 0) {
                    ItemStack prevBtn = new ItemStack(Items.WATER_BUCKET);
                    prevBtn.setStackDisplayName("\u00A7aPrev");
                    shopInventory.setInventorySlotContents(slotIndex, prevBtn);
                } else {
                    ItemStack noneBtn = new ItemStack(Items.BUCKET);
                    noneBtn.setStackDisplayName("\u00A77End");
                    shopInventory.setInventorySlotContents(slotIndex, noneBtn);
                }
            } else if (i == 8) {
                if (pageIndex < totalPages - 1) {
                    ItemStack nextBtn = new ItemStack(Items.LAVA_BUCKET);
                    nextBtn.setStackDisplayName("\u00A7cNext");
                    shopInventory.setInventorySlotContents(slotIndex, nextBtn);
                } else {
                    ItemStack noneBtn = new ItemStack(Items.BUCKET);
                    noneBtn.setStackDisplayName("\u00A77End");
                    shopInventory.setInventorySlotContents(slotIndex, noneBtn);
                }
            } else {
                int catIndex = this.pageIndex * NAVIGATE_SIZE + i - 1;
                if (catIndex < categories.size()) {
                    ShopCategory cat = categories.get(catIndex);
                    boolean selected = (catIndex == categoryIndex);
                    shopInventory.setInventorySlotContents(slotIndex, NavigateSlot.createNavDisplay(cat, selected));
                } else {
                    shopInventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < PAGE_SIZE) {
            if (player.world.isRemote) return ItemStack.EMPTY;
            if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) return ItemStack.EMPTY;
            ShopCategory category = categories.get(categoryIndex);
            if (category.isSoldOut()) {
                player.sendMessage(new TextComponentString("\u00A7cThis Category Is Sold Out"));
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1.0f, 1.0f);
                return ItemStack.EMPTY;
            }
            List<Trade> trades = category.trades;
            if (slotId >= trades.size()) return ItemStack.EMPTY;
            Trade trade = trades.get(slotId);
            if (trade.product.isEmpty()) return ItemStack.EMPTY;
            boolean shift = (clickType == ClickType.QUICK_MOVE);
            boolean bought = false;
            if (shift) {
                int count = category.count;
                while (!category.isSoldOut() && TradeHelper.trade(player, trade)) {
                    category.decrement();
                }
                bought = category.count != count;
            } else {
                if (TradeHelper.trade(player, trade)) {
                    category.decrement();
                    bought = true;
                }
            }
            if (bought) {
                player.sendMessage(new TextComponentString("\u00A7aPurchase Successful"));
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
            } else {
                player.sendMessage(new TextComponentString("\u00A7cNot Enough Materials"));
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            updateShopInventory();
            this.detectAndSendChanges();
            return ItemStack.EMPTY;
        }
        if (slotId >= PAGE_SIZE && slotId < MENU_SIZE) {
            if (player.world.isRemote) return ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(slotId);
            if (slot instanceof NavigateSlot) {
                NavigateSlot nav = (NavigateSlot) slot;
                int target = nav.getTargetIndex();
                if (target == NavigateSlot.TARGET_PREV_PAGE) {
                    if (pageIndex > 0) {
                        pageIndex--;
                        categoryIndex = pageIndex * NAVIGATE_SIZE;
                        updateShopInventory();
                        this.detectAndSendChanges();
                    }
                } else if (target == NavigateSlot.TARGET_NEXT_PAGE) {
                    int totalPages = (categories.size() + NAVIGATE_SIZE - 1) / NAVIGATE_SIZE;
                    if (pageIndex < totalPages - 1) {
                        pageIndex++;
                        categoryIndex = pageIndex * NAVIGATE_SIZE;
                        updateShopInventory();
                        this.detectAndSendChanges();
                    }
                } else if (target >= NavigateSlot.TARGET_CATEGORY_OFFSET) {
                    int relativeIndex = target - NavigateSlot.TARGET_CATEGORY_OFFSET;
                    int actualIndex = (pageIndex * NAVIGATE_SIZE) + relativeIndex;
                    if (actualIndex != categoryIndex && actualIndex < categories.size()) {
                        categoryIndex = actualIndex;
                        updateShopInventory();
                        this.detectAndSendChanges();
                    }
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