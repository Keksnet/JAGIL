package de.neo8.jagil.ui.prebuilt;

import com.google.gson.JsonObject;
import de.neo8.jagil.gui.inventory.InventoryGuiTypes;
import de.neo8.jagil.ui.UIRenderPaneProvider;
import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.components.JsonParsable;
import de.neo8.jagil.ui.components.UIComponent;
import de.neo8.jagil.ui.impl.UIAction;
import de.neo8.jagil.util.InventoryPositionUtil;
import de.neo8.jagil.util.ParseUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Button implements UIComponent, Clickable, JsonParsable {

    private final String id;
    private final Point position;
    private final Dimension size;
    private final int priority;
    private final Rectangle bounds;
    private final Material material;
    private final boolean border;
    private final Material borderMaterial;

    public Button(JsonObject json) {
        this(json.get("id").getAsString(),
                Material.getMaterial(ParseUtil.getJsonStringOrNull(json, "material")),
                Material.getMaterial(ParseUtil.getJsonStringOrNull(json, "borderMaterial")),
                new Rectangle(ParseUtil.getPosition(json, "pos"), ParseUtil.getSize(json, "size")));
    }

    public Button(String id, Material material, Rectangle bounds) {
        this(id, material, null, bounds);
    }

    public Button(String id, Material material, Material borderMaterial, Rectangle bounds) {
        this.id = id;
        this.position = bounds.getLocation();
        this.size = bounds.getSize();
        this.priority = 0;
        this.bounds = bounds;
        this.material = material;
        this.border = borderMaterial != null;
        this.borderMaterial = borderMaterial;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull Point getPosition() {
        return position;
    }

    @Override
    public @NotNull Dimension getSize() {
        return size;
    }

    @Override
    public @NotNull Rectangle getBounds() {
        return bounds;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void render(@NotNull UIRenderPaneProvider<?> renderPlainProvider) {
        Object renderPlain = renderPlainProvider.getRenderPane();
        if (renderPlain instanceof InventoryGuiTypes.DataGui gui) {
            boolean renderBorder = size.width >= 3 && size.height >= 3 && border;
            // Render inner button
            InventoryGuiTypes.GuiItem innerItem = new InventoryGuiTypes.GuiItem();
            innerItem.material = material;
            innerItem.amount = 1;
            for (int i = 0; i < size.height; i++) {
                for (int j = 0; j < size.width; j++) {
                    InventoryGuiTypes.GuiItem copy = new InventoryGuiTypes.GuiItem(innerItem);
                    copy.slot = InventoryPositionUtil.toSlot(j, i);
                    if (copy.id == null) {
                        copy.id = "gen-slot-" + copy.slot;
                        copy.generatedId = true;
                    }

                    if (renderBorder) {
                        if (i == 0 || i == size.height - 1 || j == 0 || j == size.width - 1) {
                            // Render button
                            copy.material = borderMaterial;
                        }
                    }
                    gui.items.put(copy.id, copy);
                }
            }
        }
    }

    @Override
    public void click(@NotNull UIAction<?> click) {
        click.entity().sendMessage("Button clicked!");
    }
}
