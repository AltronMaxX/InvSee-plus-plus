package com.janboerman.invsee.folia.impl_1_20_1_R1;

import com.janboerman.invsee.spigot.api.MainSpectatorInventory;
import com.janboerman.invsee.spigot.api.MainSpectatorInventoryView;
import com.janboerman.invsee.spigot.api.logging.Difference;
import com.janboerman.invsee.spigot.api.logging.DifferenceTracker;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;

class MainBukkitInventoryView extends MainSpectatorInventoryView {

    private final MainNmsContainer nms;

    MainBukkitInventoryView(MainNmsContainer nms) {
        super(nms.creationOptions);
        this.nms = nms;
    }

    @Override
    public MainSpectatorInventory getTopInventory() {
        return nms.top.bukkit();
    }

    @Override
    public PlayerInventory getBottomInventory() {
        return nms.player.getBukkitEntity().getInventory();
    }

    @Override
    public HumanEntity getPlayer() {
        return nms.player.getBukkitEntity();
    }

    @Override
    public String getTitle() {
        return nms.title();
    }

    @Override
    public String getOriginalTitle() {
        return nms.originalTitle;
    }

    @Override
    public void setTitle(String title) {
        CraftInventoryView.sendInventoryTitleChange(this, title);
        nms.title = title;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        if (slot >= 0) {
            nms.getSlot(slot).set(stack);
        } else {
            nms.player.drop(stack, false);
        }
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < 0 ? null : CraftItemStack.asCraftMirror(nms.getSlot(slot).getItem());
    }

    @Override
    public @Nullable Difference getTrackedDifference() {
        DifferenceTracker tracker = nms.tracker;
        return tracker == null ? null : tracker.getDifference();
    }
}
