package de.neo8.jagil;

import de.neo8.jagil.listener.GUIListener;
import de.neo8.jagil.manager.GuiReaderManager;
import de.neo8.jagil.reader.JsonGuiReader;
import de.neo8.jagil.reader.XmlGuiReader;
import de.neo8.jagil.util.GlobalJAGILConfig;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

	static {
		globalJAGILConfig = GlobalJAGILConfig.builder()
				.loaderName("none")
				.debugMode(false)
				.build();

		GuiReaderManager.getInstance().register(new JsonGuiReader());
		GuiReaderManager.getInstance().register(new XmlGuiReader());
	}

	/**
	 * Initializes JAGIL.
	 *
	 * @param plugin your {@link JavaPlugin} instance.
	 */
	@SuppressWarnings({ "UnstableApiUsage" })
	public static void init(JavaPlugin plugin) {
		PluginMeta pluginMeta = plugin.getPluginMeta();
		plugin.getLogger().info("Registered JAGIL from " + pluginMeta.getName() + " " + pluginMeta.getVersion());

		Bukkit.getPluginManager().registerEvents(new GUIListener(plugin), plugin);
	}

	public static Logger getLogger() {
		return loaderPlugin.getLogger();
	}
}
