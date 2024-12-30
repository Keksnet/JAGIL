package de.neo8.jagil.annotation;

import de.neo8.jagil.reader.CustomFileProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserInterfaceByFile {

    /**
     * This has to be at least a valid uri.
     * Custom file providers can be implemented by implementing {@link CustomFileProvider}.
     *
     * @return path to the file.
     */
    @NotNull
    String value();

}
