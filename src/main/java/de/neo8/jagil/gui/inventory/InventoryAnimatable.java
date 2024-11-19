package de.neo8.jagil.gui.inventory;

import java.util.concurrent.atomic.AtomicInteger;

public interface InventoryAnimatable {

    /**
     * This method is called once a tick to animate the {@link InventoryGui}.
     *
     * @param tick the current tick after the {@link InventoryGui} was opened
     */
    void animate(long tick, AtomicInteger atomicLastItem);

}
