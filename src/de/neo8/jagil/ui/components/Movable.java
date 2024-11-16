package de.neo8.jagil.ui.components;

import java.awt.*;

public interface Movable extends Dynamic {

    void setPosition(Point position);

    void move(int x, int y);

    void move(Point position);

    void moveTo(int x, int y);

    void moveTo(Point position);



}
