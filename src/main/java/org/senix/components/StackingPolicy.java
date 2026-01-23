package org.senix.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class StackingPolicy {
    private boolean enabled = true;
    private boolean partialOnly = true;

    public static final BuilderCodec<StackingPolicy> CODEC = BuilderCodec.builder(
            StackingPolicy.class,
            StackingPolicy::new
            )
            .addField(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                    (policy, value) -> policy.enabled = value,
                    (policy) -> policy.enabled)
            .documentation("Whether auto-stacking is active for this slot.")
            .addField(new KeyedCodec<>("PartialOnly", Codec.BOOLEAN),
                    (policy, value) -> policy.partialOnly = value,
                    (policy) -> policy.partialOnly)
            .documentation("If true, only tops off existing stacks. If false, moves entire stacks.")
            .build();

    public StackingPolicy() {}
    public StackingPolicy(boolean enabled, boolean partialOnly) {
        this.enabled = enabled;
        this.partialOnly = partialOnly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPartialOnly() {
        return partialOnly;
    }

    public void setPartialOnly(boolean partialOnly) {
        this.partialOnly = partialOnly;
    }
}
