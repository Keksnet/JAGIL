package de.neo8.jagil.ui.impl;

import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.ClickType;

import java.awt.*;

public class UIAction<T> {

    private final Entity entity;
    private final Class<T> uiType;
    private final Point click;
    private final ClickType clickType;

    public UIAction(Entity entity, Class<T> uiType, Point click, ClickType clickType) {
        this.entity = entity;
        this.uiType = uiType;
        this.click = click;
        this.clickType = clickType;
    }

    public Entity getEntity() {
        return entity;
    }

    public Class<T> getUiType() {
        return uiType;
    }

    public Point getClick() {
        return click;
    }

    public ClickType getClickType() {
        return clickType;
    }

}
