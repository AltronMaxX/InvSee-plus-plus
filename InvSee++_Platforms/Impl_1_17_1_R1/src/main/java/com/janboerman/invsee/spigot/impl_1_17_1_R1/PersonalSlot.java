package com.janboerman.invsee.spigot.impl_1_17_1_R1;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class PersonalSlot extends Slot {

    PersonalSlot(MainNmsInventory inventory, int index, int xPos, int yPos) {
        super(inventory, index, xPos, yPos);
    }

    boolean works() {
        MainNmsInventory inv = (MainNmsInventory) container;
        int personalSize = inv.personalContents.size();
        boolean inRange = 45 <= slot && slot < 45 + personalSize;
        return inRange;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        if (!works()) return false;
        return super.mayPlace(itemStack);
    }

    @Override
    public boolean hasItem() {
        if (!works()) return false;
        return super.hasItem();
    }

    @Override
    public void set(ItemStack itemStack) {
        if (!works()) this.setChanged();
        super.set(itemStack);
    }

    @Override
    public int getMaxStackSize() {
        if (!works()) return 0;
        return super.getMaxStackSize();
    }

    @Override
    public ItemStack remove(int subtractAmount) {
        if (!works()) {
            return ItemStack.EMPTY;
        } else {
            return super.remove(subtractAmount);
        }
    }

    @Override
    public boolean allowModification(Player player) {
        if (!works()) return false;
        return super.allowModification(player);
    }

    @Override
    public boolean mayPickup(Player player) {
        if (!works()) return false;
        return super.mayPickup(player);
    }

    @Override
    public ItemStack getItem() {
        if (!works()) return ItemStack.EMPTY;
        return super.getItem();
    }

}