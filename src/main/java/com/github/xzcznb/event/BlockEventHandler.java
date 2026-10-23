package com.github.xzcznb.event;

import com.github.xzcznb.item.ItemLoader;
import com.github.xzcznb.tile.TileEntityGoldenIdol;
import com.github.xzcznb.util.GoldenIdolHelper;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockEventHandler {
    public BlockEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player = event.getHarvester();
        if (player == null) return;
        ItemStack held = player.getHeldItemMainhand();
        if (held.getItem() != ItemLoader.wealthPickaxe) return;
        IBlockState state = event.getState();
        if (state.getMaterial() != Material.ROCK) return;
        Block rock = state.getBlock();
        if (rock.hasTileEntity(state)) return;
        int extra = 6 + ItemHelper.getTotalEnchantLevel(held);
        int fortune = player.getRNG().nextInt(extra) + EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held);
        if (rock instanceof BlockOre) {
            if (rock == Blocks.GOLD_ORE) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Blocks.GOLD_BLOCK, fortune * fortune));
                return;
            }
            Item item = rock.getItemDropped(state, event.getWorld().rand, 0);
            if (item != net.minecraft.init.Items.AIR) {
                int count = rock.quantityDroppedWithBonus(fortune, event.getWorld().rand);
                int meta = rock.damageDropped(state);
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(item, count, meta));
            }
        } else {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Blocks.GOLD_BLOCK, fortune));
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote) return;
        Chunk chunk = event.getChunk();
        for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
            if (!(tileEntity instanceof TileEntityGoldenIdol)) continue;
            BlockPos pos = tileEntity.getPos();
            world.scheduleUpdate(pos, world.getBlockState(pos).getBlock(), 1);
        }
    }
}
