package com.ladis.direwolves.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.menu.DirewolfMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, LadisDirewolves.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<DirewolfMenu>> DIREWOLF_MENU =
            MENUS.register("direwolf", () -> IMenuTypeExtension.create(DirewolfMenu::new));
}
