package de.neo8.jagil.gui;

import de.neo8.jagil.JAGIL;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UserInterface extends UniversalUserInterface {

    @NotNull
    UUID getPlayerUUID();

    @NotNull
    Player getPlayer();

    /**
     * Opens this {@link UserInterface}
     *
     * @return instance for chaining
     */
    @Contract("-> this")
    UserInterface show();

    @Override
    default UserInterface show(Player ignore) {
        JAGIL.getLogger().warning("UserInterface should not be opened using show(Player). Use show() instead!");
        return show();
    }
}
