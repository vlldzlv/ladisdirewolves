package com.ladis.direwolves.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.ladis.direwolves.LadisDirewolves;
import com.ladis.direwolves.entity.DirewolfEntity;
import com.ladis.direwolves.menu.DirewolfMenu;

import java.util.EnumMap;
import java.util.Map;

public class DirewolfScreen extends AbstractContainerScreen<DirewolfMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(LadisDirewolves.MOD_ID, "textures/gui/direwolf.png");
    private static final int TEXTURE_U = 20;
    private static final int TEXTURE_V = 1;
    private static final int CONTENT_WIDTH = 146;
    private static final int CONTENT_HEIGHT = 180;
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 217;
    private static final int ROW_SPACING = 28;
    private static final int COLOR_TEXT = 0x46332D;
    private static final int SHADOW_TEXT = 0xC1A59D;
    private static final int COLOR_MUTED = 0xB4A48B;
    private static final int SHADOW_MUTED = 0xE1D3BD;
    private static final int COLOR_TREATS = 0x886C3A;
    private static final int SHADOW_TREATS = 0xBEAA87;
    private static final int COLOR_HP = 0x5C6F43;
    private static final int SHADOW_HP = 0x9CA68F;
    private final Map<DirewolfEntity.Stat, Button> upgradeButtons = new EnumMap<>(DirewolfEntity.Stat.class);

    public DirewolfScreen(DirewolfMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        for (DirewolfEntity.Stat stat : DirewolfEntity.Stat.values()) {
            Button button = Button.builder(Component.literal("+"), b -> this.buyUpgrade(stat))
                    .bounds(this.leftPos + GUI_WIDTH - 44, this.topPos + 38 + stat.ordinal() * ROW_SPACING - 1, 20, 20)
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
        guiGraphics.blit(GUI_TEXTURE, x, y, GUI_WIDTH, GUI_HEIGHT, TEXTURE_U, TEXTURE_V, CONTENT_WIDTH, CONTENT_HEIGHT, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.drawShadowed(guiGraphics, this.title, this.centeredX(this.title), 12, COLOR_TEXT, SHADOW_TEXT);
        DirewolfEntity direwolf = this.menu.getDirewolf();
        if (direwolf == null) {
            return;
        }
        Component treats = Component.translatable("gui.ladisdirewolves.treats", direwolf.getTreats());
        this.drawShadowed(guiGraphics, treats, this.centeredX(treats), 26, COLOR_TREATS, SHADOW_TREATS);
        Component hp = Component.translatable("gui.ladisdirewolves.hp",
                (int) Math.ceil(direwolf.getHealth()),
                (int) Math.ceil(direwolf.getMaxHealth()));
        this.drawShadowed(guiGraphics, hp, this.centeredX(hp), 182, COLOR_HP, SHADOW_HP);
        int i = 0;
        for (DirewolfEntity.Stat stat : DirewolfEntity.Stat.values()) {
            int rowY = 38 + i * ROW_SPACING;
            this.drawShadowed(guiGraphics,
                    Component.translatable("gui.ladisdirewolves." + stat.name().toLowerCase()),
                    18, rowY, COLOR_TEXT, SHADOW_TEXT);
            int level = stat.getLevel(direwolf);
            this.drawShadowed(guiGraphics,
                    Component.literal("Lv " + level + "/" + DirewolfEntity.MAX_STAT_LEVEL),
                    18, rowY + 11, COLOR_MUTED, SHADOW_MUTED);
            i++;
        }
    }

    private int centeredX(Component text) {
        return (GUI_WIDTH - this.font.width(text)) / 2;
    }

    private void drawShadowed(GuiGraphics guiGraphics, Component text, int x, int y, int color, int shadowColor) {
        guiGraphics.drawString(this.font, text, x + 1, y + 1, shadowColor, false);
        guiGraphics.drawString(this.font, text, x, y, color, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}