package de.neo8.jagil.reader;

import de.neo8.jagil.gui.inventory.InventoryGuiTypes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public interface GuiReader<T> {

    boolean supportsFile(Path filePath, String content);

    InventoryGuiTypes.DataGui read(String content) throws IOException;

    void parseItem(InventoryGuiTypes.DataGui gui, T itemObject);

    void parseUIComponent(InventoryGuiTypes.DataGui gui, T uiComponentObject)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

}
