package de.neo8.jagil.gui.inventory;

import de.neo8.jagil.JAGIL;
import de.neo8.jagil.annotation.Internal;
import de.neo8.jagil.annotation.OptionalImplementation;
import de.neo8.jagil.annotation.UserInterfaceByFile;
import de.neo8.jagil.exception.JAGILException;
import de.neo8.jagil.gui.UserInterfaceContextHolder;
import de.neo8.jagil.reader.GuiReaderManager;
import de.neo8.jagil.ui.UISystem;
import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.impl.InventoryUiSystem;
import de.neo8.jagil.ui.impl.UIAction;
import de.neo8.jagil.util.InventoryPosition;
import de.neo8.jagil.util.Pair;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a {@link InventoryGui}.
 * Extend this class to create your own GUI with {@link JAGIL}.
 *
 * @author Neo8
 * @version 4.0
 */
@EqualsAndHashCode
public class InventoryGui implements InventoryUserInterface, AnimatedInventory {

    @Setter
    @Getter
    private Component name;

    @Getter
    private int size;

    @Getter
    private InventoryType type;

    @Getter
    private Inventory inventory;

    @Getter
    private InventoryGuiTypes.DataGui guiData;

    @Setter
    @Getter
    private long interactionCooldown;

    @Getter
    private Player player;

    public int animationTaskId;
    protected HashMap<String, Integer> itemIds;
    private UISystem<InventoryGuiTypes.DataGui> uiSystem;
    private long lastInteraction;

    {
        this.interactionCooldown = 50;
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor if you have annotated your class with {@link UserInterfaceByFile}.
     * Use this constructor when you like to create a non-universal {@link InventoryGui}.
     *
     * @param player {@link Player} that should see this {@link Inventory}
     */
    public InventoryGui(Player player) {
        if (!getClass().isAnnotationPresent(UserInterfaceByFile.class)) {
            throw new JAGILException("no-args-contructor is only permitted if this class is annotated with @UserInterByFile");
        }

        this.player = player;

        TagResolver tagContext = TagResolver.empty();
        if (this instanceof UserInterfaceContextHolder ctxHolder) {
            tagContext = ctxHolder.getTagContext();
        }

        InventoryGuiTypes.DataGui gui;
        try {
            gui = GuiReaderManager.getInstance().readGui(this, tagContext);
        } catch (IOException e) {
            throw new JAGILException("Could no build gui from file", e);
        }

        this.guiData = gui;
        this.name = gui.name;
        this.size = gui.size;
        this.itemIds = new HashMap<>();
        gui.items.values().forEach(item -> this.itemIds.put(item.id, item.slot));
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor when you like to load a GUI from the {@link InventoryGuiTypes.DataGui} class.
     * Use this constructor when you like to create a universal {@link InventoryGui}.
     *
     * @param gui the {@link InventoryGuiTypes.DataGui} class to load the GUI from.
     */
    public InventoryGui(InventoryGuiTypes.DataGui gui) {
        this.guiData = gui;
        this.name = gui.name;
        this.size = gui.size;
        this.itemIds = new HashMap<>();
        gui.items.values().forEach(item -> this.itemIds.put(item.id, item.slot));
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor when you like to load a GUI from the {@link InventoryGuiTypes.DataGui} class.
     * Use this constructor when you like to create a non-universal {@link InventoryGui}.
     *
     * @param gui the {@link InventoryGuiTypes.DataGui} class to load the GUI from.
     * @param player {@link Player} that should see this {@link Inventory}
     */
    public InventoryGui(InventoryGuiTypes.DataGui gui, Player player) {
        this.guiData = gui;
        this.name = gui.name;
        this.size = gui.size;
        this.player = player;
        this.itemIds = new HashMap<>();
        gui.items.values().forEach(item -> this.itemIds.put(item.id, item.slot));
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor when you like to create a universal {@link InventoryGui} with a specific size.
     *
     * @param name name of the {@link Inventory}
     * @param size size of the {@link Inventory}
     */
    public InventoryGui(Component name, int size) {
        this(name, size, null);
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor when you like to create a non-universal {@link InventoryGui} with a specific size.
     *
     * @param name          name of the {@link Inventory}
     * @param size          size of the {@link Inventory}
     * @param player the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
     */
    public InventoryGui(Component name, int size, Player player) {
        this.name = name;
        this.size = size;
        this.player = player;
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor when you like to create a universal {@link InventoryGui} with a specific {@link InventoryType}.
     *
     * @param name name of the {@link Inventory}
     * @param type {@link InventoryType} of the {@link Inventory}
     */
    public InventoryGui(Component name, InventoryType type) {
        this(name, type, null);
    }

    /**
     * Creates a new instance of the {@link InventoryGui} class.
     * Use this constructor when you like to create a non-universal {@link InventoryGui} with a specific size.
     *
     * @param name          name of the {@link Inventory}
     * @param type          {@link InventoryType} of the {@link Inventory}
     * @param player the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
     */
    public InventoryGui(Component name, InventoryType type, Player player) {
        this.name = name;
        this.type = type;
        this.player = player;
    }

    @Override
    public final @NotNull UUID getPlayerUUID() {
        return this.player.getUniqueId();
    }

    @Override
    public @NotNull UISystem<InventoryGuiTypes.DataGui> getUiSystem() {
        if (this.uiSystem == null) {
            this.uiSystem = new InventoryUiSystem(this.size);
        }

        return this.uiSystem;
    }

    /**
     * Closes the {@link Inventory} of this {@link InventoryGui} save.
     */
    @Override
    public final void closePlayerInventory() {
        InventoryView inventoryView = this.getPlayer().getOpenInventory();
        InventoryHolder bottomInvHolder = inventoryView.getBottomInventory().getHolder();
        InventoryHolder topInvHolder = inventoryView.getTopInventory().getHolder();

        // only close the players inventory if it is this inventory
        if (!(bottomInvHolder instanceof InventoryGui bottomInv)) {
            return;
        }

        if (!(topInvHolder instanceof InventoryGui topInv)) {
            return;
        }

        if (!bottomInv.equals(this) && !topInv.equals(this)) {
            return;
        }

        Bukkit.getScheduler().runTask(JAGIL.getLoaderPlugin(), () -> this.getPlayer().closeInventory());
    }

    /**
     * This method is called to create an Inventory.
     * This is called by {@link InventoryGui#show()} automatically.
     */
    private void updateRenderedInventory() {
        if (this.inventory != null) {
            this.fillInternal();
            this.getPlayer().updateInventory();
            return;
        }

        if (this.size > 0 && (this.size % 9) == 0) {
            this.inventory = Bukkit.createInventory(this, this.size, this.name);
        } else if (this.type != null) {
            this.inventory = Bukkit.createInventory(this, this.type, this.name);
        }

        if (this.inventory == null) {
            throw new JAGILException("this.size > 0 or this.type != null must be true");
        }

        this.fillInternal();
        this.getPlayer().updateInventory();
    }

    /**
     * This method creates a new {@link Inventory}.
     * The name can be updated this way.
     */
    @Override
    public final void forceUpdate() {
        this.inventory = null;
        this.updateRenderedInventory();
    }

    private void initAnimation() {
        AtomicInteger ticks = new AtomicInteger(0);
        AtomicInteger lastItem = new AtomicInteger(0);

        if (this.animationTaskId != -1) Bukkit.getScheduler().cancelTask(this.animationTaskId);

        this.animationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(JAGIL.getLoaderPlugin(), () -> {
            if (this.inventory == null) return;
            this.animate(ticks.getAndIncrement(), lastItem);
        }, 0L, 1L);
    }

    /**
     * Call this method to open the inventory of a non-universal {@link InventoryGui}.
     *
     * @return instance for chaining
     * @throws RuntimeException if the internal {@link Player} is null or this is used on a universal {@link InventoryGui}
     */
    @Override
    public final InventoryGui show() {
        this.updateRenderedInventory();
        Bukkit.getScheduler().runTask(JAGIL.getLoaderPlugin(), () -> {
            this.getPlayer().openInventory(this.inventory);
            this.getPlayer().updateInventory();
        });

        if (this.guiData.animationTick != 0) {
            this.initAnimation();
        }

        return this;
    }

    /**
     * Call this method to open the {@link Inventory} of a universal {@link InventoryGui} for a specific {@link Player}.
     *
     * @param player player that should see the {@link Inventory}
     * @return instance for chaining
     */
    @Override
    public final InventoryGui show(Player player) {
        if (player != null) {
            JAGIL.getLogger()
                    .warning("Using show(OfflinePlayer) for non-universal GUIs is dangerous. Please try to avoid it.");
        }

        this.player = player;
        this.updateRenderedInventory();
        this.show();
        this.player = null;
        return this;
    }

    private void fillInternal() {
        if (this.guiData != null) {
            this.guiData.ui
                    .values()
                    .stream()
                    .filter(it -> !this.getUiSystem().hasComponent(it.getId()))
                    .forEach(getUiSystem()::addComponent);
        }

        this.getUiSystem().render();
        InventoryGuiTypes.DataGui data = this.getUiSystem().getRenderProvider().getRenderPane();

        if (this.guiData != null) {
            data.name = this.guiData.name;
            data.size = this.guiData.size;
            data.animationTick = this.guiData.animationTick;
            data.merge(this.guiData);
        } else {
            data.name = this.getName();
            data.size = this.getSize();
            data.animationTick = 0;
        }
        this.guiData = data;

        // fill has to be called before the processing of gui items to allow for mutation of the items
        this.fill();

        this.guiData.items.values().stream()
                .filter(guiItem -> guiItem.slot >= 0 && !guiItem.template)
                .filter(guiItem -> this.inventory.getItem(guiItem.slot) == null) // do not overwrite existing items
                .sorted()
                .map(guiItem -> new Pair<>(guiItem.slot, guiItem.toItem()))
                .forEach(pair -> this.inventory.setItem(pair.getKey(), pair.getValue()));
    }

    /**
     * Fills this {@link InventoryGui}
     */
    @Override
    public void fill() {
    }

    /**
     * This method is called once a tick to animate the {@link InventoryGui}.
     *
     * @param tick the current tick after the {@link InventoryGui} was opened
     */
    @Override
    public void animate(long tick, AtomicInteger atomicLastItem) {
        if (this.guiData == null) return;
        if (this.guiData.animationTick == 0) return;
        if (tick % this.guiData.animationTick != 0) return;

        int lastItem = atomicLastItem.getAndIncrement();
        for (InventoryGuiTypes.GuiItem guiItem : this.guiData.items.values()) {
            if (guiItem == null) continue;
            if (guiItem.slot < 0) continue;
            if (guiItem.animationFrames.isEmpty()) continue;
            InventoryGuiTypes.GuiAnimationFrame frame = guiItem.animationFrames.get((lastItem + 1) % guiItem.animationFrames.size());
            frame.animate(tick, this);
        }

        this.getPlayer().updateInventory();
    }

    @Override
    @Internal
    public final boolean handleInternal(InventoryClickEvent e) {
        if (System.currentTimeMillis() - this.lastInteraction <= this.interactionCooldown) {
            this.handleBlocked(e);
            return this.isCancelledByDefault();
        }

        this.lastInteraction = System.currentTimeMillis();
        Point p = InventoryPosition.fromSlot(e.getSlot()).toPoint();
        Clickable component = this.getUiSystem().getClickedComponent(p);
        if (component != null) {
            UIAction<InventoryGuiTypes.DataGui> click = new UIAction<>(e.getWhoClicked(), InventoryGuiTypes.DataGui.class, p, e.getClick());
            component.click(click);
        }

        return this.handle(e);
    }

    /**
     * Called on an {@link InventoryClickEvent} in this {@link Inventory}.
     * If you override this method you should not call super.
     *
     * @param e the fired {@link InventoryClickEvent}
     * @return if the event should be cancelled or not.
     */
    @Override
    public boolean handle(InventoryClickEvent e) {
        return this.isCancelledByDefault();
    }

    /**
     * Like {@link InventoryGui#handle(InventoryClickEvent)} but optional and one tick later.
     *
     * @param e the fired {@link InventoryClickEvent}
     */
    @Override
    @OptionalImplementation
    public void handleLast(InventoryClickEvent e) {
    }

    /**
     * Called when the cooldown is not yet over but the {@link InventoryClickEvent} is fired.
     *
     * @param e the fired {@link InventoryClickEvent}
     */
    @Override
    @OptionalImplementation
    public void handleBlocked(InventoryClickEvent e) {
    }

    /**
     * Called on an {@link InventoryDragEvent} in this {@link Inventory}.
     *
     * @param e the fired {@link InventoryDragEvent}
     * @return if the event should be cancelled or not.
     */
    @Override
    @OptionalImplementation
    public boolean handleDrag(InventoryDragEvent e) {
        return this.isCancelledByDefault();
    }

    /**
     * Like {@link InventoryGui#handleDrag(InventoryDragEvent)} but optional and one tick later.
     *
     * @param e the fired {@link InventoryClickEvent}
     */
    @Override
    @OptionalImplementation
    public void handleDragLast(InventoryDragEvent e) {
    }

    /**
     * Like {@link InventoryGui#handleDrag(InventoryDragEvent)} but optional and one tick later.
     *
     * @param e the fired {@link InventoryClickEvent}
     */
    @Override
    @OptionalImplementation
    public void handleClose(InventoryCloseEvent e) {
    }

    /**
     * Returns if the event should be cancelled by default.
     *
     * @return the default cancel-value
     */
    @Override
    @OptionalImplementation
    public boolean isCancelledByDefault() {
        return true;
    }

    /**
     * Terminates the animation task. This causes the animation to be stopped.
     */
    @Override
    public void cancelAnimationTask() {
        if (this.animationTaskId == -1) {
            Bukkit.getScheduler().cancelTask(this.animationTaskId);
        }
        this.animationTaskId = -1;
    }
}