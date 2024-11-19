package de.neo8.jagil.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The ItemBuilder is an alternative to the {@link ItemTool} class.
 * I would recommend using it for more complex cases.
 */
public class ItemBuilder {

    @Setter
    private Material material;

    @Setter
    private Component name;

    @Setter
    private int amount;

    @Setter
    private int durability;

    @Setter
    private PlayerProfile skullProfile;

    @Setter
    private int customModelData;

    private final List<Component> lore;
    private final List<Pair<Enchantment, Integer>> enchantments;

    /**
     * Create a new ItemBuilder from the values of a {@link InventoryGuiTypes.GuiItem}.
     * Be aware that this operation will eventually discard some information.
     *
     * @param guiItem item to get the values from
     */
    public ItemBuilder(InventoryGuiTypes.GuiItem guiItem) {
        this.material = guiItem.material;
        this.name = guiItem.name;
        this.amount = guiItem.amount;
        this.durability = -1;
        this.lore = guiItem.lore;
        this.enchantments = guiItem.enchantments
                .stream()
                .map(ench -> new Pair<>(ench.enchantment, ench.level))
                .collect(Collectors.toList());

        if (guiItem.texture != null) {
            this.skullProfile = Bukkit.createProfile(UUID.randomUUID());
            this.skullProfile.setProperty(new ProfileProperty("textures", guiItem.texture));
        }

        this.customModelData = guiItem.customModelData;
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     */
    public ItemBuilder(Material material) {
        this(material, Component.empty());
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     * @param name     the name of the item
     */
    public ItemBuilder(Material material, Component name) {
        this(material, name, 1);
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     * @param name     the name of the item
     * @param amount   the amount of the item
     */
    public ItemBuilder(Material material, Component name, int amount) {
        this(material, name, amount, -1);
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material   the material of the item
     * @param name       the name of the item
     * @param amount     the amount of the item
     * @param durability the durability of the item
     *                   (-1 = ignore, -2 = unbreakable)
     */
    public ItemBuilder(Material material, Component name, int amount, int durability) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.durability = durability;
        this.lore = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.skullProfile = null;
    }

    public ItemBuilder withLore(Component... lore) {
        this.lore.clear();
        this.lore.addAll(List.of(lore));
        return this;
    }

    public ItemBuilder addLore(Component... lore) {
        this.lore.addAll(List.of(lore));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.add(new Pair<>(enchantment, level));
        return this;
    }

    public ItemBuilder setBase64Head(String texture) {
        if (this.skullProfile == null) {
            this.skullProfile = Bukkit.createProfile(UUID.randomUUID());
        }

        this.skullProfile.setProperty(new ProfileProperty("textures", texture));
        return this;
    }

    public ItemBuilder setSkullOwner(OfflinePlayer player) {
        if (this.skullProfile == null) {
            this.skullProfile = Bukkit.createProfile(UUID.randomUUID());
        }

        this.skullProfile.setTextures(player.getPlayerProfile().getTextures());
        return this;
    }

    public ItemBuilder withCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(material, amount);
        enchantments.forEach(pair -> is.addUnsafeEnchantment(pair.getKey(), pair.getValue()));
        ItemMeta meta = is.getItemMeta();

        if (durability == -2) {
            meta.setUnbreakable(true);
        } else if (durability != -1) {
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() - durability);
            }
        }

        if (!this.name.equals(Component.empty())) {
            meta.displayName(this.name);
        }

        if (!lore.isEmpty()) {
            meta.lore(this.lore);
        }

        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setPlayerProfile(this.skullProfile);
        }

        if (this.customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }

        is.setItemMeta(meta);
        return is;
    }

}
