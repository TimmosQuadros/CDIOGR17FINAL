package Interface;

import org.opencv.core.Point;

/*
 * Author Timm Daniel Rasmussen.
 */

public interface Navigation {
    public void goTo(Point src, Point dst, double direction);
}
