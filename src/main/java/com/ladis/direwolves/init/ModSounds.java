package com.ladis.direwolves.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.ladis.direwolves.LadisDirewolves;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, LadisDirewolves.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> WHISTLE_USE =
            SOUND_EVENTS.register("whistle_use",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(LadisDirewolves.MOD_ID, "whistle_use")));
}