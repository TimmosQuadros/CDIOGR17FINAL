package LineCreation;

import org.opencv.core.Point;

public class LineSegment {
    private Point startPoint;
    private Point endPoint;
    private double a;
    private double b;
    private boolean infiniteSlope = false;

    /**
     * Constructs a line segment object with the given start and end points.
     *
     * @param startPoint the starting point of the line segment.
     * @param endPoint   the ending point of the line segment.
     */
    public LineSegment(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        if(startPoint.x == endPoint.x)
            this.infiniteSlope = true;
    }

    public LineSegment(double a, double b){
        this.a = a;
        this.b = b;
    }

    /**
     * Returns the starting point of the line segment.
     *
     * @return the starting point.
     */

    public Point getStartPoint() {
        return startPoint;
    }

    /**
     * Returns the ending point of the line segment.
     *
     * @return the ending point.
     */

    public Point getEndPoint() {
        return endPoint;
    }

    public double getLength() {
        return Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
    }

    public double getA(){return this.a;}
    public double getB(){return this.b;}

    /**
     * Checks if the line segment has an infinite slope (vertical line).
     *
     * @return true if the line segment has an infinite slope, false otherwise.
     */
    public boolean isInfiniteSlope() {
        return infiniteSlope;
    }

    /**
     * Determines the equation of the line segment if it does not have an infinite slope.
     */
    public void determineEquation(){
        if(!this.infiniteSlope) {
            double[] arr = ConstructLine.constructLine(this.startPoint, this.endPoint);
            this.a = arr[0];
            this.b = arr[1];
        }
    }
}
