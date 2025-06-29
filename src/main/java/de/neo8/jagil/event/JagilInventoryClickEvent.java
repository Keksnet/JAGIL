package de.neo8.jagil.event;

import de.neo8.jagil.JAGIL;
import de.neo8.jagil.gui.inventory.InventoryGui;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class JagilInventoryClickEvent extends InventoryClickEvent {

    @Getter
    @NotNull
    private final String itemId;

    @NotNull
    private final InventoryGui inventoryGui;

    public JagilInventoryClickEvent(@NotNull InventoryClickEvent bukkitEvent, @NotNull InventoryGui inventoryGui) {
        super(bukkitEvent.getView(), bukkitEvent.getSlotType(), bukkitEvent.getRawSlot(), bukkitEvent.getClick(),
              bukkitEvent.getAction(), bukkitEvent.getHotbarButton());

        // init jagil event
        ItemStack itemStack = bukkitEvent.getCurrentItem();
        if (itemStack == null) {
            throw new NullPointerException("ItemStack is null");
        }

        PersistentDataContainerView dataView = itemStack.getPersistentDataContainer();
        if (!dataView.has(JAGIL.getJagilIdentifier(), PersistentDataType.STRING)) {
            throw new IllegalArgumentException("ItemStack does not contain a JAGIL identifier");
        }

        String itemId = dataView.get(JAGIL.getJagilIdentifier(), PersistentDataType.STRING);
        if (itemId == null) {
            throw new NullPointerException("JAGIL identifier is null");
        }

        this.itemId = itemId;
        this.inventoryGui = inventoryGui;
    }

    @NotNull
    public InventoryGuiTypes.GuiItem getGuiItem() {
        InventoryGuiTypes.DataGui dataGui = this.inventoryGui.getGuiData();
        if (dataGui == null) {
            throw new IllegalStateException("GuiData is null");
        }

        InventoryGuiTypes.GuiItem guiItem = dataGui.getGuiItem(this.itemId);
        if (guiItem == null) {
            throw new IllegalStateException("GuiItem with id " + this.itemId + " is null");
        }

        return guiItem;
    }
}
