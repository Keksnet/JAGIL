package de.neo8.jagil.gui.inventory;

import de.neo8.jagil.gui.UserInterface;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryUserInterface extends UserInterface, InventoryHolder {

    /**
     * Closes the {@link Inventory} of this {@link InventoryGui} save.
     */
    void closeInventory();

    /**
     * Fills this {@link InventoryUserInterface} with items
     */
    void fill();

    /**
     * Called on an {@link InventoryClickEvent} in this {@link Inventory}.
     * If you override this method you should not call super.
     *
     * @param event the fired {@link InventoryClickEvent}
     * @return if the event should be cancelled or not.
     */
    boolean handle(InventoryClickEvent event);

    /**
     * Like {@link InventoryGui#handle(InventoryClickEvent)} but optional and one tick later.
     *
     * @param event the fired {@link InventoryClickEvent}
     */
    void handleLast(InventoryClickEvent event);

    /**
     * Called when the cooldown is not yet over but the {@link InventoryClickEvent} is fired.
     *
     * @param event the fired {@link InventoryClickEvent}
     */
    void handleBlocked(InventoryClickEvent event);

    /**
     * Called on an {@link InventoryDragEvent} in this {@link Inventory}.
     *
     * @param event the fired {@link InventoryDragEvent}
     * @return if the event should be cancelled or not.
     */
    boolean handleDrag(InventoryDragEvent event);

    /**
     * Like {@link #handleDrag(InventoryDragEvent)} but optional and one tick later.
     *
     * @param event the fired {@link InventoryClickEvent}
     */
    void handleDragLast(InventoryDragEvent event);

    /**
     * Like {@link InventoryGui#handleDrag(InventoryDragEvent)} but optional and one tick later.
     *
     * @param event the fired {@link InventoryClickEvent}
     */
    void handleClose(InventoryCloseEvent event);

    /**
     * Returns if the event should be cancelled by default.
     *
     * @return the default cancel-value
     */
    boolean isCancelledByDefault();

    @Override
    default void render() {
        this.fill();
    }
}
