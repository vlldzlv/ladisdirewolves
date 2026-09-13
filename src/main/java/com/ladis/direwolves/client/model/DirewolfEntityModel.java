package com.ladis.direwolves.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.entity.DirewolfEntity;

import java.util.Set;

public class DirewolfEntityModel extends GeoModel<DirewolfEntity> {

    private static final Set<String> VALID_TEXTURES = Set.of(
            "default", "spotted", "snow", "rusty", "chestnut", "black", "ashy", "stripped", "forest");

    @Override
    public ResourceLocation getModelResource(DirewolfEntity object) {
        return ResourceLocation.fromNamespaceAndPath(LadisDirewolves.MOD_ID, "geo/direwolf.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DirewolfEntity object) {
        String variant = VALID_TEXTURES.contains(object.getColorVariant())
                ? object.getColorVariant()
                : "default";
        return ResourceLocation.fromNamespaceAndPath(
                LadisDirewolves.MOD_ID,
                "textures/entity/direwolf_" + variant + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(DirewolfEntity object) {
        return ResourceLocation.fromNamespaceAndPath(LadisDirewolves.MOD_ID, "animations/direwolf.animation.json");
    }

    @Override
    public void setCustomAnimations(DirewolfEntity animatable, long instanceId, AnimationState<DirewolfEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone head = this.getBone("head").orElse(null);
        if (head != null) {
            float netHeadYaw = animatable.yHeadRot - animatable.yBodyRot;
            float headPitch = animatable.getXRot();
            head.updateRotation(
                    (float) Math.toRadians(headPitch) * 0.5F,
                    (float) Math.toRadians(netHeadYaw),
                    0
            );
        }
    }
}

