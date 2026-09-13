package com.ladis.direwolves.init;

import net.minecraft.core.registries.Registries;
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
            ITEMS.register("direwolf_food", () -> new DirewolfFoodItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> DIREWOLF_TOKEN =
            ITEMS.register("direwolf_token", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> DIREWOLF_TREATMENT =
            ITEMS.register("direwolf_treatment", () -> new DirewolfTreatmentItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> WHISTLE =
            ITEMS.register("whistle", () -> new WhistleItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> DIREWOLF_SPAWN_EGG =
            ITEMS.register("direwolf_spawn_egg",
                    () -> new DeferredSpawnEggItem(
                            ModEntities.DIREWOLF,
                            0x5C5C5C,
                            0x3A3A3A,
                            new Item.Properties()
                    ));
}
