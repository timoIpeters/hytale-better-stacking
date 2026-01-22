package org.senix.plugin.commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.senix.plugin.components.BetterStackingSettings;
import org.senix.plugin.components.StackingPolicy;

import java.awt.*;

public class BetterStackingCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> slotArg;
    private final FlagArg fullModeFlag;

    public BetterStackingCommand() {
        super("stacking", "Better stacking");
        this.setPermissionGroup(GameMode.Adventure);

        this.slotArg = withRequiredArg("slot", "The slot to configure", ArgTypes.STRING)
                .addValidator(Validators.nonNull())
                .addValidator(Validators.nonEmptyString())
                .suggest((sender, text, cursor, result) -> {
                    result.suggest("offhand");
                    result.suggest("backpack");
                });

        this.fullModeFlag = withFlagArg("full",
                "Toggles between the two stacking mode\n\n" +
                        "Partial: Only stacks new pickups with the target slot\n" +
                        "Full: Moves the entire stack from the inventory to the target slot");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        BetterStackingSettings settings = store.getComponent(ref, BetterStackingSettings.TYPE);
        if (settings == null) {
            settings = new BetterStackingSettings();
        }

        String slot = slotArg.get(commandContext).toUpperCase();
        StackingPolicy policy = settings.getPolicy(slot);

        boolean isFullFlagPresent = fullModeFlag.get(commandContext);

        if (isFullFlagPresent) {
            // if we use the full flag and previously used the partialOnly setting, this keeps enabled
            // makes sure that using --full also uses a toggle feature when used repeatedly
            policy.setEnabled(policy.isPartialOnly() || !policy.isEnabled());
            policy.setPartialOnly(false);
        } else {
            policy.setEnabled(!policy.isEnabled());
            if (policy.isEnabled()) {
                policy.setPartialOnly(true);
            }
        }

        store.putComponent(ref, BetterStackingSettings.TYPE, settings);

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            player.markNeedsSave();

            String statusText = policy.isEnabled() ? "ON" : "OFF";
            Color statusColor = policy.isEnabled() ? Color.GREEN : Color.RED;
            String modeText = policy.isPartialOnly() ? "Partial (New pickups)" : "Full (Moves stacks)";

            Message msg = Message.join(
                    Message.raw("[Better Stacking - " + slot + "]: ").color(Color.WHITE),
                    Message.raw(statusText).color(statusColor),
                    Message.raw(" | Mode: ").color(Color.GRAY),
                    Message.raw(modeText).color(Color.CYAN)
            );
            player.sendMessage(msg);
        }
    }
}
