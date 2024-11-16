package de.neo.jagil.listener;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class GUIListener implements Listener {
	
	private final JavaPlugin plugin;

	@Internal
	public GUIListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Internal
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) {
			return;
		}

		HumanEntity human = e.getWhoClicked();
		if (!(human instanceof Player player)) {
			return;
		}

		InventoryHolder holder = e.getClickedInventory().getHolder();
		if (!(holder instanceof GUI gui)) {
			return;
		}

        boolean cancelInventoryClick = gui.isCancelledByDefault();
		if (e.getCurrentItem() != null) {
			try {
				cancelInventoryClick = gui.handleInternal(e);
			} catch (Exception ex) {
				JAGIL.getLogger().warning("Unhandled exception in JAGIL GUI: " + ex.getMessage());

				if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
					ex.printStackTrace();
				}
			}
		}

		e.setCancelled(cancelInventoryClick);
		if (cancelInventoryClick) {
			player.updateInventory();
		}

		Bukkit.getScheduler().runTaskLater(this.plugin, () -> gui.handleLast(e), 1L);
	}

	@Internal
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		HumanEntity human = e.getWhoClicked();
		if (!(human instanceof Player player)) {
			return;
		}

		InventoryHolder holder = e.getInventory().getHolder();
		if (!(holder instanceof GUI gui)) {
			return;
		}

		boolean cancelInventoryDrag = gui.isCancelledByDefault();
		try {
			cancelInventoryDrag = gui.handleDrag(e);
		} catch (Exception ex) {
			JAGIL.getLoaderPlugin().getLogger().warning("Unhandled exception in JAGIL GUI: " + ex.getMessage());

			if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
				ex.printStackTrace();
			}
		}

		e.setCancelled(cancelInventoryDrag);
		if (cancelInventoryDrag) {
			player.updateInventory();
		}

		Bukkit.getScheduler().runTaskLater(this.plugin, () -> gui.handleDragLast(e), 1L);
	}

	@Internal
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		HumanEntity human = e.getPlayer();
		if (!(human instanceof Player player)) {
			return;
		}

		InventoryHolder holder = e.getInventory().getHolder();
		if (!(holder instanceof GUI gui)) {
			return;
		}

		try {
			gui.handleClose(e);
		} catch (Exception ex) {
			JAGIL.getLoaderPlugin().getLogger().warning("Unhandled exception in JAGIL GUI: " + ex.getMessage());

			if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
				ex.printStackTrace();
			}
		}

		if (gui.animationTaskId != -1) {
			Bukkit.getScheduler().cancelTask(gui.animationTaskId);
		}
		gui.animationTaskId = -1;

		player.updateInventory();
	}
}