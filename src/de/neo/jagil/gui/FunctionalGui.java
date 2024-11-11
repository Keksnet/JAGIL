package de.neo.jagil.gui;

import de.neo.jagil.manager.GuiReaderManager;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides a GUI implementation for functional programming and with lambda support.
 */
public class FunctionalGui extends GUI {

    private final Consumer<GUI> fill;
    private final Function<GUI, Boolean> handle;
    private final Consumer<GUI> handleLater;
    private final Function<GUI, Boolean> drag;
    private final Consumer<GUI> dragLater;
    private final Consumer<GUI> close;
    private final Function<GUI, Boolean> defaultCancel;

    protected FunctionalGui(String guiFile, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                            Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod,
                            Consumer<GUI> handleDragLastMethod, Consumer<GUI> handleCloseMethod,
                            Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        this(Paths.get(guiFile), fillMethod, handleMethod, handleLastMethod, handleDragMethod, handleDragLastMethod,
                handleCloseMethod, cancelDefault, customConstructorCallback);
    }

    protected FunctionalGui(String guiFile, OfflinePlayer p, Consumer<GUI> fillMethod,
                            Function<GUI, Boolean> handleMethod, Consumer<GUI> handleLastMethod,
                            Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                            Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault,
                            Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        this(Paths.get(guiFile), p, fillMethod, handleMethod, handleLastMethod, handleDragMethod, handleDragLastMethod,
                handleCloseMethod, cancelDefault, customConstructorCallback);
    }

    protected FunctionalGui(Path guiFile, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                            Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod,
                            Consumer<GUI> handleDragLastMethod, Consumer<GUI> handleCloseMethod,
                            Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        super(GuiReaderManager.getInstance().readFile(guiFile));
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Path guiFile, OfflinePlayer p, Consumer<GUI> fillMethod,
                            Function<GUI, Boolean> handleMethod, Consumer<GUI> handleLastMethod,
                            Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                            Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault,
                            Consumer<GUI> customConstructorCallback)
            throws XMLStreamException, IOException {
        super(GuiReaderManager.getInstance().readFile(guiFile), p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Component name, int size, Consumer<GUI> fillMethod, Function<GUI, Boolean> handleMethod,
                            Consumer<GUI> handleLastMethod, Function<GUI, Boolean> handleDragMethod,
                            Consumer<GUI> handleDragLastMethod, Consumer<GUI> handleCloseMethod,
                            Function<GUI, Boolean> cancelDefault, Consumer<GUI> customConstructorCallback) {
        super(name, size);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Component name, int size, OfflinePlayer p, Consumer<GUI> fillMethod,
                            Function<GUI, Boolean> handleMethod, Consumer<GUI> handleLastMethod,
                            Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                            Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault,
                            Consumer<GUI> customConstructorCallback) {
        super(name, size, p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Component name, InventoryType type, Consumer<GUI> fillMethod,
                            Function<GUI, Boolean> handleMethod, Consumer<GUI> handleLastMethod,
                            Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                            Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault,
                            Consumer<GUI> customConstructorCallback) {
        super(name, type);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Component name, InventoryType type, OfflinePlayer p, Consumer<GUI> fillMethod,
                            Function<GUI, Boolean> handleMethod, Consumer<GUI> handleLastMethod,
                            Function<GUI, Boolean> handleDragMethod, Consumer<GUI> handleDragLastMethod,
                            Consumer<GUI> handleCloseMethod, Function<GUI, Boolean> cancelDefault,
                            Consumer<GUI> customConstructorCallback) {
        super(name, type, p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    private boolean executeCallback(Function<GUI, Boolean> callback) {
        boolean cancel = defaultCancel == null || executeCallback(defaultCancel);
        if(callback == null) return cancel;
        try {
            cancel = callback.apply(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return cancel;
    }

    private void executeCallback(Consumer<GUI> callback) {
        if(callback == null) return;
        try {
            callback.accept(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fill() {
        executeCallback(fill);
    }

    @Override
    public boolean handle(InventoryClickEvent e) {
        return executeCallback(handle);
    }

    @Override
    public void handleLast(InventoryClickEvent e) {
        executeCallback(handleLater);
    }

    @Override
    public boolean handleDrag(InventoryDragEvent e) {
        return executeCallback(drag);
    }

    @Override
    public void handleDragLast(InventoryDragEvent e) {
        executeCallback(dragLater);
    }

    @Override
    public void handleClose(InventoryCloseEvent e) {
        executeCallback(close);
    }

    @Override
    public boolean isCancelledByDefault() {
        return defaultCancel == null || executeCallback(defaultCancel);
    }
}
