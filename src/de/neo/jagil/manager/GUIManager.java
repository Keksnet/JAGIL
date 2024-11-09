package de.neo.jagil.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import de.neo.jagil.annotation.Internal;
import de.neo.jagil.gui.GUI;

public class GUIManager {
	
	private static GUIManager INSTANCE;
	
	private HashMap<String, GUI> inventories;
	private HashSet<String> deleteLock;

	@Internal
	private GUIManager() {
		this.inventories = new HashMap<>();
		this.deleteLock = new HashSet<>();
	}

	@Internal
	public void register(GUI gui) {
		unregister(gui);
		this.inventories.put(gui.getIdentifier(), gui);
	}

	public Boolean isGUIByJAGIL(String identifier) {
		return this.inventories.containsKey(identifier);
	}

	public GUI getGUI(String identifier) {
		return this.inventories.get(identifier);
	}

	@Internal
	public void unregister(String identifier) {
		this.inventories.remove(identifier);
	}

	@Internal
	public void unregister(GUI gui) {
		this.inventories.remove(gui.getIdentifier());
	}

	@Internal
	public boolean unlockIfLocked(String identifier) {
		if (this.deleteLock.contains(identifier)) {
			this.deleteLock.remove(identifier);
			return true;
		}
		return false;
	}

	@Internal
	public void lockIfNotLocked(String identifier) {
		if(!this.deleteLock.contains(identifier)) {
			this.deleteLock.add(identifier);
		}
	}

	@Internal
	public static GUIManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GUIManager();
		}

		return INSTANCE;
	}
}
