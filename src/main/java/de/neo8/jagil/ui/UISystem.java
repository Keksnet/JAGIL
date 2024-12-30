package de.neo8.jagil.ui;

import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.components.UIComponent;

import java.awt.*;

public interface UISystem<P> {

    int getSize();

    boolean hasComponent(String id);

    void addComponent(UIComponent component) throws IllegalArgumentException;

    UIComponent getComponent(String id);

    <T extends UIComponent & Clickable> T getClickedComponent(Point click);

    void removeComponent(UIComponent component);

    UIRenderPaneProvider<P> getRenderProvider();

    void render();

    void render(UIRenderPaneProvider<?> renderPaneProvider);

}
