package org.senix.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.senix.plugin.components.BetterStackingSettings;
import org.senix.plugin.components.StackingPolicy;

public class InventoryChangeBetterStackingHandler {

    public static void onInventoryChange(LivingEntityInventoryChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        Ref<EntityStore> ref = entity.getReference();
        if (ref == null) return;

        BetterStackingSettings settings = ref.getStore().getComponent(ref, BetterStackingSettings.TYPE);
        if (settings == null) return;

        if (!(event.getTransaction() instanceof ItemStackTransaction itemStackTransaction) || !itemStackTransaction.succeeded()) return;

        for (ItemStackSlotTransaction slot : itemStackTransaction.getSlotTransactions()) {
            if (slot != null && slot.succeeded()) {
                handleStacking(event, entity, slot, settings);
            }
        }
    }

    private static void handleStacking(LivingEntityInventoryChangeEvent event, LivingEntity entity, ItemStackSlotTransaction slot, BetterStackingSettings settings) {
        ItemStack stackAfter = slot.getSlotAfter();
        ItemStack stackBefore = slot.getSlotBefore();

        if (!isOffhandCompatible(stackAfter)) return;

        StackingPolicy offhandPolicy = settings.getPolicy("OFFHAND");

        if (!offhandPolicy.isEnabled()) return;

        // adjust move amount based on stacking mode
        int amountToMove;
        if (offhandPolicy.isPartialOnly()) {
            amountToMove = calculateDelta(stackBefore, stackAfter);
        } else {
            amountToMove = stackAfter.getQuantity();
        }

        if (amountToMove <= 0) return;

        Inventory inv = entity.getInventory();
        ItemContainer offhandSection = inv.getUtility();

        // no need to continue if the change happened in the offhand section
        if (event.getItemContainer() == offhandSection) return;

        for (short i = 0; i < offhandSection.getCapacity(); i++) {
            ItemStack offhandStack = offhandSection.getItemStack(i);

            if (offhandStack != null && offhandStack.isStackableWith(stackAfter)) {
                int max = offhandStack.getItem().getMaxStack();
                int currentOffhand = offhandStack.getQuantity();
                int canTransfer = Math.min(max - currentOffhand, amountToMove);

                if (canTransfer > 0) {
                    // update offhand
                    offhandSection.setItemStackForSlot(i, offhandStack.withQuantity(currentOffhand + canTransfer));

                    // calculate remaining for original slot
                    int finalOriginalAmount = stackAfter.getQuantity() - canTransfer;

                    if (finalOriginalAmount <= 0) {
                        event.getItemContainer().removeItemStackFromSlot(slot.getSlot());
                    } else {
                        event.getItemContainer().setItemStackForSlot(slot.getSlot(), stackAfter.withQuantity(finalOriginalAmount));
                    }

                    inv.markChanged();
                    amountToMove -= canTransfer;
                    if (amountToMove <= 0) break;
                }
            }
        }
    }

    private static int calculateDelta(ItemStack before, ItemStack after) {
        if (before == null || !before.isStackableWith(after)) {
            return after.getQuantity();
        }
        return after.getQuantity() - before.getQuantity();
    }

    private static boolean isOffhandCompatible(ItemStack stack) {
        return stack != null && stack.getItem().getUtility().isUsable();
    }

}
