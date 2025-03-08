package de.neo8.jagil.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class CacheComponent {

    @NotNull
    private final String name;

    private final int version;

}
