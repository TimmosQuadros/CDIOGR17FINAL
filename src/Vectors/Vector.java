package Vectors;

import org.opencv.core.Point;

public class Vector {
    private double x;
    private double y;
    private double length;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        setLength();
    }
    public Vector(Point point1, Point point2) {
        this.x = Math.abs(point1.x - point2.x);
        this.y = Math.abs(point1.y - point2.y);
        setLength();
    }

    private void setLength() {
        length = Math.sqrt(x*x + y*y);
    }

    public double getLength(){
        return length;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


}