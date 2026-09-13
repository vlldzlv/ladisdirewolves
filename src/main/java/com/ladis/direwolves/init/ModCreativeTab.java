package com.ladis.direwolves.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.ladis.direwolves.LadisDirewolves;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LadisDirewolves.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DIREWOLVES_TAB =
            CREATIVE_TABS.register("ladisdirewolves_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.ladisdirewolves"))
                            .icon(() -> new ItemStack(ModItems.DIREWOLF_TOKEN.get()))
                            .displayItems((params, output) -> {
                                output.accept(ModItems.DIREWOLF_FOOD.get());
                                output.accept(ModItems.DIREWOLF_TOKEN.get());
                                output.accept(ModItems.DIREWOLF_TREATMENT.get());
                                output.accept(ModItems.WHISTLE.get());
                                output.accept(ModItems.DIREWOLF_SPAWN_EGG.get());
                            })
                            .build());
}
