package com.ladis.direwolves.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.init.ModMenus;

public class DirewolfMenu extends AbstractContainerMenu {

    private final DirewolfEntity direwolf;

    public DirewolfMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, (DirewolfEntity) playerInventory.player.level().getEntity(extraData.readInt()));
    }

    public DirewolfMenu(int containerId, DirewolfEntity direwolf) {
        super(ModMenus.DIREWOLF_MENU.get(), containerId);
        this.direwolf = direwolf;
    }

    public DirewolfEntity getDirewolf() {
        return this.direwolf;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        DirewolfEntity.Stat[] stats = DirewolfEntity.Stat.values();
        if (id >= 0 && id < stats.length) {
            DirewolfEntity direwolf = this.direwolf;
            if (direwolf != null && direwolf.isOwnedBy(player)) {
                return direwolf.tryUpgrade(stats[id]);
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.direwolf != null
                && !this.direwolf.isRemoved()
                && player.distanceToSqr(this.direwolf) < 64.0D;
    }
}
