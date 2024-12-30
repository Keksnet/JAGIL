package de.neo8.jagil.reader;

import de.neo8.jagil.gui.UserInterfaceContextHolder;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface GuiReader<T> extends UserInterfaceContextHolder {

    InventoryGuiTypes.DataGui read(String content) throws IOException;

}
