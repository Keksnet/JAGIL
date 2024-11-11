package de.neo.jagil.gui.prebuild;

import de.neo.jagil.gui.GUI;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.neo.jagil.handler.ConfirmationHandler;
import de.neo.jagil.util.ItemTool;

public class ConfirmGUI extends GUI {
	
	private String yes_item;
	private String no_item;
	private String base64_yes;
	private String base64_no;
	private String cmd_yes;
	private String cmd_no;
	private ConfirmationHandler handler;
	
	public ConfirmGUI(Component question, String cmd_yes, OfflinePlayer p) {
		this(question, cmd_yes, null, p);
	}

	public ConfirmGUI(Component question, String cmd_yes, String cmd_no, OfflinePlayer p) {
		super(question, 9, p);
		this.cmd_yes = cmd_yes;
		this.cmd_no = cmd_no;
		this.base64_yes = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
		this.base64_no = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
		this.yes_item = "§aYes";
		this.no_item = "§cNo";
	}
	
	public ConfirmGUI(Component question, ConfirmationHandler handler, OfflinePlayer p) {
		super(question, 9, p);
		this.handler = handler;
		this.base64_yes = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
		this.base64_no = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
		this.yes_item = "§aYes";
		this.no_item = "§cNo";
	}
	
	public ConfirmGUI setBase64Yes(String base64) {
		this.base64_yes = base64;
		return this;
	}
	
	public ConfirmGUI setBase64No(String base64) {
		this.base64_no = base64;
		return this;
	}
	
	public ConfirmGUI setYesItemName(String name) {
		this.yes_item = name;
		return this;
	}
	
	public ConfirmGUI setNoItemName(String name) {
		this.no_item = name;
		return this;
	}

	@Override
	public void fill() {
		this.getInventory().setItem(2, ItemTool.createBase64Skull(this.yes_item, this.base64_yes));
		this.getInventory().setItem(6, ItemTool.createBase64Skull(this.no_item, this.base64_no));
	}

	@Override
	public boolean handle(InventoryClickEvent e) {
		int slot = e.getSlot();
		switch (slot) {
			case 2: {
				e.getWhoClicked().closeInventory();
				if (this.handler == null && this.cmd_yes != null) {
					((Player) e.getWhoClicked()).performCommand(this.cmd_yes);
				} else if (this.handler != null) {
					this.handler.handleYes((OfflinePlayer) e.getWhoClicked());
				}
			}
			case 6: {
				e.getWhoClicked().closeInventory();
				if (this.handler == null && this.cmd_no != null) {
					((Player) e.getWhoClicked()).performCommand(this.cmd_no);
				} else if (this.handler != null) {
					this.handler.handleNo((OfflinePlayer) e.getWhoClicked());
				}
			}
			default: {
			}
		}
		return true;
	}

}
