package de.neo8.jagil.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neo8.jagil.annotation.Internal;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.components.JsonParsable;
import de.neo8.jagil.ui.components.UIComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ParseUtil {

    @Internal
    public static String getJsonString(JsonObject json, String key) {
        if (!json.has(key)) return "";
        return json.get(key).getAsString();
    }

    @Internal
    public static String getJsonStringOrNull(JsonObject json, String key) {
        String jsonString = getJsonString(json, key);
        if (jsonString.isEmpty()) return null;
        return jsonString;
    }

    @Internal
    public static int getJsonInt(JsonObject json, String key) {
        if (!json.has(key)) return 0;
        return json.get(key).getAsInt();
    }

    @Internal
    public static String normalizeString(String unfiltered) {
        StringBuilder r = new StringBuilder();
        for (char c : unfiltered.toCharArray()) {
            if (Character.isDigit(c) || c == '-') {
                r.append(c);
            }
        }
        return r.toString().trim();
    }

    @Internal
    public static int getAutoSlotId(InventoryGuiTypes.DataGui gui) {
        for (int i = -1; i > -999; i--) {
            if (gui.items.containsKey(i)) continue;
            return i;
        }
        throw new RuntimeException("No free slot id found!");
    }

    @Internal
    public static InventoryPosition getJsonPosition(JsonObject frame, String key) {
        if (!frame.has(key)) return InventoryPosition.DEFAULT;
        JsonObject json = frame.get(key).getAsJsonObject();
        int x = json.has("x") ? json.get("x").getAsInt() : 0;
        int y = json.has("y") ? json.get("y").getAsInt() : 0;
        return new InventoryPosition(x, y);
    }

    @Internal
    public static Point getPosition(JsonObject frame, String key) {
        if (!frame.has(key)) return new Point(0, 0);
        JsonObject json = frame.get(key).getAsJsonObject();
        int x = json.has("x") ? json.get("x").getAsInt() : 0;
        int y = json.has("y") ? json.get("y").getAsInt() : 0;
        return new Point(x, y);
    }

    @Internal
    public static Dimension getSize(JsonObject frame, String key) {
        if (!frame.has(key)) return new Dimension(0, 0);
        JsonObject json = frame.get(key).getAsJsonObject();
        int x = json.has("width") ? json.get("width").getAsInt() : 0;
        int y = json.has("height") ? json.get("height").getAsInt() : 0;
        return new Dimension(x, y);
    }

    @Internal
    public static <T extends UIComponent & JsonParsable> T getUIComponent(String type, JsonObject json)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<T> uiClazz = (Class<T>) Class.forName(type);
        return uiClazz.getConstructor(JsonObject.class).newInstance(json);
    }

    @Internal
    public static InventoryGuiTypes.MessageFormat getMessageFormat(JsonObject json, String key) {
        String messageFormat = getJsonStringOrNull(json, key);
        if (messageFormat == null || !Arrays.stream(InventoryGuiTypes.MessageFormat.values()).anyMatch((x) -> x.name().equalsIgnoreCase(messageFormat))) {
            return InventoryGuiTypes.MessageFormat.MINI_MESSAGE;
        }

        return InventoryGuiTypes.MessageFormat.valueOf(messageFormat);
    }

    @Internal
    public static Component getAsComponent(InventoryGuiTypes.DataGui gui, JsonElement jsonElement) {
        ComponentSerializer<Component, ? extends Component, String> serializer;
        switch (gui.messageFormat) {
            case MINI_MESSAGE -> serializer = MiniMessage.miniMessage();
            case LEGACY -> serializer = LegacyComponentSerializer.legacySection();
            case PLAIN -> serializer = PlainTextComponentSerializer.plainText();
            case JSON -> serializer = GsonComponentSerializer.gson();

            default -> throw new IllegalArgumentException("Unsupported format: " + gui.messageFormat);
        }

        String serializedMessage = jsonElement.getAsString();
        if (serializedMessage == null || serializedMessage.isBlank()) {
            return Component.empty();
        }

        return serializer.deserialize(jsonElement.getAsString());
    }

}
