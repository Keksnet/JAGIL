package de.neo8.jagil;

import de.neo8.jagil.config.CachingConfig;
import de.neo8.jagil.config.GlobalJAGILConfig;
import de.neo8.jagil.listener.InventoryListener;
import de.neo8.jagil.reader.GuiReaderManager;
import de.neo8.jagil.reader.JsonGuiReader;
import de.neo8.jagil.reader.XmlGuiReader;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JAGIL
 */
public class JAGIL {

    @Getter
    @Setter
    private static JavaPlugin loaderPlugin;

    @Getter
    @Setter
    private static GlobalJAGILConfig globalJAGILConfig;

    @Getter
    private final static NamespacedKey jagilIdentifier = new NamespacedKey("jagil", "identifier");

    static {
        globalJAGILConfig = GlobalJAGILConfig.builder()
                                             .loaderName("none")
                                             .debugMode(false)
                                             .supportedFeatures(List.of())
                                             .cachingConfig(CachingConfig.builder()
                                                                         .enabled(true)
                                                                         .providerConfig(Map.of())
                                                                         .build())
                                             .build();

        GuiReaderManager.getInstance().register(JsonGuiReader.Provider.getInstance());
        GuiReaderManager.getInstance().register(XmlGuiReader.Provider.getInstance());
    }

    /**
     * Initializes JAGIL.
     *
     * @param plugin your {@link JavaPlugin} instance.
     */
    @SuppressWarnings({"UnstableApiUsage"})
    public static void init(JavaPlugin plugin) {
        PluginMeta pluginMeta = plugin.getPluginMeta();
        plugin.getLogger().info("Registered JAGIL from " + pluginMeta.getName() + " " + pluginMeta.getVersion());

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    public static Logger getLogger() {
        return loaderPlugin.getLogger();
    }
}
