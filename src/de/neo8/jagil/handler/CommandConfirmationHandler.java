package de.neo8.jagil.handler;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CommandConfirmationHandler implements ConfirmationHandler {

    @Nullable
    private final String confirmCommand;

    @Nullable
    private final String cancelCommand;

    public CommandConfirmationHandler(@Nullable String confirmCommand, @Nullable String cancelCommand) {
        this.confirmCommand = confirmCommand;
        this.cancelCommand = cancelCommand;
    }

    @Override
    public void handleConfirm(OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return;
        }

        if (this.confirmCommand == null) {
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

        if (this.cancelCommand == null) {
            return;
        }

        player.performCommand(this.cancelCommand);
    }
}
