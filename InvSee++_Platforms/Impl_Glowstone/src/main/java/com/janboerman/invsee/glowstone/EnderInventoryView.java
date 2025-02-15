package com.janboerman.invsee.glowstone;

import com.janboerman.invsee.spigot.api.CreationOptions;
import com.janboerman.invsee.spigot.api.EnderSpectatorInventoryView;
import com.janboerman.invsee.spigot.api.logging.Difference;
import com.janboerman.invsee.spigot.api.logging.DifferenceTracker;
import com.janboerman.invsee.spigot.api.logging.LogOptions;
import com.janboerman.invsee.spigot.api.logging.LogOutput;
import com.janboerman.invsee.spigot.api.target.Target;
import com.janboerman.invsee.spigot.api.template.EnderChestSlot;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.List;

class EnderInventoryView extends BukkitInventoryView<EnderChestSlot> implements EnderSpectatorInventoryView {

    private final HumanEntity spectator;
    private final EnderInventory top;
    private final PlayerInventory bottom;

    private final ItemStack inaccessibleSlotPlaceholder;

    private DifferenceTracker diffTracker;

    InventoryOpenEvent openEvent;

    EnderInventoryView(HumanEntity spectator, EnderInventory top, CreationOptions<EnderChestSlot> creationOptions) {
        super(creationOptions);
        this.spectator = spectator;
        this.bottom = spectator.getInventory();
        this.top = top;

        final Target target = Target.byGameProfile(top.getSpectatedPlayerId(), top.getSpectatedPlayerName());
        final LogOptions logOptions = creationOptions.getLogOptions();
        final Plugin plugin = creationOptions.getPlugin();
        if (!LogOptions.isEmpty(logOptions)) {
            diffTracker = new DifferenceTracker(
                    LogOutput.make(plugin, spectator.getUniqueId(), spectator.getName(), target, logOptions),
                    logOptions.getGranularity());
            diffTracker.onOpen();
        }

        this.inaccessibleSlotPlaceholder = creationOptions.getPlaceholderPalette().inaccessible();
    }

    @Override
    public EnderInventory getTopInventory() {
        return top;
    }

    @Override
    public PlayerInventory getBottomInventory() {
        return bottom;
    }

    @Override
    public HumanEntity getPlayer() {
        return spectator;
    }

    @Override
    public ItemStack getItem(int slot) {
        EnderInventory top;
        if (0 <= slot && slot < (top = getTopInventory()).getSize()) {
            EnderChestSlot ecSlot = getMirror().getSlot(slot);
            return ecSlot == null ? inaccessibleSlotPlaceholder : top.getItem(ecSlot.defaultIndex());
        } else {
            return super.getItem(slot);
        }
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        EnderInventory top;
        if (0 <= slot && slot < (top = getTopInventory()).getSize()) {
            EnderChestSlot ecSlot = getMirror().getSlot(slot);
            if (ecSlot != null) top.setItem(ecSlot.defaultIndex(), item);
        } else {
            super.setItem(slot, item);
        }
    }

    @Nullable
    @Override
    public Difference getTrackedDifference() {
        return diffTracker == null ? null : diffTracker.getDifference();
    }

    void onClick(List<ItemStack> oldItems, List<ItemStack> newItems) {
        if (diffTracker != null)
            diffTracker.onClick(oldItems, newItems);
    }

    void onClose() {
        if (diffTracker != null)
            diffTracker.onClose();
    }
    
    @Override
    public InventoryType getType() {
        return InventoryType.CHEST;
    }
}
