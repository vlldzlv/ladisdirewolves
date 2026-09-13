package com.ladis.direwolves.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.entity.DirewolfEntity;

public class SaddleGeoLayer extends GeoRenderLayer<DirewolfEntity> {

    public SaddleGeoLayer(GeoRenderer<DirewolfEntity> renderer) {
        super(renderer);
    }

    @Override
    protected ResourceLocation getTextureResource(DirewolfEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(LadisDirewolves.MOD_ID, "textures/entity/direwolf_saddle.png");
    }

    @Override
    public void render(PoseStack poseStack, DirewolfEntity animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.hasSaddle()) {
            return;
        }
        RenderType translucent = RenderType.entityTranslucent(this.getTextureResource(animatable));
        VertexConsumer consumer = bufferSource.getBuffer(translucent);
        this.getRenderer().reRender(
                bakedModel, poseStack, bufferSource, animatable, translucent,
                consumer, partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
    }
}