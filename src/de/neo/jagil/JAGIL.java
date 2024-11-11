package de.neo.jagil;

import de.neo.jagil.listener.GUIListener;
import de.neo.jagil.manager.GuiReaderManager;
import de.neo.jagil.reader.JsonGuiReader;
import de.neo.jagil.reader.XmlGuiReader;
import de.neo.jagil.util.GlobalJAGILConfig;
import io.papermc.paper.plugin.configuration.PluginMeta;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.manager.GUIManager;

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

		// Initializes GUI Manager
		GUIManager.getInstance();

		Bukkit.getPluginManager().registerEvents(new GUIListener(plugin), plugin);
	}

	public static Logger getLogger() {
		return loaderPlugin.getLogger();
	}
}
