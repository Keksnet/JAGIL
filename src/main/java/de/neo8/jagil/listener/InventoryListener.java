package de.neo8.jagil.listener;

import de.neo8.jagil.JAGIL;
import de.neo8.jagil.annotation.Internal;
import de.neo8.jagil.gui.inventory.AnimatedInventory;
import de.neo8.jagil.gui.inventory.InventoryUserInterface;
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

public class InventoryListener implements Listener {

    private final JavaPlugin plugin;

    @Internal
    public InventoryListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Internal
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof InventoryUserInterface inventoryGui)) {
            return;
        }

        boolean cancelInventoryClick = inventoryGui.isCancelledByDefault();
        if (event.getCurrentItem() != null) {
            try {
                cancelInventoryClick = inventoryGui.handleInternal(event);
            } catch (Exception ex) {
                JAGIL.getLogger().warning("Unhandled exception in JAGIL GUI: " + ex.getMessage());

                if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                    ex.printStackTrace();
                }
            }
        }

        event.setCancelled(cancelInventoryClick);
        if (cancelInventoryClick) {
            player.updateInventory();
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> inventoryGui.handleLast(event), 1L);
    }

    @Internal
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof InventoryUserInterface inventoryGui)) {
            return;
        }

        boolean cancelInventoryDrag = inventoryGui.isCancelledByDefault();
        try {
            cancelInventoryDrag = inventoryGui.handleDrag(event);
        } catch (Exception ex) {
            JAGIL.getLoaderPlugin().getLogger().warning("Unhandled exception in JAGIL GUI: " + ex.getMessage());

            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                ex.printStackTrace();
            }
        }

        event.setCancelled(cancelInventoryDrag);
        if (cancelInventoryDrag) {
            player.updateInventory();
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> inventoryGui.handleDragLast(event), 1L);
    }

    @Internal
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        HumanEntity human = event.getPlayer();
        if (!(human instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof InventoryUserInterface inventoryGui)) {
            return;
        }

        try {
            inventoryGui.handleClose(event);
        } catch (Exception ex) {
            JAGIL.getLoaderPlugin().getLogger().warning("Unhandled exception in JAGIL GUI: " + ex.getMessage());

            if (JAGIL.getGlobalJAGILConfig().isDebugMode()) {
                ex.printStackTrace();
            }
        }

        if (inventoryGui instanceof AnimatedInventory animatableInventory) {
            animatableInventory.cancelAnimationTask();
        }

        player.updateInventory();
    }
}