package Vectors;

public class Vector {
    private double x;
    private double y;
    private double length;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
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