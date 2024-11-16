package de.neo.jagil.handler;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandConfirmationHandler implements ConfirmationHandler {

    private final String confirmCommand;
    private final String cancelCommand;

    public CommandConfirmationHandler(String confirmCommand, String cancelCommand) {
        this.confirmCommand = confirmCommand;
        this.cancelCommand = cancelCommand;
    }

    @Override
    public void handleConfirm(OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return;
        }

        player.performCommand(this.confirmCommand);
    }

    @Override
    public void handleCancel(OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return;
        }

        player.performCommand(this.cancelCommand);
    }
}
