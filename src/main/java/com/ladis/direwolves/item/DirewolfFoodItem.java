package com.ladis.direwolves.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.init.ModEntities;

public class DirewolfFoodItem extends Item {

    public DirewolfFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand usedHand) {
        if (target instanceof DirewolfEntity direwolf && !direwolf.isDeadOrDying()) {
            if (direwolf.getHealth() < direwolf.getMaxHealth()) {
                direwolf.heal(6.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        } else if (target instanceof Wolf wolf && !wolf.isDeadOrDying()) {
            Level level = wolf.level();
            if (level instanceof ServerLevel serverLevel) {
                DirewolfEntity newDirewolf = ModEntities.DIREWOLF.get().create(serverLevel);
                if (newDirewolf != null) {
                    newDirewolf.moveTo(wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getYRot(), wolf.getXRot());
                    newDirewolf.setCustomName(wolf.getCustomName());
                    newDirewolf.setCustomNameVisible(wolf.isCustomNameVisible());

                    if (wolf.isTame() && wolf.getOwnerUUID() != null) {
                        newDirewolf.tame(player);
                    }

                    if (wolf.isOrderedToSit()) {
                        newDirewolf.setOrderedToSit(true);
                    }

                    wolf.discard();
                    serverLevel.addFreshEntity(newDirewolf);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
