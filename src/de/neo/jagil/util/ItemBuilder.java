package de.neo.jagil.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.neo.jagil.gui.GuiTypes;
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

    private Material material;
    private String name;
    private int amount;
    private int durability;
    private List<String> lore;
    private List<Pair<Enchantment, Integer>> enchantments;
    private String texture;
    private OfflinePlayer skullOwner;
    private int customModelData;

    /**
     * Create a new ItemBuilder from the values of a {@link de.neo.jagil.gui.GuiTypes.GuiItem}.
     * Be aware that this operation will eventually discard some information.
     *
     * @param guiItem item to get the values from
     */
    public ItemBuilder(GuiTypes.GuiItem guiItem) {
        this.material = guiItem.material;
        this.name = guiItem.name;
        this.amount = guiItem.amount;
        this.durability = -1;
        this.lore = guiItem.lore;
        this.enchantments = guiItem.enchantments
                .stream()
                .map(ench -> new Pair<>(ench.enchantment, ench.level))
                .collect(Collectors.toList());
        this.texture = guiItem.texture;
        this.skullOwner = null;
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     */
    public ItemBuilder(Material material) {
        this(material, "");
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     * @param name     the name of the item
     */
    public ItemBuilder(Material material, String name) {
        this(material, name, 1);
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     * @param name     the name of the item
     * @param amount   the amount of the item
     */
    public ItemBuilder(Material material, String name, int amount) {
        this(material, name, amount, -1);
    }

    /**
     * Create a new ItemBuilder.
     *
     * @param material the material of the item
     * @param name     the name of the item
     * @param amount   the amount of the item
     * @param durability the durability of the item
     *                   (-1 = ignore, -2 = unbreakable)
     */
    public ItemBuilder(Material material, String name, int amount, int durability) {
        this.material = material;
        this.name = name;
        this.amount = amount;
        this.durability = durability;
        this.lore = new ArrayList<>();
        this.enchantments = new ArrayList<>();
        this.texture = null;
        this.skullOwner = null;
    }

    public ItemBuilder withDisplayName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder withDurability(int durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder withLore(String... lore) {
        this.lore.clear();
        this.lore.addAll(List.of(lore));
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        this.lore.addAll(List.of(lore));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.add(new Pair<>(enchantment, level));
        return this;
    }

    public ItemBuilder setBase64Head(String texture) {
        this.texture = texture;
        return this;
    }

    public ItemBuilder setSkullOwner(OfflinePlayer player) {
        this.skullOwner = player;
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

        if(durability == -2) {
            meta.setUnbreakable(true);
        }else if(durability != -1) {
            if(meta instanceof Damageable) {
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() - durability);
            }
        }

        if(!name.isEmpty()) {
            meta.setDisplayName(name);
        }

        if(!lore.isEmpty()) {
            meta.setLore(lore);
        }

        if (meta instanceof SkullMeta skullMeta) {
            if(texture != null) {
                PlayerProfile profile = Bukkit.getServer().createProfile(UUID.randomUUID());
                profile.setProperty(new ProfileProperty("textures", this.texture));
                skullMeta.setPlayerProfile(profile);
            }

            if(skullOwner != null) {
                skullMeta.setOwningPlayer(skullOwner);
            }
        }

        if(customModelData != 0) {
            meta.setCustomModelData(customModelData);
        }

        is.setItemMeta(meta);
        return is;
    }

}
