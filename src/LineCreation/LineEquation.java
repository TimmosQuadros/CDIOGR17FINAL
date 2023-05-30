package LineCreation;

public class LineEquation {
    private double a;
    private double b;

    /**
     * Constructs a line equation object with the given coefficients.
     *
     * @param a the coefficient 'a' of the linear equation.
     * @param b the coefficient 'b' of the linear equation.
     */

    public LineEquation(double a, double b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Returns the coefficient 'a' of the linear equation.
     *
     * @return the coefficient 'a'.
     */

    public double getA() {
        return this.a;
    }

    /**
     * Returns the coefficient 'b' of the linear equation.
     *
     * @return the coefficient 'b'.
     */
    public double getB() {
        return this.b;
    }

    /*public double getLength() {
        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;
        return Math.sqrt(dx * dx + dy * dy);
    }*/
}

