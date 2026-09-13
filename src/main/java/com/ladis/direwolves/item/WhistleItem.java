package com.ladis.direwolves.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.init.ModSounds;
import com.ladis.direwolves.menu.DirewolfMenu;

import java.util.List;

public class WhistleItem extends Item {

    public WhistleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
        if (target instanceof DirewolfEntity direwolf) {
            return this.openDirewolfMenu(player, direwolf, usedHand);
        }
        return InteractionResult.PASS;
    }

    public InteractionResult openDirewolfMenu(Player player, DirewolfEntity direwolf, InteractionHand usedHand) {
        if (direwolf.isTame() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), ModSounds.WHISTLE_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (id, inventory, p) -> new DirewolfMenu(id, direwolf),
                            direwolfTitle(direwolf)
                    ),
                    buf -> buf.writeInt(direwolf.getId())
            );
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    private static Component direwolfTitle(DirewolfEntity direwolf) {
        if (direwolf.getCustomName() != null) {
            return direwolf.getType().getDescription().copy()
                    .append(Component.literal(" "))
                    .append(direwolf.getCustomName().copy());
        }
        return direwolf.getType().getDescription();
    }
}
