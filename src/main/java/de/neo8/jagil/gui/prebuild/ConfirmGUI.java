package de.neo8.jagil.gui.prebuild;

import de.neo8.jagil.gui.GUI;
import de.neo8.jagil.handler.CommandConfirmationHandler;
import de.neo8.jagil.handler.ConfirmationHandler;
import de.neo8.jagil.util.ItemTool;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConfirmGUI extends GUI {

    private final static String DEFAULT_CONFIRM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
    private final static String DEFAULT_CANCEL_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";


    @Setter
    private Component confirmItemText;

    @Setter
    private Component declineItemText;

    @Setter
    private String confirmTexture;

    @Setter
    private String cancelTexture;

    private final ConfirmationHandler handler;

    public ConfirmGUI(Component question, String acceptCommand, OfflinePlayer player) {
        this(question, acceptCommand, null, player);
    }

    public ConfirmGUI(Component question, String acceptCommand, String declineCommand, OfflinePlayer player) {
        super(question, 9, player);
        this.handler = new CommandConfirmationHandler(acceptCommand, declineCommand);
        this.confirmTexture = DEFAULT_CONFIRM_TEXTURE;
        this.cancelTexture = DEFAULT_CANCEL_TEXTURE;
        this.confirmItemText = LegacyComponentSerializer.legacySection().deserialize("§aYes");
        this.declineItemText = LegacyComponentSerializer.legacySection().deserialize("§cNo");
    }

    public ConfirmGUI(Component question, ConfirmationHandler handler, OfflinePlayer player) {
        super(question, 9, player);
        this.handler = handler;
        this.confirmTexture = DEFAULT_CONFIRM_TEXTURE;
        this.cancelTexture = DEFAULT_CANCEL_TEXTURE;
        this.confirmItemText = LegacyComponentSerializer.legacySection().deserialize("§aYes");
        this.declineItemText = LegacyComponentSerializer.legacySection().deserialize("§cNo");
    }

    @Override
    public void fill() {
        this.getInventory().setItem(2, ItemTool.createBase64Skull(this.confirmItemText, this.confirmTexture));
        this.getInventory().setItem(6, ItemTool.createBase64Skull(this.declineItemText, this.cancelTexture));
    }

    @Override
    public boolean handle(InventoryClickEvent e) {
        // Cast to player can be assumed
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        switch (slot) {
            case 2 -> {
                player.closeInventory();
                this.handler.handleConfirm(player);
            }

            case 6 -> {
                player.closeInventory();
                this.handler.handleCancel(player);
            }

            default -> {
            }
        }
        return true;
    }

}
