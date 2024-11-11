package de.neo.jagil.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import de.neo.jagil.annotation.Internal;
import de.neo.jagil.gui.GUI;

public class GUIManager {
	
	private static GUIManager INSTANCE;
	
	private HashMap<String, GUI> inventories;

	@Internal
	private GUIManager() {
		this.inventories = new HashMap<>();
	}

	@Internal
	public static GUIManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GUIManager();
		}

		return INSTANCE;
	}
}
