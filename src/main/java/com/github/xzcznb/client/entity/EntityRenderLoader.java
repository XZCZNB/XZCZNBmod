package com.github.xzcznb.client.entity;

import com.github.xzcznb.client.model.ModelLoyalZombie;
import com.github.xzcznb.client.render.RenderGrenade;
import com.github.xzcznb.client.render.RenderShell;
import com.github.xzcznb.entity.*;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRenderLoader {

    private static final ResourceLocation MY_SKIN = new ResourceLocation("xzcznb", "textures/entity/loyal_zombie.png");
    private static final float SCALE = 0.9375f;

    public EntityRenderLoader() {
        RenderingRegistry.registerEntityRenderingHandler(EntityShell.class, RenderShell::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, RenderGrenade::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityLoyalZombie.class, manager -> {
            RenderBiped<EntityLoyalZombie> renderer = new RenderBiped<EntityLoyalZombie>(manager, new ModelLoyalZombie(), 0.5f) {
                @Override
                protected ResourceLocation getEntityTexture(EntityLoyalZombie entity) {
                    return MY_SKIN;
                }
                @Override
                protected void preRenderCallback(EntityLoyalZombie entity, float partialTickTime) {
                    GlStateManager.scale(SCALE, SCALE, SCALE);
                    GlStateManager.translate(0, 0, 0);
                }
            };
            renderer.addLayer(new LayerLoyalZombieArmor(renderer));
            return renderer;
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityLoyalZombieDecoy.class, manager -> {
            RenderBiped<EntityLoyalZombieDecoy> renderer = new RenderBiped<EntityLoyalZombieDecoy>(manager, new ModelLoyalZombie(), 0.5f) {
                @Override
                protected ResourceLocation getEntityTexture(EntityLoyalZombieDecoy entity) {
                    return MY_SKIN;
                }
                @Override
                protected void preRenderCallback(EntityLoyalZombieDecoy entity, float partialTickTime) {
                    GlStateManager.scale(SCALE, SCALE, SCALE);
                    GlStateManager.translate(0, 0, 0);
                }
            };
            renderer.addLayer(new LayerLoyalZombieArmor(renderer));
            return renderer;
        });
    }

    public static class LayerLoyalZombieArmor extends LayerBipedArmor {
        public LayerLoyalZombieArmor(RenderBiped<?> rendererIn) {
            super(rendererIn);
        }
        @Override
        protected void initArmor() {
            this.modelLeggings = new ModelBiped(0.5f);
            this.modelArmor = new ModelBiped(1.0f);
        }
    }
}