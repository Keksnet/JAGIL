package de.neo.jagil.manager;

import de.neo.jagil.exception.JAGILException;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.reader.GuiReader;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class GuiReaderManager {

    private final HashMap<String, GuiReader<?>> readers;
    private static GuiReaderManager instance;

    private GuiReaderManager() {
        readers = new HashMap<>();
    }

    public void register(GuiReader<?> reader) {
        readers.put(reader.getFileType(), reader);
    }

    public GuiReader<?> getReader(String fileType) {
        if(!readers.containsKey(fileType)) {
            throw new JAGILException("No reader for file type " + fileType + " registered!");
        }
        return readers.get(fileType);
    }

    /**
     * Loads a full {@link GUI} from a file
     *
     * @param file the file to load from
     * @return the {@link GuiTypes.DataGui}
     */
    public GuiTypes.DataGui readFile(Path file) throws IOException {
        String[] fileName = file.toString().split("\\.");
        GuiReader<?> reader = GuiReaderManager.getInstance().getReader(fileName[fileName.length - 1].toLowerCase());
        String content = Files.readString(file);
        return reader.read(content);
    }

    public static GuiReaderManager getInstance() {
        if(instance == null) {
            instance = new GuiReaderManager();
        }
        return instance;
    }

}
