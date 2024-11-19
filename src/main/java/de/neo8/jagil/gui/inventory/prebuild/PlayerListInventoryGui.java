package de.neo8.jagil.gui.inventory.prebuild;

import com.google.common.collect.ImmutableList;
import de.neo8.jagil.gui.inventory.InventoryGui;
import de.neo8.jagil.util.ItemTool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * A GUI for displaying a list of players.
 * Implementation by Nononitas from KeinSurvival. (https://github.com/Nononitas)
 * Edited by Neo8
 *
 * @author Nononitas
 * @version 3.3.5
 */
public class PlayerListInventoryGui extends InventoryGui {

    private int page = 0;

    protected String backHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
    protected Component backComponent = LegacyComponentSerializer.legacySection().deserialize("§cBack");
    protected Component playerNameFormat = LegacyComponentSerializer.legacySection().deserialize("§9%player%");
    protected String nextPageHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=";
    protected String prevPageHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=";
    protected Component nextPageComponent = LegacyComponentSerializer.legacySection().deserialize("§aNext Page");
    protected Component prevPageComponent = LegacyComponentSerializer.legacySection().deserialize("§aPrevious Page");

    public PlayerListInventoryGui(Player player, Component title) {
        super(title, 54, player);
    }

    @Override
    public void fill() {
        List<OfflinePlayer> playerList = getPlayerList();
        final int constant = this.page * 28;
        for (int i = 0, j = 0; i < 54; i++) {
            ItemStack item = ItemTool.createItem(Material.BLACK_STAINED_GLASS_PANE);
            if (i != 17 && i != 18 && i != 26 && i != 27 && i != 35 && i != 36) {
                if (i == 8) {
                    item = ItemTool.createBase64Skull(this.backComponent, this.backHead);
                } else if (i > 9 && i < 44) {
                    if (playerList.size() > j + constant) {
                        OfflinePlayer offlinePlayer = playerList.get(j + constant);
                        if (offlinePlayer == null || offlinePlayer.getName() == null) {
                            continue;
                        }

                        Component skullName = this.playerNameFormat.replaceText((builder) ->
                                builder.matchLiteral("%player%").replacement(offlinePlayer.getName()));
                        item = ItemTool.createSkull(skullName, offlinePlayer);
                        j++;
                    } else {
                        item = ItemTool.createItem(Material.GRAY_STAINED_GLASS_PANE);
                    }

                } else if (i == 47 && page > 0) {
                    item = ItemTool.createBase64Skull(nextPageComponent, nextPageHead);
                } else if (i == 51) {
                    if (constant + 28 < playerList.size()) {
                        item = ItemTool.createBase64Skull(prevPageComponent, prevPageHead);
                    }
                }
            }

            getInventory().setItem(i, item);
        }
    }

    @Override
    public boolean handle(InventoryClickEvent event) {
        int slot = event.getSlot();
        ItemStack clickedItem = getInventory().getItem(slot);
        if (slot > 9 && slot < 18) {
            if (clickedItem != null) {
                if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                    onPlayerClick(skullMeta.getOwningPlayer());
                }
            }
            return true;
        }
        if (slot == 8) {
            onBack();
        }
        if (slot == 47 && page > 0) {
            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                page--;
                fillInternal();
            }

        } else if (slot == 51) {
            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                page++;
                fillInternal();
            }
        }


        return true;
    }

    public List<OfflinePlayer> getPlayerList() {
        return ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    }


    public void onPlayerClick(OfflinePlayer offlinePlayer) {
    }

    public void onBack() {
    }


}