package com.ladis.direwolves.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.menu.DirewolfMenu;

import java.util.EnumMap;
import java.util.Map;

public class DirewolfScreen extends AbstractContainerScreen<DirewolfMenu> {

    private static final int ROW_SPACING = 34;
    private final Map<DirewolfEntity.Stat, Button> upgradeButtons = new EnumMap<>(DirewolfEntity.Stat.class);

    public DirewolfScreen(DirewolfMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        for (DirewolfEntity.Stat stat : DirewolfEntity.Stat.values()) {
            Button button = Button.builder(Component.literal("+"), b -> this.buyUpgrade(stat))
                    .bounds(this.leftPos + 142, this.topPos + 34 + stat.ordinal() * ROW_SPACING + 7, 20, 20)
                    .tooltip(this.upgradeTooltip(stat))
                    .build();
            this.upgradeButtons.put(stat, button);
            this.addRenderableWidget(button);
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        DirewolfEntity direwolf = this.menu.getDirewolf();
        boolean canUpgrade = direwolf != null && this.isOwner(direwolf);
        for (DirewolfEntity.Stat stat : DirewolfEntity.Stat.values()) {
            Button button = this.upgradeButtons.get(stat);
            if (button != null) {
                button.active = canUpgrade
                        && direwolf.getTreats() > 0
                        && stat.getLevel(direwolf) < DirewolfEntity.MAX_STAT_LEVEL;
            }
        }
    }

    private boolean isOwner(DirewolfEntity direwolf) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null
                && direwolf.getOwnerUUID() != null
                && direwolf.getOwnerUUID().equals(minecraft.player.getUUID());
    }

    private void buyUpgrade(DirewolfEntity.Stat stat) {
        DirewolfEntity direwolf = this.menu.getDirewolf();
        if (direwolf != null && this.isOwner(direwolf)
                && direwolf.getTreats() > 0
                && stat.getLevel(direwolf) < DirewolfEntity.MAX_STAT_LEVEL) {
            Minecraft.getInstance().player.connection.send(
                    new ServerboundContainerButtonClickPacket(this.menu.containerId, stat.ordinal()));
        }
    }

    private Tooltip upgradeTooltip(DirewolfEntity.Stat stat) {
        return Tooltip.create(Component.translatable("gui.ladisdirewolves.cost")
                .append(Component.literal("\n"))
                .append(Component.translatable("gui.ladisdirewolves.effect." + stat.name().toLowerCase())));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xC0161616);
        guiGraphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + this.imageHeight - 1, 0xCC242424);
        guiGraphics.fill(x + 8, y + 31, x + this.imageWidth - 8, y + 32, 0xFF3A3A3A);
        for (int i = 1; i < DirewolfEntity.Stat.values().length + 1; i++) {
            int lineY = y + 34 + i * ROW_SPACING - 3;
            guiGraphics.fill(x + 8, lineY, x + this.imageWidth - 8, lineY + 1, 0x553A3A3A);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF);
        DirewolfEntity direwolf = this.menu.getDirewolf();
        if (direwolf == null) {
            return;
        }
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.ladisdirewolves.treats", direwolf.getTreats()),
                10, 20, 0xE8C25A);
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.ladisdirewolves.hp",
                        (int) Math.ceil(direwolf.getHealth()),
                        (int) Math.ceil(direwolf.getMaxHealth())),
                10, 206, 0x55FF55);
        int i = 0;
        for (DirewolfEntity.Stat stat : DirewolfEntity.Stat.values()) {
            int rowY = 34 + i * ROW_SPACING;
            guiGraphics.drawString(
                    this.font,
                    Component.translatable("gui.ladisdirewolves." + stat.name().toLowerCase()),
                    10, rowY, 0xFFFFFF);
            int level = stat.getLevel(direwolf);
            guiGraphics.drawString(
                    this.font,
                    Component.literal("Lv " + level + "/" + DirewolfEntity.MAX_STAT_LEVEL),
                    78, rowY + 11, 0xA9A9A9);
            i++;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}