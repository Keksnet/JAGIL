package de.neo8.jagil.reader.builtin;

import de.neo8.jagil.JAGIL;
import de.neo8.jagil.reader.CustomFileProvider;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Implements {@link CustomFileProvider} and handles the "plugin:" scheme.
 * {@link #readFile(String)} throws an UnsupportedOperationException.
 */
public class PluginFileProvider implements CustomFileProvider {

    private final boolean debug = JAGIL.getGlobalJAGILConfig().isDebugMode();

    @Override
    public @NotNull Path convertToPath(@NotNull String path) {
        if (!path.startsWith("plugin:")) {
            throw new IllegalArgumentException("Path must start with 'plugin:'");
        }

        if (this.debug) {
            JAGIL.getLogger().info(PluginFileProvider.class.getName() + " is loading gui file from: '" + path + "'");
        }

        Class<? extends CustomFileProvider> uiPluginClass = getClass();
        if (this.debug) {
            JAGIL.getLogger().info("Using " + uiPluginClass.getName() + " for plugin path '" + path + "'");
        }

        // the string "plugin:" has 7 characters
        String guiFilePath = path.substring(7);
        URL fileUrl = uiPluginClass.getResource(guiFilePath);
        if (fileUrl == null) {
            throw new IllegalArgumentException("File '" + guiFilePath + "' not found");
        }

        Path filePath;
        try {
            filePath = Path.of(fileUrl.toURI()).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return filePath;
    }

    @Override
    public @NotNull String readFile(@NotNull String path) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
