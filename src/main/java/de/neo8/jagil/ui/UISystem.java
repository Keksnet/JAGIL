package de.neo8.jagil.ui;

import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.components.UIComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface UISystem<P> {

    int getSize();

    boolean hasComponent(@NotNull String id);

    void addComponent(@NotNull UIComponent component) throws IllegalArgumentException;

    @Nullable
    UIComponent getComponent(@NotNull String id);

    @Nullable
    <T extends UIComponent & Clickable> T getClickedComponent(@NotNull Point click);

    void removeComponent(@NotNull UIComponent component);

    @NotNull
    UIRenderPaneProvider<P> getRenderProvider();

    void render();

    void render(@NotNull UIRenderPaneProvider<?> renderPaneProvider);

}
