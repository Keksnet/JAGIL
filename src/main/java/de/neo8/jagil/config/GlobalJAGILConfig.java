package de.neo8.jagil.config;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GlobalJAGILConfig {
    private String loaderName;
    private boolean debugMode;
    private CachingConfig cachingConfig;
    private List<String> supportedFeatures;
}

