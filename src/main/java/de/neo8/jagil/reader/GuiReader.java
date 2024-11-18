package de.neo8.jagil.reader;

import de.neo8.jagil.gui.GuiTypes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public interface GuiReader<T> {

    boolean supportsFile(Path filePath, String content);

    GuiTypes.DataGui read(String content) throws IOException;

    void parseItem(GuiTypes.DataGui gui, T itemObject);

    void parseUIComponent(GuiTypes.DataGui gui, T uiComponentObject)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

}
