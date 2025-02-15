package com.janboerman.invsee.spigot.impl_1_17_1_R1;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class InaccessibleSlot extends Slot {

    InaccessibleSlot(Container inventory, int index, int xPos, int yPos) {
        super(inventory, index, xPos, yPos);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean hasItem() {
        return false;
    }

    @Override
    public void set(ItemStack itemStack) {
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public ItemStack remove(int subtractAmount) {
        return InvseeImpl.EMPTY_STACK;
    }

    @Override
    public boolean allowModification(Player player) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return InvseeImpl.EMPTY_STACK;
    }

}

class InaccessiblePlaceholderSlot extends InaccessibleSlot {

    private final ItemStack placeholder;

    InaccessiblePlaceholderSlot(ItemStack placeholder, Container inventory, int index, int xPos, int yPos) {
        super(inventory, index, xPos, yPos);

        this.placeholder = placeholder;
    }

    @Override
    public ItemStack getItem() {
        return placeholder;
    }

}