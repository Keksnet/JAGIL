package de.neo8.jagil.cache.h2;

import de.neo8.jagil.JAGIL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class H2CacheBase {

    protected final Connection getConnection() throws SQLException {
        if (!JAGIL.getGlobalJAGILConfig().getCachingConfig().isEnabled()) {
            return null;
        }

        Path dataPath = JAGIL.getLoaderPlugin().getDataPath();
        if (!Files.exists(dataPath)) {
            throw new SQLException("Could not find text data file: " + dataPath);
        }

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        return DriverManager.getConnection("jdbc:h2:" + dataPath.toAbsolutePath().resolve("jagil-cache"));
    }

}
