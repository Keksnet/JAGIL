package de.neo8.jagil.ui.impl;

import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.ClickType;

import java.awt.*;

public record UIAction<T>(Entity entity, Class<T> uiType, Point click, ClickType clickType) {
}
