package de.neo8.jagil.config;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class CachingConfig {
    private boolean enabled;
    private Map<String, String> providerConfig;
}
