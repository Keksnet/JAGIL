package de.neo8.jagil.util;

import lombok.Getter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HdbProvider {

    @Getter
    private final static HeadDatabaseAPI headDatabaseAPI = new HeadDatabaseAPI();

}
