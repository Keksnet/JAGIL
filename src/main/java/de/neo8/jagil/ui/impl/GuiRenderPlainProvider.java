package de.neo8.jagil.ui.impl;

import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.UIRenderPlainProvider;

public class GuiRenderPlainProvider implements UIRenderPlainProvider<InventoryGuiTypes.DataGui> {

    private final InventoryGuiTypes.DataGui data = new InventoryGuiTypes.DataGui();

    @Override
    public InventoryGuiTypes.DataGui getRenderPlain() {
        return this.data;
    }
}
