package com.ladis.direwolves.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.init.ModEntities;

import java.util.List;
import net.minecraft.util.RandomSource;

public class DirewolfFoodItem extends Item {

    public DirewolfFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand usedHand) {
        if (target instanceof DirewolfEntity direwolf && !direwolf.isDeadOrDying()) {
            if (direwolf.getHealth() < direwolf.getMaxHealth()) {
                direwolf.setHealth(direwolf.getMaxHealth());
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
                    newDirewolf.setColorVariant(variantKey(wolf.getVariant()));

                    if (wolf.isTame() && wolf.getOwnerUUID() != null) {
                        newDirewolf.tame(player);
                    }

                    if (wolf.isOrderedToSit()) {
                        newDirewolf.setOrderedToSit(true);
                    }

                    RandomSource random = serverLevel.random;
                    for (int i = 0; i < 30; i++) {
                        double offset = (random.nextDouble() - 0.5D) * wolf.getBbWidth();
                        serverLevel.sendParticles(
                                ParticleTypes.SMOKE,
                                wolf.getX() + offset,
                                wolf.getY() + random.nextDouble() * wolf.getBbHeight(),
                                wolf.getZ() + offset,
                                1, 0.0D, 0.02D, 0.0D, 0.005D
                        );
                    }
                    serverLevel.sendParticles(
                            ParticleTypes.LARGE_SMOKE,
                            wolf.getX(), wolf.getY() + wolf.getBbHeight() * 0.5D, wolf.getZ(),
                            8, 0.45D, 0.35D, 0.45D, 0.0D
                    );
                    wolf.level().playSound(null, wolf.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.NEUTRAL, 1.0F, 1.0F);

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

    private static String variantKey(Holder<net.minecraft.world.entity.animal.WolfVariant> variant) {
        ResourceLocation id = variant.unwrapKey()
                .map(ResourceKey::location)
                .orElse(ResourceLocation.withDefaultNamespace("pale"));
        return switch (id.getPath()) {
            case "pale" -> "default";
            case "ashen" -> "ashy";
            case "snowy" -> "snow";
            case "striped" -> "stripped";
            case "woods" -> "forest";
            default -> id.getPath();
        };
    }
}
