package de.neo8.jagil.ui.components;

import de.neo8.jagil.ui.UIRenderPaneProvider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface UIComponent {

    @NotNull
    String getId();

    @NotNull
    Point getPosition();

    @NotNull
    Dimension getSize();

    @NotNull
    Rectangle getBounds();

    int getPriority();

    void render(@NotNull UIRenderPaneProvider<?> renderPlain);

}
