package com.ladis.direwolves.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.client.model.DirewolfEntityModel;

public class DirewolfEntityRenderer extends GeoEntityRenderer<DirewolfEntity> {

    public DirewolfEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DirewolfEntityModel());
    }
}
