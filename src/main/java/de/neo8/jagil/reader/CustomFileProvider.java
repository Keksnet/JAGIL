package de.neo8.jagil.reader;

import de.neo8.jagil.annotation.UserInterfaceByFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public interface CustomFileProvider {

    /**
     * Converts {@link UserInterfaceByFile#value()} to a path.
     * Throws a {@link UnsupportedOperationException} if the path cannot be converted to a path.
     * If an exception is thrown {@link #readFile(String)} has to be implemented properly.
     *
     * @param path {@link UserInterfaceByFile#value()} of the class
     * @return String as path to read the file from
     * @throws UnsupportedOperationException thrown if the reader does not support converting to a path or implements {@link #readFile(String)}
     */
    @NotNull
    Path convertToPath(@NotNull String path) throws UnsupportedOperationException;

    /**
     * Read the whole file from the {@link UserInterfaceByFile#value()} input.
     * {@link #convertToPath(String)} has to throw a {@link UnsupportedOperationException} for this method to be considered.
     *
     * @param path Path provided to {@link UserInterfaceByFile#value()}
     * @return whole file content as string
     * @throws IOException thrown if any {@link IOException} occurred
     * @throws UnsupportedOperationException thrown if the reader does not support reading the file and has implemented {@link #convertToPath(String)}
     */
    @NotNull
    String readFile(@NotNull String path) throws IOException, UnsupportedOperationException;

}
