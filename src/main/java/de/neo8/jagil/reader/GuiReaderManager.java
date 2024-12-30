package de.neo8.jagil.reader;

import de.neo8.jagil.annotation.UserInterfaceByFile;
import de.neo8.jagil.exception.JAGILException;
import de.neo8.jagil.gui.UserInterfaceContextHolder;
import de.neo8.jagil.gui.inventory.InventoryGui;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GuiReaderManager {

    private final List<GuiReaderProvider<?>> readers;
    private static GuiReaderManager instance;

    private GuiReaderManager() {
        this.readers = new ArrayList<>();
    }

    public void register(GuiReaderProvider<?> readerProvider) {
        readers.add(readerProvider);
    }

    public GuiReader<?> getReader(Path filePath, @Nullable TagResolver tagContext) {
        String fileContent;
        try {
            fileContent = Files.readString(filePath);
        } catch (IOException e) {
            throw new JAGILException("Could not read file " + filePath.toAbsolutePath(), e);
        }

        final String finalFileContent = fileContent;

        AtomicReference<GuiReader<?>> supportedReader = new AtomicReference<>();
        this.readers
                .stream()
                .filter(x -> x.supportsFile(filePath, finalFileContent))
                .findFirst()
                .ifPresentOrElse(x -> supportedReader.set(x.getReader(tagContext)), () -> {
                    throw new JAGILException("No reader for file " + filePath.toAbsolutePath() + " registered!");
                });

        return supportedReader.get();
    }

    /**
     * Loads a full {@link InventoryGui} from a file
     *
     * @param file the file to load from
     * @return the {@link InventoryGuiTypes.DataGui}
     */
    public InventoryGuiTypes.DataGui readFile(Path file, @Nullable TagResolver tagContext) throws IOException {
        String content = Files.readString(file);
        GuiReader<?> reader = GuiReaderManager.getInstance().getReader(file, tagContext);
        return reader.read(content);
    }

    /**
     * Loads a complete {@link InventoryGui} from a file.
     * The filepath is read from the {@link de.neo8.jagil.annotation.UserInterfaceByFile} annotation.
     *
     * @param gui class with the {@link de.neo8.jagil.annotation.UserInterfaceByFile} annotation
     * @return {@link InventoryGuiTypes.DataGui}
     */
    public <T> InventoryGuiTypes.DataGui readGui(T gui) throws IOException {
        if (!(gui instanceof UserInterfaceContextHolder contextHolder)) {
            return this.readGui(gui, null);
        }

        return this.readGui(gui, contextHolder.getTagContext());
    }

    /**
     * Loads a complete {@link InventoryGui} from a file.
     * The filepath is read from the {@link de.neo8.jagil.annotation.UserInterfaceByFile} annotation.
     *
     * @param gui class with the {@link de.neo8.jagil.annotation.UserInterfaceByFile} annotation
     * @param tagContext give context for mini messages encoded in the ui file
     * @return {@link InventoryGuiTypes.DataGui}
     */
    public <T> InventoryGuiTypes.DataGui readGui(T gui, @Nullable TagResolver tagContext) throws IOException {
        UserInterfaceByFile uiFileAnnotation = gui.getClass().getAnnotation(UserInterfaceByFile.class);
        if (uiFileAnnotation == null) {
            throw new JAGILException("No @UserInterfaceByFile annotation found on " + gui.getClass().getName());
        }

        String uiFile = uiFileAnnotation.value();
        if (uiFile.isEmpty()) {
            throw new JAGILException("No @UserInterfaceByFile annotation found on " + gui.getClass().getName());
        }

        if (!(gui instanceof CustomFileProvider customFileProvider)) {
            Path uiFilePath = Path.of(uiFile);
            return this.readFile(uiFilePath, tagContext);
        }

        Path uiFilePath;
        try {
            uiFilePath = customFileProvider.convertToPath(uiFile);
            return this.readFile(uiFilePath, tagContext);
        } catch (Exception e) {
            // only throw the exception if the thrown exception was not a UnsupportedOperationException
            if (!(e instanceof UnsupportedOperationException)) {
                throw new JAGILException("Could not read file " + uiFile, e);
            }
        }

        GuiReader<?> reader = GuiReaderManager.getInstance().getReader(Path.of(URI.create(uiFile)), tagContext);

        String fileContent;
        try {
            fileContent = customFileProvider.readFile(uiFile);
        } catch (IOException e) {
            throw new JAGILException("Could not read file " + uiFile, e);
        } catch (UnsupportedOperationException e) {
            throw new JAGILException("readFile() is not supported by the CustomFileProvider. Is your implementation fine?");
        }

        return reader.read(fileContent);
    }

    public static GuiReaderManager getInstance() {
        if (instance == null) {
            instance = new GuiReaderManager();
        }
        return instance;
    }

}
