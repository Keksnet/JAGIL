package de.neo8.jagil.reader;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface GuiReaderProvider<T extends GuiReader<?>> {

    boolean supportsFile(@NotNull Path filePath, @NotNull String content);

    @NotNull
    T getReader(@Nullable TagResolver tagContext);

}
