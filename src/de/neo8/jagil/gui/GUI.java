package de.neo8.jagil.gui;

import de.neo8.jagil.JAGIL;
import de.neo8.jagil.annotation.Internal;
import de.neo8.jagil.annotation.OptionalImplementation;
import de.neo8.jagil.annotation.UnstableFeature;
import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.UIRenderPlainProvider;
import de.neo8.jagil.ui.UISystem;
import de.neo8.jagil.ui.impl.GuiUISystem;
import de.neo8.jagil.ui.impl.UIAction;
import de.neo8.jagil.util.InventoryPosition;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Represents a {@link GUI}.
 * Extend this class to create your own GUI with {@link JAGIL}.
 *
 * @version 4.0
 * @author Neo8
 */
public class GUI implements InventoryHolder {

	@Setter
	@Getter
	private Component name;

	@Getter
	private int size;

	@Getter
	private Inventory inventory;

	@Getter
	private GuiTypes.DataGui guiData;

	@Setter
	@Getter
	private long interactionCooldown;

	private InventoryType type;
	private OfflinePlayer offlinePlayer;
	private UISystem uiSystem;
	protected HashMap<String, Integer> itemIds;
	public int animationTaskId;



	private long lastInteraction;

	{
		interactionCooldown = 50;
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to load a GUI from the {@link GuiTypes.DataGui} class.
	 * Use this constructor when you like to create a universal {@link GUI}.
	 *
	 * @param gui the {@link GuiTypes.DataGui} class to load the GUI from.
	 */
	public GUI(GuiTypes.DataGui gui) {
		guiData = gui;
		name = gui.name;
		size = gui.size;
		itemIds = new HashMap<>();
		for(GuiTypes.GuiItem item : gui.items.values()) {
			itemIds.put(item.id, item.slot);
		}
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to load a GUI from the {@link GuiTypes.DataGui} class.
	 * Use this constructor when you like to create a non-universal {@link GUI}.
	 *
	 * @param gui the {@link GuiTypes.DataGui} class to load the GUI from.
	 */
	public GUI(GuiTypes.DataGui gui, OfflinePlayer offlinePlayer) {
		guiData = gui;
		name = gui.name;
		size = gui.size;
		this.offlinePlayer = offlinePlayer;
		itemIds = new HashMap<>();
		for(GuiTypes.GuiItem item : gui.items.values()) {
			itemIds.put(item.id, item.slot);
		}
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a universal {@link GUI} with a specific size.
	 *
	 * @param name name of the {@link Inventory}
	 * @param size size of the {@link Inventory}
	 */
	public GUI(Component name, int size) {
		this(name, size, null);
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a non-universal {@link GUI} with a specific size.
	 *
	 * @param name name of the {@link Inventory}
	 * @param size size of the {@link Inventory}
	 * @param offlinePlayer the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
	 */
	public GUI(Component name, int size, OfflinePlayer offlinePlayer) {
		this.name = name;
		this.size = size;
		this.offlinePlayer = offlinePlayer;
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a universal {@link GUI} with a specific {@link InventoryType}.
	 *
	 * @param name name of the {@link Inventory}
	 * @param type {@link InventoryType} of the {@link Inventory}
	 */
	@UnstableFeature
	public GUI(Component name, InventoryType type) {
		this(name, type, null);
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a non-universal {@link GUI} with a specific size.
	 *
	 * @param name name of the {@link Inventory}
	 * @param type {@link InventoryType} of the {@link Inventory}
	 * @param offlinePlayer the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
	 */
	@UnstableFeature
	public GUI(Component name, InventoryType type, OfflinePlayer offlinePlayer) {
		this.name = name;
		this.type = type;
		this.offlinePlayer = offlinePlayer;
	}
	
	public final UUID getPlayerUUID() {
		return this.offlinePlayer.getUniqueId();
	}

	public final Player getPlayer() {
		return this.offlinePlayer.getPlayer();
	}

	public UISystem getUiSystem() {
		if (this.uiSystem == null) {
			this.uiSystem = new GuiUISystem(this.size);
		}

		return uiSystem;
	}

	/**
	 * Closes the {@link Inventory} of this {@link GUI} save.
	 */
	public final void closeInventory() {
		Bukkit.getScheduler().runTask(JAGIL.getLoaderPlugin(), () -> getPlayer().closeInventory());
	}

	private void updateInternal() {
		if(this.inventory == null) {
			if(this.size != 0) {
				this.inventory = Bukkit.createInventory(this, this.size, this.name);
			}else {
				this.inventory = Bukkit.createInventory(this, this.type, this.name);
			}
			fillInternal();
		}else {
			fillInternal();
			getPlayer().updateInventory();
		}
	}

	/**
	 * This method is called to create an Inventory.
	 * This is called by {@link GUI#show()} automatically.
	 */
	@Internal
	protected final void update() {
		if(offlinePlayer == null) throw new RuntimeException("This method should not be called on universal GUIs");
		updateInternal();
	}

	/**
	 * This method creates a new {@link Inventory}.
	 * The name can be updated this way.
	 */
	public final void forceUpdate() {
		this.inventory = null;
		updateInternal();
	}

	/**
	 * Call this method to open the inventory of a non-universal {@link GUI}.
	 *
	 * @throws RuntimeException if the internal {@link Player} is null or this is used on a universal {@link GUI}
	 * @return instance for chaining
	 */
	public final GUI show() {
		update();
		if(this.offlinePlayer == null) throw new RuntimeException("Please use show(OfflinePlayer) for universal GUIs");
		if(!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(JAGIL.getLoaderPlugin(), () -> getPlayer().openInventory(this.inventory));
		}else {
			getPlayer().openInventory(this.inventory);
		}
		getPlayer().updateInventory();

		if(this.guiData.animationMod == 0) {
			return this;
		}

		AtomicInteger ticks = new AtomicInteger(0);
		AtomicInteger lastItem = new AtomicInteger(0);

		if(this.animationTaskId != -1) Bukkit.getScheduler().cancelTask(this.animationTaskId);

		this.animationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(JAGIL.getLoaderPlugin(), () -> {
			if(this.inventory == null) return;
			animate(ticks.getAndIncrement(), lastItem);
		}, 0L, 1L);
		return this;
	}

	/**
	 * Call this method to open the {@link Inventory} of a universal {@link GUI} for a specific {@link Player}.
	 *
	 * @param player player that should see the {@link Inventory}
	 * @return instance for chaining
	 */
	public final GUI show(OfflinePlayer player) {
		if (player != null) {
			Logger.getLogger("JAGIL")
					.warning("Using show(OfflinePlayer) for non-universal GUIs is dangerous. Please try to avoid it.");
		}

		this.offlinePlayer = player;
		this.updateInternal();
		this.show();
		this.offlinePlayer = null;
		return this;
	}

	protected final void fillInternal() {
		if (this.guiData != null) {
			this.guiData.ui
					.values()
					.stream()
					.filter(it -> !getUiSystem().hasComponent(it.getId()))
					.forEach(getUiSystem()::addComponent);
		}

		getUiSystem().render();
		GuiTypes.DataGui data = ((UIRenderPlainProvider<GuiTypes.DataGui>) getUiSystem().getRenderProvider()).getRenderPlain();

		if (this.guiData != null) {
			data.name = this.guiData.name;
			data.size = this.guiData.size;
			data.animationMod = this.guiData.animationMod;
			data.merge(this.guiData);
		} else {
			data.name = getName();
			data.size = getSize();
			data.animationMod = 0;
		}
		this.guiData = data;

		for(GuiTypes.GuiItem guiItem : this.guiData.items.values()) {
			if(guiItem.slot < 0) continue;
			ItemStack is = guiItem.toItem();
			this.inventory.setItem(guiItem.slot, is);
		}

		fill();
	}

	/**
	 * Fills this {@link GUI}
	 */
	public void fill() {}

	/**
	 * This method is called once a tick to animate the {@link GUI}.
	 *
	 * @param tick the current tick after the {@link GUI} was opened
	 */
	public void animate(long tick, AtomicInteger atomicLastItem) {
		if (this.guiData == null) return;
		if (this.guiData.animationMod == 0) return;
		if (tick % this.guiData.animationMod != 0) return;

		int lastItem = atomicLastItem.getAndIncrement();
		for(GuiTypes.GuiItem guiItem : this.guiData.items.values()) {
			if(guiItem == null) continue;
			if(guiItem.slot < 0) continue;
			if(guiItem.animationFrames.isEmpty()) continue;
			GuiTypes.GuiAnimationFrame frame = guiItem.animationFrames.get((lastItem + 1) % guiItem.animationFrames.size());
			frame.animate(tick, this);
		}

		getPlayer().updateInventory();
	}

	@Internal
	public final boolean handleInternal(InventoryClickEvent e) {
		if(System.currentTimeMillis() - this.lastInteraction <= this.interactionCooldown) {
			handleBlocked(e);
			return isCancelledByDefault();
		}

		this.lastInteraction = System.currentTimeMillis();
		Point p = InventoryPosition.fromSlot(e.getSlot()).toPoint();
		Clickable component = getUiSystem().getClickedComponent(p);
		if (component != null) {
			UIAction<GuiTypes.DataGui> click = new UIAction<>(e.getWhoClicked(), GuiTypes.DataGui.class, p, e.getClick());
			component.click(click);
		}

		return handle(e);
	}

	/**
	 * Called on an {@link InventoryClickEvent} in this {@link Inventory}.
	 * If you override this method you should not call super.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 * @return if the event should be cancelled or not.
	 */
	public boolean handle(InventoryClickEvent e) {
		return isCancelledByDefault();
	}

	/**
	 * Like {@link GUI#handle(InventoryClickEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleLast(InventoryClickEvent e) {}

	/**
	 * Called when the cooldown is not yet over but the {@link InventoryClickEvent} is fired.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleBlocked(InventoryClickEvent e) {}

	/**
	 * Called on an {@link InventoryDragEvent} in this {@link Inventory}.
	 *
	 * @param e the fired {@link InventoryDragEvent}
	 * @return if the event should be cancelled or not.
	 */
	@OptionalImplementation
	public boolean handleDrag(InventoryDragEvent e) { return isCancelledByDefault(); }

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleDragLast(InventoryDragEvent e) {}

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleClose(InventoryCloseEvent e) {}

	/**
	 * Returns if the event should be cancelled by default.
	 *
	 * @return the default cancel-value
	 */
	@OptionalImplementation
	public boolean isCancelledByDefault() { return true; }

	@Internal
	public final String getIdentifier() {
		return this.name + "-" + (this.offlinePlayer != null ? this.offlinePlayer.getUniqueId() : "universal");
	}
}