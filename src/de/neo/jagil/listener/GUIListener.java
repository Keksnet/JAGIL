package de.neo.jagil.listener;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.debug.Hook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.gui.GUI;
import de.neo.jagil.manager.GUIManager;

public class GUIListener implements Listener {
	
	private JavaPlugin plugin;

	@Internal
	public GUIListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Internal
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() != null) {
			if (GUIManager.getInstance().isGUIByJAGIL(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId())) {
				GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId());
				if (gui == null) {
					e.setCancelled(true);
					throw new JAGILException("Internal error: GUI is null but registered in GUIManager!");
				}
				if(JAGIL.debugMode) {
					JAGIL.debugger.executeHook(Hook.CLICK_LISTENER, gui, e);
				}
				boolean cancel = gui.isCancelledByDefault();
				if (e.getCurrentItem() != null && e.getClickedInventory() != e.getWhoClicked().getInventory()) {
					try {
						cancel = gui.handleInternal(e);
					}
				}
				e.setCancelled(cancel);
				if (cancel) {
					((Player) e.getWhoClicked()).updateInventory();
				}
				Bukkit.getScheduler().runTaskLater(this.plugin, () -> gui.handleLast(e), 1L);
			}
		}
	}

	@Internal
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		String identifier = e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId();
		if(GUIManager.getInstance().isGUIByJAGIL(identifier)) {
			GUI gui = GUIManager.getInstance().getGUI(identifier);
			if(gui == null) {
				e.setCancelled(true);
				throw new JAGILException("Internal error: GUI is null but registered in GUIManager!");
			}
			if(JAGIL.debugMode) {
				JAGIL.debugger.executeHook(Hook.DRAG_LISTENER, gui, e);
			}
			boolean cancel = gui.isCancelledByDefault();
			try {
				cancel = gui.handleDrag(e);
			}catch (Exception ex) {
				ex.printStackTrace();
			}
			e.setCancelled(cancel);
			if(cancel) {
				((Player)e.getWhoClicked()).updateInventory();
			}
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> gui.handleDragLast(e), 1L);
		}
	}

	@Internal
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		String identifier = e.getView().getTitle() + "-" + e.getPlayer().getUniqueId();
		if(GUIManager.getInstance().isGUIByJAGIL(identifier)) {
			GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getPlayer().getUniqueId());
			if(JAGIL.debugMode) {
				JAGIL.debugger.executeHook(Hook.CLOSE_LISTENER, gui, e);
			}
			if(gui != null) {
				if(e.getInventory() != e.getPlayer().getInventory()) {
					gui.handleClose(e);
				}
			}
			if(!GUIManager.getInstance().unlockIfLocked(identifier)) {
				GUIManager.getInstance().unregister(identifier);
			}
			Player p = (Player) e.getPlayer();
			p.updateInventory();
		}
	}
}