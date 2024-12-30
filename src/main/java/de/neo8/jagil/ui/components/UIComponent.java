package de.neo8.jagil.ui.components;

import de.neo8.jagil.ui.UIRenderPaneProvider;

import java.awt.*;

public interface UIComponent {

    String getId();

    Point getPosition();

    Dimension getSize();

    Rectangle getBounds();

    int getPriority();

    void render(UIRenderPaneProvider<?> renderPlain);

}
