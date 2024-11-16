package de.neo.jagil.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.neo.jagil.JAGIL;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.ui.components.UIComponent;
import de.neo.jagil.util.InventoryPosition;
import de.neo.jagil.util.ParseUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class JsonGuiReader implements GuiReader<JsonObject> {

    @Override
    public boolean supportsFile(Path filePath, String content) {
        try {
            new Gson().fromJson(content, JsonObject.class);
            return true;
        } catch (JsonSyntaxException ignored) {
        }

        return false;
    }

    @Override
    public GuiTypes.DataGui read(String content) throws RuntimeException {
        GuiTypes.DataGui gui = new GuiTypes.DataGui();
        JsonObject json = new Gson().fromJson(content, JsonObject.class);

        // Unknown fileVersion
        gui.fileVersion = -1;

        JsonElement fileVersionElement = json.get("fileVersion");
        if (fileVersionElement != null) {
            gui.fileVersion = fileVersionElement.getAsLong();
        }

        gui.messageFormat = ParseUtil.getMessageFormat(json, "messageFormat");
        gui.name = ParseUtil.getAsComponent(gui, json.get("name"));
        gui.size = json.get("size").getAsInt();
        gui.animationMod = ParseUtil.getJsonInt(json, "animationTick");

        if (json.has("items")) {
            parseItems(gui, json);
        } else if (json.has("ui")) {
            try {
                parseUI(gui, json);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            JAGIL.getLogger().warning("[JAGIL] Empty GUI " + gui.name + ": no items section!");
            return gui;
        }

        return gui;
    }

    @Override
    public void parseItem(GuiTypes.DataGui gui, JsonObject json) {
        JsonObject jsonItem = json.getAsJsonObject();
        GuiTypes.GuiItem item = new GuiTypes.GuiItem();

        item.id = ParseUtil.getJsonString(jsonItem, "id");

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
                item.slot = ParseUtil.getAutoSlotId(gui);
            } else throw new IllegalStateException("slot is not json");
        }

        item.material = Material.getMaterial(ParseUtil.getJsonString(jsonItem, "material"));
        item.name = ParseUtil.getAsComponent(gui, jsonItem.get("name"));
        item.amount = ParseUtil.getJsonInt(jsonItem, "amount");
        item.amount = item.amount == 0 ? 1 : item.amount;

        if (jsonItem.has("lore")) {
            for(JsonElement strElem : jsonItem.get("lore").getAsJsonArray()) {
                item.lore.add(ParseUtil.getAsComponent(gui, strElem));
            }
        }

        if (jsonItem.has("enchantments")) {
            for(JsonElement enchantElem : jsonItem.get("enchantments").getAsJsonArray()) {
                JsonObject enchJson = enchantElem.getAsJsonObject();
                GuiTypes.GuiEnchantment enchantment = new GuiTypes.GuiEnchantment();
                enchantment.enchantment =
                        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).stream()
                                .filter(it -> enchJson.get("name").getAsString().equalsIgnoreCase(it.toString()))
                                .findFirst().get();
                enchantment.level = enchJson.get("level").getAsInt();
                item.enchantments.add(enchantment);
            }
        }

        if(jsonItem.has("texture")) {
            item.texture = ParseUtil.getJsonString(jsonItem, "texture");
        }

        if(jsonItem.has("modelData")) {
            item.customModelData = jsonItem.get("modelData").getAsInt();
        }

        if(jsonItem.has("animation")) {
            for(JsonElement animElem : jsonItem.get("animation").getAsJsonArray()) {
                JsonObject animFrame = animElem.getAsJsonObject();
                GuiTypes.GuiAnimationFrame frame = new GuiTypes.GuiAnimationFrame();
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

        if(jsonItem.has("attributes")) {
            for(JsonElement attrElem : jsonItem.get("attributes").getAsJsonArray()) {
                JsonObject attrJson = attrElem.getAsJsonObject();
                item.attributes.put(attrJson.get("name").getAsString(), attrJson.get("value"));
            }
        }

        if(!jsonItem.has("slot") || !jsonItem.get("slot").isJsonPrimitive()) {
            gui.items.put(item.slot, item);
        }

        if(jsonItem.has("slot")) {
            JsonElement slotElement = jsonItem.get("slot");
            if (slotElement.isJsonPrimitive()) {
                gui.items.put(item.slot, item);
            } else if (slotElement.isJsonObject()) {
                applyFillObject(gui, item, slotElement.getAsJsonObject());
            } else if (slotElement.isJsonArray()) {
                for(JsonElement fillElem : jsonItem.get("slot").getAsJsonArray()) {
                    if(fillElem.isJsonObject()) {
                        applyFillObject(gui, item, fillElem.getAsJsonObject());
                    } else if(fillElem.isJsonPrimitive()) {
                        int slot = fillElem.getAsInt();
                        GuiTypes.GuiItem item2 = new GuiTypes.GuiItem(item);
                        item2.slot = slot;
                        gui.items.put(slot, item2);
                    } else {
                        JAGIL.getLogger().warning("[JAGIL] Invalid fill property item in GUI:" + fillElem);
                    }
                }
            }
        }
    }

    @Override
    public void parseUIComponent(GuiTypes.DataGui gui, JsonObject jsonUi) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        UIComponent component = ParseUtil.getUIComponent(jsonUi.get("type").getAsString(), jsonUi);
        gui.ui.put(component.getId(), component);
    }

    public void parseUI(GuiTypes.DataGui gui, JsonObject json) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (JsonElement elem : json.get("ui").getAsJsonArray()) {
            parseUIComponent(gui, elem.getAsJsonObject());
        }
    }

    public void parseItems(GuiTypes.DataGui gui, JsonObject json) {
        for(JsonElement elem : json.get("items").getAsJsonArray()) {
            parseItem(gui, elem.getAsJsonObject());
        }
    }

    private void applyFillObject(GuiTypes.DataGui gui, GuiTypes.GuiItem item, JsonObject fillObject) {
        int from = fillObject.get("from").getAsInt();
        int to = fillObject.get("to").getAsInt();
        for (int i = from; i <= to; i++) {
            GuiTypes.GuiItem item2 = new GuiTypes.GuiItem(item);
            item2.slot = i;
            gui.items.put(i, item2);
        }
    }
}
