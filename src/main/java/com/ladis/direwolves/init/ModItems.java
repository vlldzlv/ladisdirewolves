package com.ladis.direwolves.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.item.DirewolfFoodItem;
import com.ladis.direwolves.item.DirewolfTreatmentItem;
import com.ladis.direwolves.item.WhistleItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, LadisDirewolves.MOD_ID);

    public static final DeferredHolder<Item, Item> DIREWOLF_FOOD =
            ITEMS.register("direwolf_food", () -> new DirewolfFoodItem(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationModifier(0.0F)
                            .alwaysEdible()
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 200), 1.0F)
                            .build())));

    public static final DeferredHolder<Item, Item> DIREWOLF_TREATMENT =
            ITEMS.register("direwolf_treatment", () -> new DirewolfTreatmentItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> WHISTLE =
            ITEMS.register("whistle", () -> new WhistleItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> DIREWOLF_SPAWN_EGG =
            ITEMS.register("direwolf_spawn_egg",
                    () -> new DeferredSpawnEggItem(
                            ModEntities.DIREWOLF,
                            0xFFFFFF,
                            0xFFFFFF,
                            new Item.Properties()
                    ));
}
