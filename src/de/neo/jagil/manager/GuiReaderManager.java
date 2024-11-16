package de.neo.jagil.manager;

import de.neo.jagil.exception.JAGILException;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.reader.GuiReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GuiReaderManager {

    private final ArrayList<GuiReader<?>> readers;
    private static GuiReaderManager instance;

    private GuiReaderManager() {
        this.readers = new ArrayList<>();
    }

    public void register(GuiReader<?> reader) {
        readers.add(reader);
    }

    public GuiReader<?> getReader(Path filePath, String content) {
        for (GuiReader<?> reader : readers) {
            if (reader.supportsFile(filePath, content)) {
                return reader;
            }
        }

        throw new JAGILException("No reader for file " + filePath.toAbsolutePath() + " registered!");
    }

    /**
     * Loads a full {@link GUI} from a file
     *
     * @param file the file to load from
     * @return the {@link GuiTypes.DataGui}
     */
    public GuiTypes.DataGui readFile(Path file) throws IOException {
        String content = Files.readString(file);
        GuiReader<?> reader = GuiReaderManager.getInstance().getReader(file, content);
        return reader.read(content);
    }

    public static GuiReaderManager getInstance() {
        if (instance == null) {
            instance = new GuiReaderManager();
        }
        return instance;
    }

}
