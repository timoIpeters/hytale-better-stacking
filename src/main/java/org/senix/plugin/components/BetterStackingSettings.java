package org.senix.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.builder.BuilderField;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Component;

import java.util.HashMap;
import java.util.Map;

public class BetterStackingSettings implements Component<EntityStore> {
    public static ComponentType<EntityStore, BetterStackingSettings> TYPE;

    private Map<String, StackingPolicy> policies = new HashMap<>();

    public BetterStackingSettings() {
        policies.put("OFFHAND", new StackingPolicy());
    }

    public static final BuilderCodec<BetterStackingSettings> CODEC;

    static {
        var builder = BuilderCodec.builder(BetterStackingSettings.class, BetterStackingSettings::new);
        new BuilderField.FieldBuilder<>(
                builder,
                new KeyedCodec<>("Policies", new MapCodec<>(
                        StackingPolicy.CODEC,
                        HashMap::new,
                        false
                )),
                (data, map, info) -> data.policies = map,
                (data, info) -> data.policies,
                null
        )
                .documentation("Map of slot types to stacking rules.")
                .add();

        CODEC = builder.build();
    }


    public StackingPolicy getPolicy(String slot) {
        return policies.computeIfAbsent(slot.toUpperCase(), k -> new StackingPolicy());
    }

    @Override
    public BetterStackingSettings clone() {
        BetterStackingSettings copy = new BetterStackingSettings();
        this.policies.forEach((k, v) -> {
            StackingPolicy policyCopy = new StackingPolicy();
            policyCopy.setEnabled(v.isEnabled());
            policyCopy.setPartialOnly(v.isPartialOnly());
            copy.policies.put(k, policyCopy);
        });
        return copy;
    }
}
