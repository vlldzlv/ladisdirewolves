package com.ladis.direwolves.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.init.ModEntities;

@EventBusSubscriber(modid = LadisDirewolves.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CommonEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DIREWOLF.get(), DirewolfEntity.createAttributes().build());
    }
}
