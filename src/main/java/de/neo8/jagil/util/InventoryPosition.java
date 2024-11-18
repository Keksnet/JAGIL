package de.neo8.jagil.util;

import java.awt.*;

public class InventoryPosition {

    public int x;
    public int y;

    public InventoryPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public InventoryPosition(InventoryPosition pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public InventoryPosition set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public InventoryPosition set(InventoryPosition pos) {
        this.x = pos.x;
        this.y = pos.y;
        return this;
    }

    public InventoryPosition move(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public InventoryPosition move(InventoryPosition pos) {
        this.x += pos.x;
        this.y += pos.y;
        return this;
    }

    public int toSlot() {
        return y * 9 + x;
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    public static InventoryPosition fromSlot(int slot) {
        return new InventoryPosition(slot % 9, slot / 9);
    }

    public static InventoryPosition DEFAULT = new InventoryPosition(0, 0);

}
