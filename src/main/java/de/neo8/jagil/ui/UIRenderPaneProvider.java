package de.neo8.jagil.ui;

import org.jetbrains.annotations.NotNull;

public interface UIRenderPaneProvider<T> {

    @NotNull
    T getRenderPane();

}
