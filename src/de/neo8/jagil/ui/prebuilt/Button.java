package de.neo8.jagil.ui.prebuilt;

import com.google.gson.JsonObject;
import de.neo8.jagil.gui.GuiTypes;
import de.neo8.jagil.ui.UIRenderPlainProvider;
import de.neo8.jagil.ui.components.Clickable;
import de.neo8.jagil.ui.components.JsonParsable;
import de.neo8.jagil.ui.components.UIComponent;
import de.neo8.jagil.ui.impl.UIAction;
import de.neo8.jagil.util.InventoryPositionUtil;
import de.neo8.jagil.util.ParseUtil;
import org.bukkit.Material;

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
    public String getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void render(UIRenderPlainProvider<?> renderPlainProvider) {
        Object renderPlain = renderPlainProvider.getRenderPlain();
        if (renderPlain instanceof GuiTypes.DataGui) {
            GuiTypes.DataGui gui = (GuiTypes.DataGui) renderPlain;
            boolean renderBorder = size.width >= 3 && size.height >= 3 && border;
            // Render inner button
            GuiTypes.GuiItem innerItem = new GuiTypes.GuiItem();
            innerItem.material = material;
            innerItem.amount = 1;
            for (int i = 0; i < size.height; i++) {
                for (int j = 0; j < size.width; j++) {
                    GuiTypes.GuiItem copy = new GuiTypes.GuiItem(innerItem);
                    int slot = InventoryPositionUtil.toSlot(j, i);
                    copy.slot = slot;
                    if (renderBorder) {
                        if (i == 0 || i == size.height - 1 || j == 0 || j == size.width - 1) {
                            // Render button
                            copy.material = borderMaterial;
                        }
                    }
                    gui.items.put(slot, copy);
                }
            }
        }
    }

    @Override
    public void click(UIAction<?> click) {
        click.getEntity().sendMessage("Button clicked!");
    }
}
