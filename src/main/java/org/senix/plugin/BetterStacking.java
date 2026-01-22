package org.senix.plugin;

import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.senix.plugin.commands.BetterStackingCommand;
import org.senix.plugin.components.BetterStackingSettings;
import org.senix.plugin.events.InventoryChangeBetterStackingHandler;

public class BetterStacking extends JavaPlugin {

    public BetterStacking(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        BetterStackingSettings.TYPE = getEntityStoreRegistry().registerComponent(
                BetterStackingSettings.class,
                "senix:better_stacking_settings",
                BetterStackingSettings.CODEC
        );

        this.getCommandRegistry().registerCommand(new BetterStackingCommand());

        this.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, InventoryChangeBetterStackingHandler::onInventoryChange);
    }
}
