package com.ladis.direwolves;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.ladis.direwolves.init.ModEntities;
import com.ladis.direwolves.init.ModItems;
import com.ladis.direwolves.init.ModMenus;
import com.ladis.direwolves.init.ModCreativeTab;
import com.ladis.direwolves.init.ModSounds;
import com.ladis.direwolves.init.ModTriggers;
import com.ladis.direwolves.item.DirewolfFoodItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.ladis.direwolves.entity.DirewolfEntity;

@Mod(LadisDirewolves.MOD_ID)
public class LadisDirewolves {
    public static final String MOD_ID = "ladisdirewolves";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LadisDirewolves(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTab.CREATIVE_TABS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModTriggers.TRIGGERS.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);

        LOGGER.info("Ladis' Direwolves loaded!");
    }

    private void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (!(target instanceof Wolf wolf) || target instanceof DirewolfEntity) {
            return;
        }
        if (!wolf.isTame()) {
            return;
        }
        Player player = event.getEntity();
        if (player.isSpectator()) {
            return;
        }
        ItemStack stack = player.getItemInHand(event.getHand());
        if (!stack.is(ModItems.DIREWOLF_FOOD.get())) {
            return;
        }
        event.setCanceled(true);
        if (player.level().isClientSide) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        if (stack.getItem() instanceof DirewolfFoodItem foodItem) {
            event.setCancellationResult(foodItem.interactLivingEntity(stack, player, wolf, event.getHand()));
        } else {
            event.setCancellationResult(InteractionResult.PASS);
        }
    }
}
