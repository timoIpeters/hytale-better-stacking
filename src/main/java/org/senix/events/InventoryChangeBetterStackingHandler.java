package org.senix.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.senix.components.BetterStackingSettings;
import org.senix.components.StackingPolicy;

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
                handleChainedStacking(event, entity, slot, settings);
            }
        }
    }

    private static void handleChainedStacking(LivingEntityInventoryChangeEvent event, LivingEntity entity, ItemStackSlotTransaction slot, BetterStackingSettings settings) {
        ItemStack currentStack = slot.getSlotAfter();
        if (currentStack == null || currentStack.isEmpty()) return;

        Inventory inv = entity.getInventory();

        // try to add to offhand first
        if (currentStack.getItem().getUtility().isUsable()) {
            currentStack = processTransfer(event, slot, currentStack, settings.getPolicy("OFFHAND"), inv.getUtility());
        }

        // backpack gets the remainder (or everything if offhand is disabled)
        if (currentStack != null && !currentStack.isEmpty()) {
            processTransfer(event, slot, currentStack, settings.getPolicy("BACKPACK"), inv.getBackpack());
        }
    }

    private static ItemStack processTransfer(LivingEntityInventoryChangeEvent event, ItemStackSlotTransaction slot, ItemStack stackToMove, StackingPolicy policy, ItemContainer targetContainer) {
        if (!policy.isEnabled() || event.getItemContainer() == targetContainer) {
            return stackToMove;
        }

        int amountToMove;
        if (policy.isPartialOnly()) {
            amountToMove = calculateDelta(slot.getSlotBefore(), stackToMove);
        } else {
            amountToMove = stackToMove.getQuantity();
        }

        if (amountToMove <= 0) return stackToMove;

        for (short i = 0; i < targetContainer.getCapacity(); i++) {
            ItemStack existingStack = targetContainer.getItemStack(i);

            if (existingStack != null && existingStack.isStackableWith(stackToMove)) {
                int max = existingStack.getItem().getMaxStack();
                int current = existingStack.getQuantity();
                int canTransfer = Math.min(max - current, amountToMove);

                if (canTransfer > 0) {
                    // Update target container
                    targetContainer.setItemStackForSlot(i, existingStack.withQuantity(current + canTransfer));

                    // calculate remainder in the original slot to return it
                    int remainingInSource = stackToMove.getQuantity() - canTransfer;

                    if (remainingInSource <= 0) {
                        event.getItemContainer().removeItemStackFromSlot(slot.getSlot());
                        stackToMove = null;
                    } else {
                        stackToMove = stackToMove.withQuantity(remainingInSource);
                        event.getItemContainer().setItemStackForSlot(slot.getSlot(), stackToMove);
                    }

                    event.getEntity().getInventory().markChanged();
                    amountToMove -= canTransfer;

                    if (amountToMove <= 0 || stackToMove == null) break;
                }
            }
        }
        return stackToMove;
    }


    private static int calculateDelta(ItemStack before, ItemStack after) {
        if (before == null || !before.isStackableWith(after)) {
            return after.getQuantity();
        }
        return after.getQuantity() - before.getQuantity();
    }
}
