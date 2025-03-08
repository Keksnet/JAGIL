package de.neo8.jagil.cache;

import de.neo8.jagil.cache.h2.TextureCacheH2Impl;
import lombok.Getter;
import lombok.Setter;

public class CacheProvider {

    @Getter
    private final static CacheProvider instance = new CacheProvider();

    @Getter
    @Setter
    private TextureCache textureCache = TextureCacheH2Impl.getInstance();

    private CacheProvider() {}

}
