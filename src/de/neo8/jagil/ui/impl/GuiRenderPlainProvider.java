package de.neo8.jagil.ui.impl;

import de.neo8.jagil.gui.GuiTypes;
import de.neo8.jagil.ui.UIRenderPlainProvider;

public class GuiRenderPlainProvider implements UIRenderPlainProvider<GuiTypes.DataGui> {

    private final GuiTypes.DataGui data = new GuiTypes.DataGui();

    @Override
    public GuiTypes.DataGui getRenderPlain() {
        return this.data;
    }
}
