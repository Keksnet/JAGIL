package de.neo8.jagil.handler;

import de.neo8.jagil.gui.inventory.prebuild.ConfirmGui;
import org.bukkit.OfflinePlayer;

public interface ConfirmationHandler {

    /**
     * Called when the user confirms an action in a {@link ConfirmGui}.
     *
     * @param player clicking user
     */
    void handleConfirm(OfflinePlayer player);

    /**
     * Called when the user cancels an action no in a {@link ConfirmGui}.
     *
     * @param player clicking user
     */
    void handleCancel(OfflinePlayer player);
}
