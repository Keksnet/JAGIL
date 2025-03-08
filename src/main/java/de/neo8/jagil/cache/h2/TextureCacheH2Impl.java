package de.neo8.jagil.cache.h2;

import com.destroystokyo.paper.profile.ProfileProperty;
import de.neo8.jagil.JAGIL;
import de.neo8.jagil.cache.CacheComponent;
import de.neo8.jagil.cache.TextureCache;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TextureCacheH2Impl extends H2CacheBase implements TextureCache {

    @Getter
    private final static TextureCacheH2Impl instance = new TextureCacheH2Impl();

    @NotNull
    private final CacheComponent cacheComponentInfo;

    private TextureCacheH2Impl() {
        this.cacheComponentInfo = new CacheComponent("TextureCache", 0x00);
        if (!JAGIL.getGlobalJAGILConfig().getCachingConfig().isEnabled()) {
            return;
        }

        new Thread(this::initTables).start();
    }

    @Override
    public boolean isTextureCached(UUID uuid) {
        try (Connection con = this.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM TEXTURES WHERE ID = ?;");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean isCached = rs.next() && rs.getInt(1) > 0;
            rs.close();
            ps.close();
            return isCached;
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public @Nullable String getCachedTexture(UUID uuid) {
        try (Connection con = this.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM TEXTURES WHERE ID = ? AND (EXPIRES IS NULL OR EXPIRES >= NOW());");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String texture = null;
            if (rs.next()) {
                texture = rs.getString(1);
            }
            ps.close();
            rs.close();

            return texture;
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public @Nullable ProfileProperty getCachedTextureAsProperty(@NotNull UUID uuid) {
        try (Connection con = this.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT TEXTURE, SIGNATURE FROM TEXTURES WHERE ID = ? AND (EXPIRES IS NULL OR EXPIRES >= NOW());");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String texture = null, signature = null;
            if (rs.next()) {
                texture = rs.getString("TEXTURE");
                signature = rs.getString("SIGNATURE");
            }
            ps.close();
            rs.close();

            if (texture == null) {
                return null;
            }

            return new ProfileProperty("textures", texture, signature);
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void updateCache(@NotNull UUID uuid, @NotNull String texture) {
        this.updateCache(uuid, texture, null);
    }

    @Override
    public void updateCacheAsync(@NotNull UUID uuid, @NotNull String texture) {
        this.updateCacheAsync(uuid, texture, null);
    }

    @Override
    public void updateCacheAsync(@NotNull UUID uuid, @NotNull String texture, @NotNull String signature) {
        new Thread(() -> this.updateCache(uuid, texture, signature)).start();
    }

    @Override
    public void updateCache(@NotNull UUID uuid, @NotNull String texture, @Nullable String signature) {
        if (this.isTextureCached(uuid)) {
            updateCacheInternal(uuid, texture, signature);
        } else {
            insertIntoCache(uuid, texture, signature);
        }
    }

    private void insertIntoCache(@NotNull UUID uuid, @NotNull String texture, @Nullable String signature) {
        try (Connection con = this.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO TEXTURES (ID, TEXTURE, SIGNATURE) VALUES (?, ?, ?);");
            ps.setString(1, uuid.toString());
            ps.setString(2, texture);
            ps.setString(3, signature);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private void updateCacheInternal(@NotNull UUID uuid, @NotNull String texture, @Nullable String signature) {
        try (Connection con = this.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE TEXTURES SET TEXTURE = ?, SIGNATURE = ? WHERE ID = ?;");
            ps.setString(1, texture);
            ps.setString(2, signature);
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private void initTables() {
        if (H2CacheManager.getInstance().ensureCompatibility(cacheComponentInfo) != null) {
            this.migrateFromVersion(cacheComponentInfo);
        }

        try (Connection con = this.getConnection()) {
            con.prepareStatement(
                       "CREATE TABLE IF NOT EXISTS TEXTURES (ID UUID PRIMARY KEY NOT NULL, TEXTURE BLOB NOT NULL, " +
                               "SIGNATURE BLOB, EXPIRES DATETIME);")
               .execute();
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private void migrateFromVersion(@NotNull CacheComponent oldComponentVersion) {
        // no migrations available yet. Hopefully it stays like this.
    }

}
