package Interface;

import org.opencv.core.Point;

public interface Navigation {
    public void goTo(Point src, Point dst, double direction);
}
