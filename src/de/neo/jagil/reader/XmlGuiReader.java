package de.neo.jagil.reader;

import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.util.ParseUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

/**
 * This class implements the {@link GuiReader} for xml.
 * It is deprecated and does not support {@link #parseItem(GuiTypes.DataGui, Object)} and {@link #parseItem(GuiTypes.DataGui, Object)}
 * Removal planned for v5. Convert your existing xml files to json using /convert xml json (JAGIL-Loader only)
 *
 * @deprecated use {@link JsonGuiReader} instead. marked as for removal in v5.
 */
@Deprecated(forRemoval = true)
public class XmlGuiReader extends GuiReader<Object> {

    public XmlGuiReader() {
        super("xml");
    }

    @Override
    public GuiTypes.DataGui read(String content) throws IOException {
        GuiTypes.DataGui gui = new GuiTypes.DataGui();

        String tag = "";
        String next = "";
        GuiTypes.GuiItem item = null;
        GuiTypes.GuiEnchantment enchantment = null;

        try {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new StringReader(content));

            parser: {
                while(reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();

                    switch(event.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT:
                            StartElement element = event.asStartElement();
                            String elem = element.getName().getLocalPart();
                            if(elem.equalsIgnoreCase("gui") || elem.equalsIgnoreCase("item")
                                    || elem.equalsIgnoreCase("lore")
                                    || elem.equalsIgnoreCase("enchantment")) {
                                tag = elem;
                                if(elem.equalsIgnoreCase("item")) {
                                    item = new GuiTypes.GuiItem();
                                }else if(elem.equalsIgnoreCase("enchantment")) {
                                    enchantment = new GuiTypes.GuiEnchantment();
                                }
                            }else {
                                next = elem;
                            }
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            Characters chars = event.asCharacters();
                            switch (next) {
                                case "id":
                                    if(item == null) continue;
                                    item.id = chars.getData().trim();
                                    break;

                                case "slot":
                                    if(item == null) continue;
                                    item.slot = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "material":
                                    if(item == null) continue;
                                    item.material = Material.getMaterial(chars.getData().toUpperCase());
                                    break;

                                case "name":
                                    if(tag.equalsIgnoreCase("item")) {
                                        if(item == null) continue;
                                        item.name = MiniMessage.miniMessage().deserialize(chars.getData());
                                    }else if(tag.equalsIgnoreCase("gui")) {
                                        gui.name = MiniMessage.miniMessage().deserialize(chars.getData());
                                    }
                                    break;

                                case "amount":
                                    if(item == null) continue;
                                    item.amount = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "line":
                                    if(item == null) continue;
                                    item.lore.add(MiniMessage.miniMessage().deserialize(chars.getData()));
                                    break;

                                case "size":
                                    gui.size = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "enchantmentName":
                                    if(enchantment == null) continue;
                                    enchantment.enchantment = Arrays.stream(Enchantment.values())
                                            .filter((it) -> chars.getData().equalsIgnoreCase(it.toString()))
                                            .findFirst().get();
                                    break;

                                case "enchantmentLevel":
                                    if(enchantment == null) continue;
                                    enchantment.level = Integer.parseInt(ParseUtil.normalizeString(chars.getData()));
                                    break;

                                case "base64":
                                    if(item == null) continue;
                                    item.texture = chars.getData();
                                    break;
                            }
                            next = "done";
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            EndElement endElement = event.asEndElement();
                            if(endElement.getName().getLocalPart().equalsIgnoreCase("item")) {
                                gui.items.put(item.slot, item);
                            }else if(endElement.getName().getLocalPart().equalsIgnoreCase("enchantment")) {
                                item.enchantments.add(enchantment);
                            }
                            break;

                        case XMLStreamConstants.END_DOCUMENT:
                            reader.close();
                            break parser;
                    }
                }
            }
        }catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return gui;
    }

    @Override
    public void parseItem(GuiTypes.DataGui gui, Object itemObject) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not supported by XmlGuiReader");
    }

    @Override
    public void parseUIComponent(GuiTypes.DataGui gui, Object uiComponentObject) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not supported by XmlGuiReader");
    }

    @Override
    public String getFileType() {
        return super.getFileType();
    }
}
