package com.ladis.direwolves.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.client.renderer.DirewolfEntityRenderer;
import com.ladis.direwolves.client.screen.DirewolfScreen;
import com.ladis.direwolves.init.ModEntities;
import com.ladis.direwolves.init.ModMenus;

@EventBusSubscriber(modid = LadisDirewolves.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DIREWOLF.get(), DirewolfEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.DIREWOLF_MENU.get(), DirewolfScreen::new);
    }
}
