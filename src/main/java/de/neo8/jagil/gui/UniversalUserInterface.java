package de.neo8.jagil.gui;

import de.neo8.jagil.ui.UISystem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface UniversalUserInterface {

    @NotNull
    UISystem getUiSystem();

    /**
     * Forcefully update the {@link UserInterface}
     */
    void forceUpdate();

    /**
     * Opens this {@link UserInterface} for the {@link Player} provided.
     * Usage of this method on any type that inherits from {@link UserInterface} is strongly discouraged.
     *
     * @param viewingPlayer player to show the inventory to
     *
     * @return instance for chaining
     */
    UserInterface show(Player viewingPlayer);

    /**
     * This method should contain code to render the interface of this {@link UserInterface}.
     * The {@link UserInterface} should not be shown until {@link #show(Player)} is called.
     */
    void render();

}
