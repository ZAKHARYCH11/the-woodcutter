package net.woodcutter.client.screen;

import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;

public class WoodcutterScreen extends StonecutterScreen {

    public WoodcutterScreen(StonecutterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}