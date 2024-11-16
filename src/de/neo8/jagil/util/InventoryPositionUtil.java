package de.neo8.jagil.util;

public class InventoryPositionUtil {

    public static int toSlot(int x, int y) {
        return y + (x * 9);
    }

}
