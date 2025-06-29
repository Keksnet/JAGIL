package de.neo8.jagil.ui.impl;

import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.UIRenderPaneProvider;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class InventoryRenderPaneProvider implements UIRenderPaneProvider<InventoryGuiTypes.DataGui> {

    @Getter
    @NotNull
    private final InventoryGuiTypes.DataGui renderPane = new InventoryGuiTypes.DataGui();
}
