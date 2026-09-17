package com.ladis.direwolves.init;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.advancement.AllStatsMaxedTrigger;

public class ModTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, LadisDirewolves.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, AllStatsMaxedTrigger> ALL_STATS_MAXED =
            TRIGGERS.register("all_stats_maxed", AllStatsMaxedTrigger::new);
}