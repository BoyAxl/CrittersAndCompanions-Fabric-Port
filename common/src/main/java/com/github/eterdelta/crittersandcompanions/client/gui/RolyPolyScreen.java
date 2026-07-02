package com.github.eterdelta.crittersandcompanions.client.gui;

import static com.github.eterdelta.crittersandcompanions.menu.RolyPolyMenu.PLAYER_INV_START_Y;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.menu.RolyPolyMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RolyPolyScreen extends AbstractContainerScreen<RolyPolyMenu> {
    private static final Identifier TEXTURE = CrittersAndCompanions.createId("textures/gui/roly_poly_chest.png");
    private static final WidgetSprites REMOVE_CHEST_SPRITES = new WidgetSprites(
            CrittersAndCompanions.createId("roly_poly/remove_chest"),
            CrittersAndCompanions.createId("roly_poly/remove_chest_highlighted")
    );

    public RolyPolyScreen(RolyPolyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = PLAYER_INV_START_Y - 11;
        this.addRenderableWidget(new ImageButton(
                this.leftPos + 152,
                this.topPos + 6,
                18,
                18,
                REMOVE_CHEST_SPRITES,
                button -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    }
                    this.onClose();
                }
        ));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
