package com.ladis.direwolves;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.ladis.direwolves.init.ModEntities;
import com.ladis.direwolves.init.ModItems;
import com.ladis.direwolves.init.ModMenus;
import com.ladis.direwolves.init.ModCreativeTab;

@Mod(LadisDirewolves.MOD_ID)
public class LadisDirewolves {
    public static final String MOD_ID = "ladisdirewolves";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LadisDirewolves(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTab.CREATIVE_TABS.register(modEventBus);

        LOGGER.info("Ladis' Direwolves loaded!");
    }
}
