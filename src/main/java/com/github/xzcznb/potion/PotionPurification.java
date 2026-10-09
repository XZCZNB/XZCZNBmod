package com.github.xzcznb.potion;

import com.github.xzcznb.util.PotionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class PotionPurification extends Potion {
    private static final ResourceLocation ICON = new ResourceLocation("xzcznb", "textures/gui/potion_purification.png");

    public PotionPurification() {
        super(false, 0xB0E0E6);
        this.setRegistryName("xzcznb", "purification");
        this.setPotionName("effect.purification");
        this.registerPotionAttributeModifier(
                SharedMonsterAttributes.MAX_HEALTH,
                "33550336-7168-8192-9216-787a637a6e62",
                10.0,
                0
        );
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(ICON);
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(ICON);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 4 == 0;
    }

    @Override
    public void performEffect(EntityLivingBase attacker, int amplifier) {
        List<PotionEffect> negativeEffects = new ArrayList<>();
        for (PotionEffect effect : attacker.getActivePotionEffects()) {
            if (effect.getPotion().isBadEffect()) {
                negativeEffects.add(effect);
            }
        }
        for (PotionEffect negativeEffect : negativeEffects) {
            PotionHelper.removePotionEffectAndSync(attacker, negativeEffect.getPotion());
        }
        if (attacker.isBurning()) {
            attacker.extinguish();
        }
        float missingHealth = attacker.getMaxHealth() - attacker.getHealth();
        if (missingHealth > 0.5f * attacker.getMaxHealth()) {
            attacker.heal(0.01f * missingHealth * (amplifier + 1.0f));
        }
    }
}