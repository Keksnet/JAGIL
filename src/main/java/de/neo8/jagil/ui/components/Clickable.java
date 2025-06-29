package de.neo8.jagil.ui.components;

import de.neo8.jagil.ui.impl.UIAction;
import org.jetbrains.annotations.NotNull;

public interface Clickable {

    void click(@NotNull UIAction<?> click);

}
