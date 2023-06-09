package LineCreation;

/**
 * Author Emil Iversen.
 */

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

    public Circle(Point center, double radius){
        this.centerX = center.x;
        this.centerY = center.y;
        this.radius = radius;
    }

    public double getRadius(){
        return radius;
    }

    public Point getCenter(){
        return new Point(centerX,centerY);
    }

    /**
     * Determines if a points is within the radius of the circle based on its center.
     * @param point
     * @return
     */
    public boolean isPointInside(Point point) {
        double distance = Math.sqrt(Math.pow((point.x - centerX), 2) + Math.pow((point.y - centerY), 2));
        return distance <= radius;
    }

    public void setCenter(double x, double y){
        this.centerX = x;
        this.centerY = y;
    }
}
