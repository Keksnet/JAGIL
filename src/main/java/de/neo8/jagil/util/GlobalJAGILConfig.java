package de.neo8.jagil.util;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GlobalJAGILConfig {
    private String loaderName;
    private boolean debugMode;
    private List<String> supportedFeatures;
}
