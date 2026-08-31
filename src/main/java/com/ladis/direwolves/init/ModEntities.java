package com.ladis.direwolves.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.entity.DirewolfEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, LadisDirewolves.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DirewolfEntity>> DIREWOLF =
            ENTITY_TYPES.register("direwolf",
                    () -> EntityType.Builder.of(DirewolfEntity::new, MobCategory.CREATURE)
                            .sized(0.9f, 1.1f)
                            .clientTrackingRange(10)
                            .build("direwolf")
            );
}
