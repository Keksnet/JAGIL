package de.neo8.jagil.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.neo8.jagil.JAGIL;
import de.neo8.jagil.cache.CacheProvider;
import de.neo8.jagil.cache.h2.TextureCacheH2Impl;
import de.neo8.jagil.exception.JAGILException;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This UtilityClass provides different methods to create and modify an {@link ItemStack}.
 *
 * @author Neo8
 * @version 2.0
 */
public class ItemTool {

    /**
     * Creates a new {@link ItemStack} with the given {@link Material}.
     * Invokes {@link ItemTool#createItem(int, Material)}.
     *
     * @param m material of the new {@link ItemStack}
     * @return the new {@link ItemStack}
     */
    public static ItemStack createItem(Material m) {
        return createItem(1, m);
    }

    /**
     * Creates a new {@link ItemStack} with the given amount and {@link Material}.
     * Invokes {@link ItemTool#createItem(Component, int, Material)}.
     *
     * @param amount amount of items in the {@link ItemStack}
     * @param m      material of the new {@link ItemStack}
     * @return the new {@link ItemStack}
     */
    public static ItemStack createItem(int amount, Material m) {
        return createItem(Component.empty(), amount, m);
    }

    /**
     * Creates a new {@link ItemStack} with the given name and {@link Material}.
     * Invokes {@link ItemTool#createItem(Component, int, Material)}.
     *
     * @param name name of the new {@link ItemStack}
     * @param m    material of the new {@link ItemStack}
     * @return the new {@link ItemStack}
     */
    public static ItemStack createItem(Component name, Material m) {
        return createItem(name, 1, m);
    }

    /**
     * Creates a new {@link ItemStack} with the given name and {@link Material}.
     *
     * @param name   name of the new {@link ItemStack}
     * @param amount amount of items in the {@link ItemStack}
     * @param m      material of the new {@link ItemStack}
     * @return the new {@link ItemStack}
     */
    public static ItemStack createItem(Component name, int amount, Material m) {
        ItemStack is = new ItemStack(m, amount);
        if (!name.equals(Component.empty())) {
            ItemMeta meta = is.getItemMeta();
            meta.displayName(name);
            is.setItemMeta(meta);
        }
        return is;
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     * Invokes {@link ItemTool#createSkull(int, OfflinePlayer)}.
     * Use {@link ItemTool#createBase64Skull(Component, int, String)} for players that are offline
     *
     * @param skullOwner the owner of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createSkull(OfflinePlayer skullOwner) {
        return createSkull(1, skullOwner);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     * Invokes {@link ItemTool#createSkull(Component, int, OfflinePlayer)}.
     * Use {@link ItemTool#createBase64Skull(Component, int, String)} for players that are offline
     *
     * @param amount     amount of items in the {@link ItemStack}
     * @param skullOwner the owner of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createSkull(int amount, OfflinePlayer skullOwner) {
        return createSkull(Component.empty(), amount, skullOwner);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     * Invokes {@link ItemTool#createSkull(Component, int, OfflinePlayer)}.
     *
     * @param name       name of the new {@link ItemStack}
     * @param skullOwner the owner of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createSkull(Component name, OfflinePlayer skullOwner) {
        return createSkull(name, 1, skullOwner);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     *
     * @param name       name of the new {@link ItemStack}
     * @param amount     amount of items in the new {@link ItemStack}
     * @param skullOwner the owner of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createSkull(Component name, int amount, OfflinePlayer skullOwner) {
        boolean cachingEnabled = JAGIL.getGlobalJAGILConfig().getCachingConfig().isEnabled();

        ItemStack is = createItem(name, amount, Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) is.getItemMeta();

        PlayerProfile profile = skullOwner.getPlayerProfile();
        if (!profile.isComplete()) {
            if (!cachingEnabled || !CacheProvider.getInstance().getTextureCache().isTextureCached(skullOwner.getUniqueId())) {
                if (!profile.complete(true, true)) {
                    meta.setOwningPlayer(skullOwner);
                }
            } else {
                ProfileProperty texture = CacheProvider.getInstance().getTextureCache().getCachedTextureAsProperty(skullOwner.getUniqueId());
                if (texture == null) {
                    throw new JAGILException("texture should have been cached!");
                }

                profile.setProperty(texture);
            }
        }

        if (cachingEnabled && profile.isComplete()) {
            ProfileProperty texture = profile.getProperties()
                                             .stream()
                                             .filter(x -> x.getName().equalsIgnoreCase("textures"))
                                             .findFirst()
                                             .orElse(null);
            if (texture != null) {
                CacheProvider.getInstance().getTextureCache().updateCache(skullOwner.getUniqueId(), texture.getValue(), texture.getSignature());
            }
        }

        meta.setPlayerProfile(profile);
        is.setItemMeta(meta);
        return is;
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     * Invokes {@link ItemTool#createBase64Skull(Component, int, String)}.
     *
     * @param base64 base64 of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createBase64Skull(String base64) {
        return createBase64Skull(Component.empty(), 1, base64);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     * Invokes {@link ItemTool#createBase64Skull(Component, int, String)}.
     *
     * @param amount amount of items in the new {@link ItemStack}
     * @param base64 base64 of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createBase64Skull(int amount, String base64) {
        return createBase64Skull(Component.empty(), 1, base64);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     * Invokes {@link ItemTool#createBase64Skull(Component, int, String)}.
     *
     * @param name   name of the new {@link ItemStack}
     * @param base64 base64 of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createBase64Skull(Component name, String base64) {
        return createBase64Skull(name, 1, base64);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     *
     * @param name   name of the new {@link ItemStack}
     * @param amount amount of items in the new {@link ItemStack}
     * @param base64 base64 of the skull
     * @return the new {@link ItemStack}
     */
    public static ItemStack createBase64Skull(Component name, int amount, String base64) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        if (!name.equals(Component.empty())) {
            meta.displayName(name);
        }

        PlayerProfile profile = Bukkit.getServer().createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", base64));
        meta.setPlayerProfile(profile);

        is.setItemMeta(meta);
        return is;
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}
     * and attaches the given {@link PlayerProfile} to the {@link SkullMeta}.
     * Invokes {@link ItemTool#createSkull(Component, int, PlayerProfile)}.
     *
     * @param profile {@link PlayerProfile} to be attached to the head
     * @return the created {@link ItemStack}
     */
    public static ItemStack createSkull(PlayerProfile profile) {
        return createSkull(Component.empty(), 1, profile);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}
     * and attaches the given {@link PlayerProfile} to the {@link SkullMeta}.
     * Invokes {@link ItemTool#createSkull(Component, int, PlayerProfile)}.
     *
     * @param profile {@link PlayerProfile} to be attached to the head
     * @param amount  amount of items in the new {@link ItemStack}
     * @return the created {@link ItemStack}
     */
    public static ItemStack createSkull(int amount, PlayerProfile profile) {
        return createSkull(Component.empty(), amount, profile);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}
     * and attaches the given {@link PlayerProfile} to the {@link SkullMeta}.
     * Invokes {@link ItemTool#createSkull(Component, int, PlayerProfile)}.
     *
     * @param profile {@link PlayerProfile} to be attached to the head
     * @param name    name of the new {@link ItemStack}
     * @return the created {@link ItemStack}
     */
    public static ItemStack createSkull(Component name, PlayerProfile profile) {
        return createSkull(name, 1, profile);
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}
     * and attaches the given {@link PlayerProfile} to the {@link SkullMeta}.
     *
     * @param profile {@link PlayerProfile} to be attached to the head
     * @param name    name of the new {@link ItemStack}
     * @param amount  amount of items in the new {@link ItemStack}
     * @return the created {@link ItemStack}
     */
    public static ItemStack createSkull(Component name, int amount, PlayerProfile profile) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        is.setAmount(amount);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setPlayerProfile(profile);
        if (!name.equals(Component.empty())) {
            meta.displayName(name);
        }

        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack applySkullTexture(ItemStack itemStack, PlayerProfile profile) {
        if (!itemStack.getType().equals(Material.PLAYER_HEAD)) {
            throw new IllegalArgumentException("itemStack is not a PLAYER_HEAD");
        }

        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setPlayerProfile(profile);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     *
     * @param is   existing {@link ItemStack}
     * @param lore the lines of the lore
     * @return the new {@link ItemStack}
     */
    public static ItemStack setLore(ItemStack is, Component... lore) {
        List<Component> lore_l = Arrays.asList(lore);
        ItemMeta meta = is.getItemMeta();
        meta.lore(lore_l);
        is.setItemMeta(meta);
        return is;
    }

    /**
     * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
     *
     * @param is   existing {@link ItemStack}
     * @param ench enchament to add
     * @param lvl  level of enchantment
     * @return the new {@link ItemStack}
     */
    public static ItemStack addEnchantment(ItemStack is, Enchantment ench, int lvl) {
        is.addUnsafeEnchantment(ench, lvl);
        return is;
    }

}
