package de.neo8.jagil.cache.h2;

import de.neo8.jagil.JAGIL;
import de.neo8.jagil.cache.CacheComponent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2CacheManager extends H2CacheBase {

    @Getter
    private final static H2CacheManager instance = new H2CacheManager();

    private H2CacheManager() {
        this.initTables();
    }

    @Nullable
    public CacheComponent ensureCompatibility(@NotNull CacheComponent currentComponent) {
        try (Connection con = this.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT VERSION FROM CACHE_VERSIONS WHERE COMPONENT = ?;");
            ps.setString(1, currentComponent.getName());
            ResultSet rs = ps.executeQuery();
            CacheComponent databaseComponent = null;
            if (rs.next()) {
                databaseComponent = new CacheComponent(
                        rs.getString("COMPONENT"),
                        rs.getInt("VERSION")
                );
            }
            ps.close();
            rs.close();

            if (databaseComponent == null || databaseComponent.getVersion() == currentComponent.getVersion()) {
                // database schema is in sync
                return null;
            }

            if (databaseComponent.getVersion() < currentComponent.getVersion()) {
                // data has to be updated
                return databaseComponent;
            }

            // downgrade detected
            throw new IllegalStateException("The database has a newer version of the cache component. This can be caused by downgrading. Please upgrade again or disable caching.");
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
            throw new IllegalStateException(e);
        }
    }

    private void initTables() {
        try (Connection con = this.getConnection()) {
            con.prepareStatement("CREATE TABLE IF NOT EXISTS CACHE_VERSIONS (COMPONENT VARCHAR(16) PRIMARY KEY NOT NULL, VERSION INTEGER NOT NULL);").execute();
        } catch (SQLException e) {
            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

}
