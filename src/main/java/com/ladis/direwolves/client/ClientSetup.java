package com.ladis.direwolves.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.client.renderer.DirewolfEntityRenderer;
import com.ladis.direwolves.init.ModEntities;

@EventBusSubscriber(modid = LadisDirewolves.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DIREWOLF.get(), DirewolfEntityRenderer::new);
    }
}
