package de.neo8.jagil.gui;

import de.neo8.jagil.ui.UISystem;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface UserInterface {

    UUID getPlayerUUID();

    Player getPlayer();

    UISystem getUiSystem();

    /**
     * Forcefully update the {@link UserInterface}
     */
    void forceUpdate();

    /**
     * Opens this {@link UserInterface}
     *
     * @return instance for chaining
     */
    UserInterface show();

    /**
     * This method should contain code to render the interface of this {@link UserInterface}.
     * The {@link UserInterface} should not be shown until {@link #show()} is called.
     */
    void render();

}
