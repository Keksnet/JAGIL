package de.neo8.jagil.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GlobalJAGILConfig {
    private String loaderName;
    private boolean debugMode;
}
