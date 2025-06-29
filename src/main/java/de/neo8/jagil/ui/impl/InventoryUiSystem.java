package de.neo8.jagil.ui.impl;

import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.UIRenderPaneProvider;
import de.neo8.jagil.ui.UISystem;
import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.components.UIComponent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryUiSystem implements UISystem<InventoryGuiTypes.DataGui> {

    private final int size;
    private final HashMap<String, UIComponent> components;

    @Getter
    @NotNull
    private final UIRenderPaneProvider<InventoryGuiTypes.DataGui> renderProvider;

    public InventoryUiSystem(int size) {
        this.size = size;
        this.components = new HashMap<>();
        this.renderProvider = new InventoryRenderPaneProvider();
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public boolean hasComponent(@NotNull String id) {
        return this.components.containsKey(id);
    }

    @Override
    public void addComponent(@NotNull UIComponent component) throws IllegalArgumentException {
        String id = component.getId();
        if (this.components.containsKey(id))
            throw new IllegalArgumentException("Component with id '" + id + "' already exists");
        this.components.put(id, component);
    }

    @Override
    public UIComponent getComponent(@NotNull String id) {
        return this.components.get(id);
    }

    @Override
    public <T extends UIComponent & Clickable> T getClickedComponent(@NotNull Point click) {
        AtomicReference<T> result = new AtomicReference<>();
        this.components.values()
                .stream()
                .filter(Objects::nonNull)
                .filter(component -> component instanceof Clickable)
                .filter(component -> component.getBounds().contains(click))
                .max(Comparator.comparingInt(UIComponent::getPriority))
                .ifPresentOrElse(component -> result.set((T) component), () -> result.set(null));
        return result.get();
    }

    @Override
    public void removeComponent(@NotNull UIComponent component) {
        this.components.remove(component.getId());
    }

    @Override
    public void render() {
        this.render(this.renderProvider);
    }

    @Override
    public void render(@NotNull UIRenderPaneProvider<?> renderPaneProvider) {
        this.components.values()
                .stream()
                .sorted(Comparator.comparingInt(UIComponent::getPriority).reversed())
                .forEach(component -> component.render(renderPaneProvider));
    }
}
