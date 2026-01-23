package org.senix.configs;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import org.senix.components.StackingPolicy;

import java.util.HashMap;
import java.util.Map;

public class BetterStackingConfig {
    private Map<String, StackingPolicy> defaultPolicies = new HashMap<>();

    public static final BuilderCodec<BetterStackingConfig> CODEC = BuilderCodec.builder(
            BetterStackingConfig.class, BetterStackingConfig::new)
            .append(new KeyedCodec<>("DefaultPolicies", new MapCodec<>(
                        StackingPolicy.CODEC,
                        HashMap::new,
                        false
                )),
                (config, map, info) -> config.defaultPolicies = map,
                (config, info) -> config.defaultPolicies)
            .add()
            .build();

    public BetterStackingConfig() {
        defaultPolicies.put("OFFHAND", new StackingPolicy(true, true));
        defaultPolicies.put("BACKPACK", new StackingPolicy(true, true));
    }

    public Map<String, StackingPolicy> getDefaultPolicies() {
        return defaultPolicies;
    }
}
