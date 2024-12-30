package de.neo8.jagil.ui.impl;

import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.UIRenderPaneProvider;

public class InventoryRenderPaneProvider implements UIRenderPaneProvider<InventoryGuiTypes.DataGui> {

    private final InventoryGuiTypes.DataGui data = new InventoryGuiTypes.DataGui();

    @Override
    public InventoryGuiTypes.DataGui getRenderPane() {
        return this.data;
    }
}
