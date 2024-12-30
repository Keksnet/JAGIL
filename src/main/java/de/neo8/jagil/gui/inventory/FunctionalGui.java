package de.neo8.jagil.gui.inventory;

import de.neo8.jagil.exception.JAGILException;
import de.neo8.jagil.reader.GuiReaderManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides a GUI implementation for functional programming and with lambda support.
 */
public class FunctionalGui extends InventoryGui {

    private final Consumer<InventoryGui> fill;
    private final Function<InventoryGui, Boolean> handle;
    private final Consumer<InventoryGui> handleLater;
    private final Function<InventoryGui, Boolean> drag;
    private final Consumer<InventoryGui> dragLater;
    private final Consumer<InventoryGui> close;
    private final Function<InventoryGui, Boolean> defaultCancel;

    protected FunctionalGui(String guiFile, Consumer<InventoryGui> fillMethod, Function<InventoryGui, Boolean> handleMethod,
                            Consumer<InventoryGui> handleLastMethod, Function<InventoryGui, Boolean> handleDragMethod,
                            Consumer<InventoryGui> handleDragLastMethod, Consumer<InventoryGui> handleCloseMethod,
                            Function<InventoryGui, Boolean> cancelDefault, Consumer<InventoryGui> customConstructorCallback)
            throws IOException {
        this(Paths.get(guiFile), fillMethod, handleMethod, handleLastMethod, handleDragMethod, handleDragLastMethod,
                handleCloseMethod, cancelDefault, customConstructorCallback);
    }

    protected FunctionalGui(String guiFile, Player p, Consumer<InventoryGui> fillMethod,
                            Function<InventoryGui, Boolean> handleMethod, Consumer<InventoryGui> handleLastMethod,
                            Function<InventoryGui, Boolean> handleDragMethod, Consumer<InventoryGui> handleDragLastMethod,
                            Consumer<InventoryGui> handleCloseMethod, Function<InventoryGui, Boolean> cancelDefault,
                            Consumer<InventoryGui> customConstructorCallback)
            throws IOException {
        this(Paths.get(guiFile), p, fillMethod, handleMethod, handleLastMethod, handleDragMethod, handleDragLastMethod,
                handleCloseMethod, cancelDefault, customConstructorCallback);
    }

    protected FunctionalGui(Path guiFile, Consumer<InventoryGui> fillMethod, Function<InventoryGui, Boolean> handleMethod,
                            Consumer<InventoryGui> handleLastMethod, Function<InventoryGui, Boolean> handleDragMethod,
                            Consumer<InventoryGui> handleDragLastMethod, Consumer<InventoryGui> handleCloseMethod,
                            Function<InventoryGui, Boolean> cancelDefault, Consumer<InventoryGui> customConstructorCallback)
            throws IOException {
        super(GuiReaderManager.getInstance().readFile(guiFile, null));
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Path guiFile, Player p, Consumer<InventoryGui> fillMethod,
                            Function<InventoryGui, Boolean> handleMethod, Consumer<InventoryGui> handleLastMethod,
                            Function<InventoryGui, Boolean> handleDragMethod, Consumer<InventoryGui> handleDragLastMethod,
                            Consumer<InventoryGui> handleCloseMethod, Function<InventoryGui, Boolean> cancelDefault,
                            Consumer<InventoryGui> customConstructorCallback)
            throws IOException {
        super(GuiReaderManager.getInstance().readFile(guiFile, null), p);
        this.fill = fillMethod;
        this.handle = handleMethod;
        this.handleLater = handleLastMethod;
        this.drag = handleDragMethod;
        this.dragLater = handleDragLastMethod;
        this.close = handleCloseMethod;
        this.defaultCancel = cancelDefault;
        executeCallback(customConstructorCallback);
    }

    protected FunctionalGui(Component name, int size, Consumer<InventoryGui> fillMethod, Function<InventoryGui, Boolean> handleMethod,
                            Consumer<InventoryGui> handleLastMethod, Function<InventoryGui, Boolean> handleDragMethod,
                            Consumer<InventoryGui> handleDragLastMethod, Consumer<InventoryGui> handleCloseMethod,
                            Function<InventoryGui, Boolean> cancelDefault, Consumer<InventoryGui> customConstructorCallback) {
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

    protected FunctionalGui(Component name, int size, Player p, Consumer<InventoryGui> fillMethod,
                            Function<InventoryGui, Boolean> handleMethod, Consumer<InventoryGui> handleLastMethod,
                            Function<InventoryGui, Boolean> handleDragMethod, Consumer<InventoryGui> handleDragLastMethod,
                            Consumer<InventoryGui> handleCloseMethod, Function<InventoryGui, Boolean> cancelDefault,
                            Consumer<InventoryGui> customConstructorCallback) {
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

    protected FunctionalGui(Component name, InventoryType type, Consumer<InventoryGui> fillMethod,
                            Function<InventoryGui, Boolean> handleMethod, Consumer<InventoryGui> handleLastMethod,
                            Function<InventoryGui, Boolean> handleDragMethod, Consumer<InventoryGui> handleDragLastMethod,
                            Consumer<InventoryGui> handleCloseMethod, Function<InventoryGui, Boolean> cancelDefault,
                            Consumer<InventoryGui> customConstructorCallback) {
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

    protected FunctionalGui(Component name, InventoryType type, Player p, Consumer<InventoryGui> fillMethod,
                            Function<InventoryGui, Boolean> handleMethod, Consumer<InventoryGui> handleLastMethod,
                            Function<InventoryGui, Boolean> handleDragMethod, Consumer<InventoryGui> handleDragLastMethod,
                            Consumer<InventoryGui> handleCloseMethod, Function<InventoryGui, Boolean> cancelDefault,
                            Consumer<InventoryGui> customConstructorCallback) {
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

    private boolean executeCallback(Function<InventoryGui, Boolean> callback) {
        boolean cancel = defaultCancel == null || executeCallback(defaultCancel);
        if (callback == null) return cancel;
        try {
            cancel = callback.apply(this);
        } catch (Exception e) {
            throw new JAGILException("Exception occurred in FunctionalGui", e);
        }
        return cancel;
    }

    private void executeCallback(Consumer<InventoryGui> callback) {
        if (callback == null) return;
        try {
            callback.accept(this);
        } catch (Exception e) {
            throw new JAGILException("Exception occurred in FunctionalGui", e);
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
