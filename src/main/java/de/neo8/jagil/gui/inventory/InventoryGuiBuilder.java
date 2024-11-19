package de.neo8.jagil.gui.inventory;

import de.neo8.jagil.exception.BuildException;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryGuiBuilder {

    private boolean fromXml;
    private boolean universal;

    // Vars for non-universal
    private OfflinePlayer p;

    // Vars for Xml
    private String guiFile;
    private Path guiFilePath;

    // Vars for normal
    private Component title;
    private int size;
    private InventoryType type;

    // Vars for events
    private Consumer<InventoryGui> fill;
    private Function<InventoryGui, Boolean> handle;
    private Consumer<InventoryGui> handleLater;
    private Function<InventoryGui, Boolean> drag;
    private Consumer<InventoryGui> dragLater;
    private Consumer<InventoryGui> close;
    private Function<InventoryGui, Boolean> defaultCancel;
    private Consumer<InventoryGui> customConstructorCallback;

    public InventoryGuiBuilder() {
        this(false, false);
    }

    public InventoryGuiBuilder(boolean fromXml, boolean universal) {
        this.fromXml = fromXml;
        this.universal = universal;
    }

    public InventoryGuiBuilder setXmlMode(boolean fromXml) {
        this.fromXml = fromXml;
        return this;
    }

    public InventoryGuiBuilder setUniversalMode(boolean universal) {
        this.universal = universal;
        return this;
    }

    public InventoryGuiBuilder forPlayer(OfflinePlayer p) {
        if (p == null) throw new BuildException("Player cannot be null!");
        if (universal) throw new BuildException("Cannot set player for universal GUI!");
        this.p = p;
        return this;
    }

    public InventoryGuiBuilder setGuiFile(String guiFile) {
        if (!fromXml) throw new BuildException("Cannot set xml file for normal GUI!");
        this.guiFile = guiFile;
        return this;
    }

    public InventoryGuiBuilder setGuiFilePath(Path guiFilePath) {
        if (!fromXml) throw new BuildException("Cannot set xml path for normal GUI!");
        this.guiFilePath = guiFilePath;
        return this;
    }

    public InventoryGuiBuilder withTitle(Component title) {
        if (fromXml) throw new BuildException("Cannot set title for xml GUI!");
        this.title = title;
        return this;
    }

    public InventoryGuiBuilder withSize(int size) {
        if (fromXml) throw new BuildException("Cannot set size for xml GUI!");
        if (size < 1 || size > 54) throw new BuildException("Size must be between 1 and 54!");
        if (size % 9 != 0) throw new BuildException("Size must be a multiple of 9!");
        this.size = size;
        return this;
    }

    public InventoryGuiBuilder withType(InventoryType type) {
        if (fromXml) throw new BuildException("Cannot set type for xml GUI!");
        this.type = type;
        return this;
    }

    public InventoryGuiBuilder onFill(Consumer<InventoryGui> fill) {
        if (fill == null) throw new BuildException("Fill cannot be null!");
        this.fill = fill;
        return this;
    }

    public InventoryGuiBuilder onClick(Function<InventoryGui, Boolean> handle) {
        if (handle == null) throw new BuildException("handle cannot be null!");
        this.handle = handle;
        return this;
    }

    public InventoryGuiBuilder afterClick(Consumer<InventoryGui> handleLater) {
        if (handleLater == null) throw new BuildException("handleLater cannot be null!");
        this.handleLater = handleLater;
        return this;
    }

    public InventoryGuiBuilder onDrag(Function<InventoryGui, Boolean> drag) {
        if (drag == null) throw new BuildException("drag cannot be null!");
        this.drag = drag;
        return this;
    }

    public InventoryGuiBuilder afterDrag(Consumer<InventoryGui> dragLater) {
        if (dragLater == null) throw new BuildException("dragLater cannot be null!");
        this.dragLater = dragLater;
        return this;
    }

    public InventoryGuiBuilder onClose(Consumer<InventoryGui> close) {
        if (close == null) throw new BuildException("close cannot be null!");
        this.close = close;
        return this;
    }

    public InventoryGuiBuilder withDefaultCancel(Function<InventoryGui, Boolean> defaultCancel) {
        if (defaultCancel == null) throw new BuildException("defaultCancel cannot be null!");
        this.defaultCancel = defaultCancel;
        return this;
    }

    public InventoryGuiBuilder onConstruct(Consumer<InventoryGui> customConstructorCallback) {
        if (customConstructorCallback == null) throw new BuildException("customConstructorCallback cannot be null!");
        this.customConstructorCallback = customConstructorCallback;
        return this;
    }

    public InventoryGui build() {
        if (!universal) {
            if (p == null) throw new BuildException("Player cannot be null!");
        }
        if (fromXml) {
            try {
                if (guiFile == null && guiFilePath == null)
                    throw new BuildException("XML file and path cannot be null!");
                if (guiFile != null && guiFilePath != null)
                    throw new BuildException("XML file and path cannot be set at the same time!");
                if (guiFile != null) {
                    if (universal) {
                        return new FunctionalGui(guiFile, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                    } else {
                        return new FunctionalGui(guiFile, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                    }
                }
                if (universal) {
                    return new FunctionalGui(guiFilePath, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                } else {
                    return new FunctionalGui(guiFilePath, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
                }
            } catch (Exception e) {
                throw new BuildException("Failed to build GUI from XML file!", e);
            }
        }
        if (title == null) throw new BuildException("Title cannot be null!");
        if (type == null) {
            if (size < 1 || size > 54) throw new BuildException("Size must be between 1 and 54!");
            if (size % 9 != 0) throw new BuildException("Size must be a multiple of 9!");
            if (universal) {
                return new FunctionalGui(title, size, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
            } else {
                return new FunctionalGui(title, size, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
            }
        }
        if (universal) {
            return new FunctionalGui(title, type, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
        } else {
            return new FunctionalGui(title, type, p, fill, handle, handleLater, drag, dragLater, close, defaultCancel, customConstructorCallback);
        }
    }

}
