package org.senix;

import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.senix.commands.BetterStackingCommand;
import org.senix.components.BetterStackingSettings;
import org.senix.configs.BetterStackingConfig;
import org.senix.events.InventoryChangeBetterStackingHandler;

public class BetterStacking extends JavaPlugin {

    private static BetterStacking instance;
    private final Config<BetterStackingConfig> config;

    public BetterStacking(@NonNullDecl JavaPluginInit init) {
        super(init);
        instance = this;
        this.config = this.withConfig("BetterStacking", BetterStackingConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();

        // Config saved to: [ServerFolder]/mods/org.senix_BetterStacking/BetterStacking.json
        this.config.save();

        BetterStackingSettings.TYPE = getEntityStoreRegistry().registerComponent(
                BetterStackingSettings.class,
                "senix:better_stacking_settings",
                BetterStackingSettings.CODEC
        );

        this.getCommandRegistry().registerCommand(new BetterStackingCommand());

        this.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, InventoryChangeBetterStackingHandler::onInventoryChange);
    }

    public static BetterStacking getInstance() {
        return instance;
    }

    public BetterStackingConfig getConfigValues() {
        return config.get();
    }
}
