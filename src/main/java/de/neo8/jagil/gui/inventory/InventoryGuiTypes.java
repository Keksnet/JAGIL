package de.neo8.jagil.gui.inventory;

import com.google.gson.JsonElement;
import de.neo8.jagil.JAGIL;
import de.neo8.jagil.ui.components.UIComponent;
import de.neo8.jagil.util.InventoryPosition;
import de.neo8.jagil.util.ItemBuilder;
import de.neo8.jagil.util.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class InventoryGuiTypes {

    public final static BiConsumer<Long, Pair<InventoryGui, GuiAnimationFrame>> DEFAULT_ANIMATION = (tick, pair) -> {
        InventoryGui inventoryGui = pair.getKey();
        GuiAnimationFrame frame = pair.getValue();
        DataGui guiData = inventoryGui.getGuiData();
        ItemStack is = guiData.getItem(frame.itemId);
        Inventory inv = inventoryGui.getInventory();
        int slot = frame.position.toSlot();
        if (frame.previousFrame != null && frame.shouldCleanUp) inv.clear(frame.previousFrame.position.toSlot());
        inv.setItem(slot, is);
    };

    public enum MessageFormat {
        MINI_MESSAGE,
        LEGACY,
        PLAIN,
        JSON
    }

    public static class DataGui {
        public long fileVersion;
        public MessageFormat messageFormat;
        public List<String> features;
        public Component name;
        public int size;
        public long animationTick;
        public Map<String, GuiItem> items;
        public Map<String, UIComponent> ui;

        public DataGui() {
            this.fileVersion = 4;
            this.messageFormat = MessageFormat.MINI_MESSAGE;
            this.name = Component.empty();
            this.size = 0;
            this.animationTick = 0;
            this.items = new HashMap<>();
            this.ui = new HashMap<>();
        }

        /**
         * This will merge the given gui data into this gui data.
         * Any items that are already in this gui data will be overwritten.
         *
         * @param other the gui data to merge into this gui data
         */
        public void merge(DataGui other) {
            this.items.putAll(other.items);
        }

        /**
         * Returns the slot of the {@link ItemStack} with the given id.
         *
         * @param itemId the id of the {@link ItemStack}
         * @return the slot of the {@link ItemStack} with the given id
         */
        @Nullable
        public Integer getSlot(@NotNull String itemId) {
            return this.items.getOrDefault(itemId, null).slot;
        }

        /**
         * Returns the {@link ItemStack} with the given id.
         *
         * @param itemId the id of the {@link ItemStack}
         * @return the {@link ItemStack} with the given id
         */
        @NotNull
        public ItemStack getItem(@NotNull String itemId) {
            return this.items.get(itemId).toItem();
        }

        /**
         * Returns a copy of the {@link GuiItem} with the given id.
         *
         * @param itemId the id of the {@link GuiItem}
         * @return the {@link GuiItem} with the given id
         */
        @Nullable
        public GuiItem getGuiItem(@NotNull String itemId) {
            GuiItem items = getMutableGuiItem(itemId);
            if (items == null) {
                return null;
            }

            return new GuiItem(items);
        }

        /**
         * Returns the itemId of the {@link ItemStack} with the given slot.
         *
         * @param slot the slot of the {@link ItemStack}
         * @return the itemId of the {@link ItemStack} with the given slot
         */
        public String getItemId(int slot) {
            String itemId = "";
            List<Map.Entry<String, GuiItem>> itemStream = this.items.entrySet()
                    .stream()
                    .filter(x -> !x.getValue().template)
                    .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                    .toList();
            for (Map.Entry<String, GuiItem> entry : itemStream) {
                if (entry.getValue().slot == slot) {
                    itemId = entry.getKey();
                    break;
                }
            }

            return itemId;
        }

        /**
         * Returns a mutable {@link GuiItem} with the given id.
         *
         * @param itemId if of the returned {@link GuiItem}
         * @return mutable {@link GuiItem} with the given id
         */
        public GuiItem getMutableGuiItem(String itemId) {
            return this.items.get(itemId);
        }

        @Override
        public String toString() {
            return "DataGui{name=" + this.name + ", " +
                    "size=" + this.size + ", " +
                    "items=" + this.items + "}";
        }
    }

    public static class GuiItem implements Comparable<GuiItem> {

        public String id;
        public boolean generatedId;
        public boolean template;
        public int slot;
        public int layer;
        public Material material;
        public Component name;
        public int amount;
        public List<Component> lore;
        public HashSet<GuiEnchantment> enchantments;
        public int customModelData;
        public String texture;
        public HashMap<String, JsonElement> attributes;
        public ArrayList<GuiAnimationFrame> animationFrames;

        public GuiItem() {
            this.id = "";
            this.generatedId = false;
            this.template = false;
            this.slot = 0;
            this.material = Material.AIR;
            this.name = Component.empty();
            this.lore = new ArrayList<>();
            this.enchantments = new HashSet<>();
            this.texture = "";
            this.attributes = new HashMap<>();
            this.animationFrames = new ArrayList<>();
        }

        public GuiItem(GuiItem item) {
            this.id = item.id;
            this.template = item.template;
            this.generatedId = item.generatedId;
            this.slot = item.slot;
            this.layer = item.layer;
            this.material = item.material;
            this.name = item.name;
            this.amount = item.amount;
            this.lore = new ArrayList<>(item.lore);
            this.enchantments = new HashSet<>(item.enchantments);
            this.customModelData = item.customModelData;
            this.texture = item.texture;
            this.attributes = new HashMap<>(item.attributes);
            this.animationFrames = new ArrayList<>(item.animationFrames);
        }

        public void applyNameComponent(Component component) {
            this.name = component;
        }

        public void applyNewLoreComponent(Component component) {
            this.lore.add(component);
        }

        public void applyLoreComponent(int line, Component component) {
            this.lore.set(line, component);
        }

        public ItemStack toItem() {
            return new ItemBuilder(this).build();
        }

        @Override
        public String toString() {
            return "GuiItem{id=" + this.id + ", " +
                    "slot=" + this.slot + ", " +
                    "material=" + this.material + ", " +
                    "name=" + this.name + ", " +
                    "amount=" + this.amount + ", " +
                    "lore=" + this.lore + ", " +
                    "enchantments=" + this.enchantments + "," +
                    "texture=" + this.texture + "," +
                    "attributes=" + this.attributes + "," +
                    "animationFrames=" + this.animationFrames + "}";
        }

        @Override
        public int compareTo(@NotNull InventoryGuiTypes.GuiItem o) {
            return this.layer - o.layer;
        }
    }

    public static class GuiEnchantment {

        public GuiEnchantment() {
            this.enchantment = null;
            this.level = 0;
        }

        public Enchantment enchantment;
        public int level;

        @Override
        public String toString() {
            return "GuiEnchantment{enchantment=" + this.enchantment + ", " +
                    "level=" + this.level + "}";
        }
    }

    public static class GuiAnimationFrame {

        public String itemId;
        public InventoryPosition position;
        public boolean shouldCleanUp;
        public GuiAnimationFrame previousFrame;
        public BiConsumer<Long, Pair<InventoryGui, GuiAnimationFrame>> animation;

        public GuiAnimationFrame() {
            this.itemId = "";
            this.position = InventoryPosition.DEFAULT;
            this.animation = DEFAULT_ANIMATION;
        }

        public void animate(long tick, InventoryGui inventoryGui) {
            this.animation.accept(tick, new Pair<>(inventoryGui, this));
        }

    }

}
