package LineCreation;

import org.opencv.core.Point;

public class Circle {

    private double centerX;
    private double centerY;
    private double radius;

    public Circle(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public double getRadius(){
        return radius;
    }

    public Point getCenter(){
        return new Point(centerX,centerY);
    }

    public boolean isPointInside(double x, double y) {
        double distance = Math.sqrt(Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2));
        return distance <= radius;
    }
}
