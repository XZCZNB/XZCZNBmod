package com.github.xzcznb.block;

import com.github.xzcznb.XZCZNB;
import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.inventory.ShopGuiHandler;
import com.github.xzcznb.item.ItemWealthPickaxe;
import com.github.xzcznb.util.ItemHelper;
import com.github.xzcznb.util.ShopCacheHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWealth extends Block {

    public BlockWealth() {
        super(Material.IRON);
        this.setTranslationKey("wealth");
        this.setHardness(648.0f);
        this.setResistance(1080.0f);
        this.setHarvestLevel("pickaxe", 3);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean heldWealthPickaxe = !ItemHelper.getHeldItem(player.getHeldItemMainhand(), player.getHeldItemOffhand(), ItemWealthPickaxe.class).isEmpty();
        if (heldWealthPickaxe && ShopCacheHelper.load(player) != null) {
            if (!world.isRemote) {
                player.openGui(XZCZNB.instance, ShopGuiHandler.GUI_ID_SHOP, world, 0, 0, 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beaconPos) {
        return true;
    }
}