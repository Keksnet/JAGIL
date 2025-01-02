package de.neo8.jagil.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.neo8.jagil.JAGIL;
import de.neo8.jagil.exception.JAGILException;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.components.UIComponent;
import de.neo8.jagil.util.HdbProvider;
import de.neo8.jagil.util.InventoryPosition;
import de.neo8.jagil.util.Pair;
import de.neo8.jagil.util.ParseUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class JsonGuiReader implements GuiReader<JsonObject> {

    @Nullable
    private final TagResolver tagContext;
    private final InventoryGuiTypes.DataGui dataGui = new InventoryGuiTypes.DataGui();

    private JsonGuiReader(@Nullable TagResolver tagContext) {
        this.tagContext = tagContext;
    }

    @Override
    public InventoryGuiTypes.DataGui read(String content) throws RuntimeException {
        JsonObject json = new Gson().fromJson(content, JsonObject.class);

        // Unknown fileVersion
        dataGui.fileVersion = -1;

        JsonElement fileVersionElement = json.get("fileVersion");
        if (fileVersionElement != null) {
            dataGui.fileVersion = fileVersionElement.getAsLong();
        }

        dataGui.messageFormat = ParseUtil.getMessageFormat(json, "messageFormat");
        dataGui.features = json.get("features").getAsJsonArray().asList()
                .stream()
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .toList();
        dataGui.name = getAsComponent(json.get("name"));
        dataGui.size = json.get("size").getAsInt();
        dataGui.animationTick = ParseUtil.getJsonInt(json, "animationTick");

        // check if all features are supported by this JAGIL version
        List<String> supportedFeatures = JAGIL.getGlobalJAGILConfig().getSupportedFeatures();
        if (!dataGui.features
                .stream()
                .map(requiredFeature -> new Pair<>(requiredFeature, supportedFeatures.contains(requiredFeature)))
                .allMatch(Pair::getValue)) {
            String unsupportedFeatures = dataGui.features
                    .stream()
                    .map(requiredFeature -> new Pair<>(requiredFeature, supportedFeatures.contains(requiredFeature)))
                    .filter(x -> !x.getValue())
                    .map(Pair::getKey)
                    .collect(Collectors.joining(", "));
            throw new JAGILException("Gui-File requires unsupported features: " + unsupportedFeatures);
        }

        if (json.has("items")) {
            parseItems(json);
        } else if (json.has("ui")) {
            try {
                parseUI(json);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            JAGIL.getLogger().warning("[JAGIL] Empty GUI " + this.dataGui.name + ": no items section!");
            return this.dataGui;
        }

        return this.dataGui;
    }

    public void parseItem(JsonObject json) {
        JsonObject jsonItem = json.getAsJsonObject();
        InventoryGuiTypes.GuiItem item = new InventoryGuiTypes.GuiItem();

        item.id = ParseUtil.getJsonString(jsonItem, "id");
        item.template = false;
        if (jsonItem.has("template")) {
            item.template = jsonItem.get("template").getAsBoolean();
        }

        item.layer = ParseUtil.getJsonInt(jsonItem, "layer", 0);

        // parse slot/pos attribute
        if (jsonItem.has("pos")) {
            item.slot = ParseUtil.getJsonPosition(jsonItem, "pos").toSlot();
        } else {
            if (jsonItem.has("slot")) {
                JsonElement slotElement = jsonItem.get("slot");
                if (slotElement.isJsonPrimitive()) {
                    item.slot = slotElement.getAsInt();
                } else if (slotElement.isJsonObject()) {
                    JsonObject fillObject = slotElement.getAsJsonObject();
                    item.slot = fillObject.get("from").getAsInt();
                } else if (slotElement.isJsonArray()) {
                    JsonElement baseSlot = slotElement.getAsJsonArray().get(0);
                    if (baseSlot.isJsonObject()) {
                        item.slot = baseSlot.getAsJsonObject().get("from").getAsInt();
                    } else if (baseSlot.isJsonPrimitive()) {
                        item.slot = baseSlot.getAsJsonPrimitive().getAsInt();
                    } else {
                        JAGIL.getLogger().warning("[JAGIL] slot property for item " + item.id + " is invalid!");
                    }
                } else {
                    JAGIL.getLogger().warning("[JAGIL] slot property for item " + item.id + " is invalid!");
                }
            } else if (!item.id.isEmpty()) {
                item.slot = ParseUtil.getAutoSlotId(dataGui);
            } else throw new IllegalStateException("slot is not json");
        }

        item.material = Material.getMaterial(ParseUtil.getJsonString(jsonItem, "material"));
        item.name = getAsComponent(jsonItem.get("name"));
        item.amount = ParseUtil.getJsonInt(jsonItem, "amount");
        item.amount = item.amount == 0 ? 1 : item.amount;

        if (jsonItem.has("lore")) {
            for (JsonElement strElem : jsonItem.get("lore").getAsJsonArray()) {
                item.lore.add(getAsComponent(strElem));
            }
        }

        if (jsonItem.has("enchantments")) {
            for (JsonElement enchantElem : jsonItem.get("enchantments").getAsJsonArray()) {
                JsonObject enchJson = enchantElem.getAsJsonObject();
                InventoryGuiTypes.GuiEnchantment enchantment = new InventoryGuiTypes.GuiEnchantment();
                enchantment.enchantment =
                        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).stream()
                                .filter(it -> enchJson.get("name").getAsString().equalsIgnoreCase(it.toString()))
                                .findFirst()
                                .get();
                enchantment.level = enchJson.get("level").getAsInt();
                item.enchantments.add(enchantment);
            }
        }

        if (jsonItem.has("texture")) {
            if (item.material != Material.PLAYER_HEAD && item.material != Material.PLAYER_WALL_HEAD) {
                JAGIL.getLogger().warning("Using texture property on a non-head item. Changing item material to PLAYER_HEAD.");
                item.material = Material.PLAYER_HEAD;
            }

            item.texture = ParseUtil.getJsonString(jsonItem, "texture");
            if (item.texture != null && item.texture.startsWith("@hdb-")) {
                if (JAGIL.getGlobalJAGILConfig().getSupportedFeatures().contains("head-database-api")) {
                    item.texture = HdbProvider.getHeadDatabaseAPI().getBase64(item.texture.substring(5));
                } else {
                    JAGIL.getLogger().warning("HeadDatabase support is either disabled or not available. GuiFile uses @hdb-<id> despite this.");
                }
            }
        }

        if (jsonItem.has("modelData")) {
            item.customModelData = jsonItem.get("modelData").getAsInt();
        }

        if (jsonItem.has("animation")) {
            for (JsonElement animElem : jsonItem.get("animation").getAsJsonArray()) {
                JsonObject animFrame = animElem.getAsJsonObject();
                InventoryGuiTypes.GuiAnimationFrame frame = new InventoryGuiTypes.GuiAnimationFrame();
                frame.itemId = animFrame.has("itemId") ? animFrame.get("itemId").getAsString() : item.id;
                frame.position = animFrame.has("pos") ?
                        ParseUtil.getJsonPosition(animFrame, "pos") : InventoryPosition.fromSlot(item.slot);
                frame.shouldCleanUp = !animFrame.has("cleanUp") || animFrame.get("cleanUp").getAsBoolean();
                if (!item.animationFrames.isEmpty())
                    frame.previousFrame = item.animationFrames.get(item.animationFrames.size() - 1);
                item.animationFrames.add(frame);
            }
            item.animationFrames.get(0).previousFrame = item.animationFrames.get(item.animationFrames.size() - 1);
        }

        if (jsonItem.has("attributes")) {
            for (JsonElement attrElem : jsonItem.get("attributes").getAsJsonArray()) {
                JsonObject attrJson = attrElem.getAsJsonObject();
                item.attributes.put(attrJson.get("name").getAsString(), attrJson.get("value"));
            }
        }

        if (item.id == null) {
            item.id = "gen-slot-" + item.slot;
            item.generatedId = true;
        }

        if (!jsonItem.has("slot") || !jsonItem.get("slot").isJsonPrimitive()) {
            dataGui.items.put(item.id, item);
        }

        if (jsonItem.has("slot")) {
            JsonElement slotElement = jsonItem.get("slot");
            if (slotElement.isJsonPrimitive()) {
                dataGui.items.put(item.id, item);
            } else if (slotElement.isJsonObject()) {
                applyFillObject(item, slotElement.getAsJsonObject());
            } else if (slotElement.isJsonArray()) {
                for (JsonElement fillElem : jsonItem.get("slot").getAsJsonArray()) {
                    if (fillElem.isJsonObject()) {
                        applyFillObject(item, fillElem.getAsJsonObject());
                    } else if (fillElem.isJsonPrimitive()) {
                        int slot = fillElem.getAsInt();
                        InventoryGuiTypes.GuiItem item2 = new InventoryGuiTypes.GuiItem(item);
                        item2.slot = slot;
                        item2.id = "gen-slot-" + item2.slot;
                        item2.generatedId = true;
                        dataGui.items.put(item2.id, item2);
                    } else {
                        JAGIL.getLogger().warning("[JAGIL] Invalid fill property item in GUI:" + fillElem);
                    }
                }
            }
        }
    }

    private void parseUIComponent(JsonObject jsonUi) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        UIComponent component = ParseUtil.getUIComponent(jsonUi.get("type").getAsString(), jsonUi);
        this.dataGui.ui.put(component.getId(), component);
    }

    private void parseUI(JsonObject json) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (JsonElement elem : json.get("ui").getAsJsonArray()) {
            parseUIComponent(elem.getAsJsonObject());
        }
    }

    private void parseItems(JsonObject json) {
        for (JsonElement elem : json.get("items").getAsJsonArray()) {
            parseItem(elem.getAsJsonObject());
        }
    }

    private void applyFillObject(InventoryGuiTypes.GuiItem item, JsonObject fillObject) {
        int from = fillObject.get("from").getAsInt();
        int to = fillObject.get("to").getAsInt();
        for (int i = from; i <= to; i++) {
            InventoryGuiTypes.GuiItem item2 = new InventoryGuiTypes.GuiItem(item);
            item2.slot = i;
            item2.id = "gen-slot-" + item2.slot;
            item2.generatedId = true;

            this.dataGui.items.put(item2.id, item2);
        }
    }

    @NotNull
    private Component getAsComponent(@Nullable JsonElement jsonElement) {
        if (jsonElement == null) {
            return Component.empty();
        }

        String serializedMessage = jsonElement.getAsString();
        if (serializedMessage == null || serializedMessage.isBlank()) {
            return Component.empty();
        }

        if (this.dataGui.messageFormat == InventoryGuiTypes.MessageFormat.MINI_MESSAGE) {
            if (this.tagContext == null) {
                return MiniMessage.miniMessage().deserialize(serializedMessage);
            }

            return MiniMessage.miniMessage().deserialize(serializedMessage, tagContext)
                    .applyFallbackStyle(Style.style(TextDecoration.ITALIC.withState(TextDecoration.State.FALSE)));
        }

        ComponentSerializer<Component, ? extends Component, String> serializer;
        switch (this.dataGui.messageFormat) {
            case LEGACY -> serializer = LegacyComponentSerializer.legacySection();
            case PLAIN -> serializer = PlainTextComponentSerializer.plainText();
            case JSON -> serializer = GsonComponentSerializer.gson();

            default -> throw new IllegalArgumentException("Unsupported format: " + this.dataGui.messageFormat);
        }

        return serializer.deserialize(jsonElement.getAsString());
    }

    public static class Provider implements GuiReaderProvider<JsonGuiReader> {
        @Getter
        private final static Provider instance = new Provider();

        private Provider() {
        }

        @Override
        public boolean supportsFile(@NotNull Path filePath, @NotNull String content) {
            try {
                new Gson().fromJson(content, JsonObject.class);
                return true;
            } catch (JsonSyntaxException ignored) {
            }

            return false;
        }

        @Override
        public @NotNull JsonGuiReader getReader(@Nullable TagResolver tagContext) {
            return new JsonGuiReader(tagContext);
        }
    }
}
