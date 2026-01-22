package org.senix.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Component;

public class BetterStackingSettings implements Component<EntityStore> {

    public static ComponentType<EntityStore, BetterStackingSettings> TYPE;

    public static final BuilderCodec<BetterStackingSettings> CODEC = BuilderCodec.builder(
                    BetterStackingSettings.class,
                    BetterStackingSettings::new
            )
            .addField(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                    (data, value) -> data.enabled = value,
                    data -> data.enabled)
            .addField(new KeyedCodec<>("PartialStack", Codec.BOOLEAN),
                    (data, value) -> data.partialStack = value,
                    data -> data.partialStack)
            .build();

    private boolean enabled = true;
    private boolean partialStack = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isPartialStack() { return partialStack; }
    public void setPartialStack(boolean partialStack) { this.partialStack = partialStack; }

    @Override
    public BetterStackingSettings clone() {
        BetterStackingSettings copy = new BetterStackingSettings();
        copy.enabled = this.enabled;
        copy.partialStack = this.partialStack;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BetterStackingSettings that)) return false;
        return enabled == that.enabled && partialStack == that.partialStack;
    }
}
