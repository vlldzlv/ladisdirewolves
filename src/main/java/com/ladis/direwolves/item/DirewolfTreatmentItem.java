package com.ladis.direwolves.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.ladis.direwolves.entity.DirewolfEntity;

import java.util.List;

public class DirewolfTreatmentItem extends Item {

    public DirewolfTreatmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
        if (target instanceof DirewolfEntity direwolf) {
            return this.feedDirewolf(player, direwolf, stack);
        }
        if (target instanceof Wolf wolf
                && !wolf.isDeadOrDying()
                && wolf.getHealth() < wolf.getMaxHealth()) {
            wolf.heal(6.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult feedDirewolf(Player player, DirewolfEntity direwolf, ItemStack stack) {
        if (!direwolf.isDeadOrDying() && direwolf.isTame()) {
            if (direwolf.getHealth() < direwolf.getMaxHealth()) {
                direwolf.heal(6.0F);
            }
            direwolf.gainTreat(stack);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}