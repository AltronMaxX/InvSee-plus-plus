package com.janboerman.invsee.spigot.api;

import com.janboerman.invsee.spigot.api.template.PlayerInventorySlot;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an open window for a {@link MainSpectatorInventory}.
 */
public abstract class MainSpectatorInventoryView extends SpectatorInventoryView<PlayerInventorySlot> {

    protected MainSpectatorInventoryView(CreationOptions<PlayerInventorySlot> creationOptions) {
        super(creationOptions);
    }

    /** {@inheritDoc} */
    @Override
    public abstract MainSpectatorInventory getTopInventory();

    @Override
    public void setItem(int slot, ItemStack item) {
        if (0 <= slot && slot < getTopInventory().getSize()) {
            Integer index = getMirror().getIndex(PlayerInventorySlot.byDefaultIndex(slot));
            if (index != null) super.setItem(index, item);
        } else {
            super.setItem(slot, item);
        }
    }

    @Override
    public ItemStack getItem(int slot) {
        if (0 <= slot && slot < getTopInventory().getSize()) {
            Integer index = getMirror().getIndex(PlayerInventorySlot.byDefaultIndex(slot));
            return index == null ? null : super.getItem(index);
        } else {
            return super.getItem(slot);
        }
    }

    /** {@inheritDoc} */
    @Override
    public InventoryType getType() {
        return InventoryType.CHEST;
    }

}
