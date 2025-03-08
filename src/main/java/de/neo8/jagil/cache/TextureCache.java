package de.neo8.jagil.cache;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface TextureCache {
    boolean isTextureCached(UUID uuid);

    @Nullable String getCachedTexture(UUID uuid);

    @Nullable ProfileProperty getCachedTextureAsProperty(@NotNull UUID uuid);

    void updateCacheAsync(@NotNull UUID uuid, @NotNull String texture, @Nullable String signature);

    void updateCache(@NotNull UUID uuid, @NotNull String texture, @Nullable String signature);

    default void updateCacheAsync(@NotNull UUID uuid, @NotNull String texture) {
        this.updateCacheAsync(uuid, texture, null);
    }

    default void updateCache(@NotNull UUID uuid, @NotNull String texture) {
        this.updateCache(uuid, texture, null);
    }
}
