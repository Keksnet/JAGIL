package de.neo8.jagil.gui;

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

import java.util.*;
import java.util.function.BiConsumer;

public class GuiTypes {

    public final static BiConsumer<Long, Pair<GUI, GuiAnimationFrame>> DEFAULT_ANIMATION = (tick, pair) -> {
        GUI gui = pair.getKey();
        GuiAnimationFrame frame = pair.getValue();
        DataGui guiData = gui.getGuiData();
        ItemStack is = guiData.getItem(frame.itemId);
        if(is == null) {
            JAGIL.getLogger().warning("[JAGIL] GUI " + gui.getName() + ": item " + frame.itemId + " not found!");
            return;
        }
        Inventory inv = gui.getInventory();
        int slot = frame.position.toSlot();
        if(frame.previousFrame != null && frame.shouldCleanUp) inv.clear(frame.previousFrame.position.toSlot());
        inv.setItem(slot, is);
    };

    public enum MessageFormat {
        MINI_MESSAGE,
        LEGACY,
        PLAIN,
        JSON;
    }

    public static class DataGui {

        public long fileVersion;
        public MessageFormat messageFormat;
        public Component name;
        public int size;
        public long animationMod;
        public HashMap<Integer, GuiItem> items;
        public HashMap<String, UIComponent> ui;
        public HashMap<String, Integer> itemIdTable;

        public DataGui() {
            this.fileVersion = 4;
            this.messageFormat = MessageFormat.MINI_MESSAGE;
            this.name = Component.empty();
            this.size = 0;
            this.animationMod = 0;
            this.items = new HashMap<>();
            this.ui = new HashMap<>();
            this.itemIdTable = new HashMap<>();
        }

        /**
         * This will merge the given gui data into this gui data.
         * Any items that are already in this gui data will be overwritten.
         *
         * @param other the gui data to merge into this gui data
         */
        public void merge(DataGui other) {
            this.items.putAll(other.items);
            rebuildItemIdTable();
        }

        /**
         * Returns the slot of the {@link ItemStack} with the given id.
         *
         * @param itemId the id of the {@link ItemStack}
         * @return the slot of the {@link ItemStack} with the given id
         */
        public int getSlot(String itemId) {
            if(itemIdTable.isEmpty()) rebuildItemIdTable();
            return this.itemIdTable.getOrDefault(itemId, 999);
        }

        /**
         * Returns the {@link ItemStack} with the given id.
         *
         * @param itemId the id of the {@link ItemStack}
         * @return the {@link ItemStack} with the given id
         */
        public ItemStack getItem(String itemId) {
            int slot = getSlot(itemId);
            if(slot == 999) {
                return new ItemStack(Material.AIR);
            }
            return this.items.get(slot).toItem();
        }

        /**
         * Returns a copy of the {@link GuiItem} with the given id.
         *
         * @param itemId the id of the {@link GuiItem}
         * @return the {@link GuiItem} with the given id
         */
        public GuiItem getGuiItem(String itemId) {
            int slot = getSlot(itemId);
            if(slot == 999) {
                return null;
            }
            return new GuiItem(this.items.get(slot));
        }

        /**
         * Returns the itemId of the {@link ItemStack} with the given slot.
         *
         * @param slot the slot of the {@link ItemStack}
         * @return the itemId of the {@link ItemStack} with the given slot
         */
        public String getItemId(int slot) {
            if(itemIdTable.isEmpty()) rebuildItemIdTable();
            String itemId = "";
            for(Map.Entry<String, Integer> entry : this.itemIdTable.entrySet()) {
                if(entry.getValue() == slot) {
                    itemId = entry.getKey();
                    break;
                }
            }
            return itemId;
        }

        public void rebuildItemIdTable() {
            this.itemIdTable.clear();
            for(Map.Entry<Integer, GuiItem> entry : this.items.entrySet()) {
                this.itemIdTable.put(entry.getValue().id, entry.getKey());
            }
        }

        @Override
        public String toString() {
            return "DataGui{name=" + this.name + ", " +
                    "size=" + this.size + ", " +
                    "items=" + this.items + "," +
                    "itemIdTable=" + this.itemIdTable + "}";
        }
    }

    public static class GuiItem {

        public String id;
        public int slot;
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
            this.slot = item.slot;
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
        public BiConsumer<Long, Pair<GUI, GuiAnimationFrame>> animation;

        public GuiAnimationFrame() {
            this.itemId = "";
            this.position = InventoryPosition.DEFAULT;
            this.animation = DEFAULT_ANIMATION;
        }

        public void animate(long tick, GUI gui) {
            this.animation.accept(tick, new Pair<>(gui, this));
        }

    }

}
