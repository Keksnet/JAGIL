# Changes in v4

## Compatibility
Starting with JAGIL 4.0-beta.6 JAGIL will NOT support spigot or bukkit servers anymore.
The reason behind this rapid change is sadly the compatibility with libraries used by JAGIL.
Spigot and bukkit do not have support for the adventure api and MiniMessage.
The adventure adapter for bukkit only provides support up to minecraft version 1.20.6 (as of now).
Furthermore almost every minecraft server runs paper nowadays. Please keep using JAGIL v3.3.13
if you need to stay on spigot.

## Animations
- Animation frames (ItemStacks)
- Json support
- Manual animations (set ``GuiAnimation.animation``)
- ``animation`` attribute in json files
- Items can be moved

## Attributes
- Simple attributes using ``GuiItem.attributes``
- ``attributes`` attribute in json files

## UISystem
- Added UISystem Interfaces
- Added Gui Implementation

## Other changes
- Removed ``JAGIL.init(JavaPlugin, boolean)`` use ``JAGIL.init(JavaPlugin)`` instead.
- The loader plugin has to inject itself into the plugin using ``JAGIL.loaderPlugin = JavaPlugin``.
- Removed the return value of ``GUI.handleClose()``, ``GUI.handleDragLast()`` and ``GUI.handleLast()``.
- DataGUI has its own file now
- Merged XmlHead into GuiItem
- The constructor with a Path has been replaced by the constructor with a DataGUI.
- To read a GUI from a file you have to use ``GUIReaderManager.readFile(Path)``.
- Moved id based methods into the GUI class.
- Added a method to handle a GUI on cooldown.
- DataGUI based GUIs are filled in the ``GUI.fill()`` method. Make sure to call it when you override it.
- Json supports ``position`` in addition to ``slot``