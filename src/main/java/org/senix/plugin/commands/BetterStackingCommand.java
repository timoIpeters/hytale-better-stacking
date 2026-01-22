package org.senix.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.senix.plugin.components.BetterStackingSettings;

import java.awt.*;

public class BetterStackingCommand extends AbstractPlayerCommand {

    private final FlagArg partialArg;

    public BetterStackingCommand() {
        super("betterts", "Better stacking");
        this.setPermissionGroup(GameMode.Adventure);

        this.partialArg = withFlagArg("mode",
                "Toggles between one of these stacking modes:\n\n" +
                          "Partial: Only stacks new pickups with the offhand slot\n" +
                          "Full: Moves the entire stack from the inventory to the offhand");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        BetterStackingSettings settings = store.getComponent(ref, BetterStackingSettings.TYPE);

        if (settings == null) {
            settings = new BetterStackingSettings();
            store.putComponent(ref, BetterStackingSettings.TYPE, settings);
        }

        boolean toggledPartial = partialArg.get(commandContext);

        if (toggledPartial) {
            settings.setEnabled(true);
            settings.setPartialStack(!settings.isPartialStack());
        } else {
            settings.setEnabled(!settings.isEnabled());
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            player.markNeedsSave();

            String statusText = settings.isEnabled() ? "ON" : "OFF";
            String modeText = settings.isPartialStack() ? "Partial (New pickups only)" : "Full (Existing stacks)";

            Message msg = Message.join(
                    Message.raw("[Better Stacking]: ").color(Color.WHITE),
                    Message.raw(statusText).color(settings.isEnabled() ? Color.GREEN : Color.RED),
                    Message.raw(" | Mode: ").color(Color.WHITE),
                    Message.raw(modeText).color(Color.CYAN)
            );
            player.sendMessage(msg);
        }
    }
}
