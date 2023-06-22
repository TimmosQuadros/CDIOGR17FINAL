package LineCreation;

/**
 * Author Emil Iversen.
 */

import org.opencv.core.Point;

import java.util.List;

public class AlignRobot {

    private LineSegment lengthSlope;
    private LineSegment widthSlope;

    public AlignRobot(List<Point> corners) {
        determineLengthSlope(corners);
        determineWidthSlope(corners);
    }

    /**
     * Determines the slope of the line that is based on the two long sides of the field.
     * We will use this slope to align the robot properly.
     * @param corners The detected corners of the field, used to create a lineSegment.
     */
    private void determineLengthSlope(List<Point> corners) {
        LineSegment upper = new LineSegment(corners.get(0), corners.get(1));
        LineSegment lower = new LineSegment(corners.get(2), corners.get(3));

        double estimatedSlope = (upper.getA() + upper.getB()) / 2.0;
        double estimatedB = (upper.getB() + lower.getB()) / 2.0;

        this.lengthSlope = new LineSegment(estimatedSlope, estimatedB);
    }

    /**
     * Determines the slope of the two short sides of the field.
     * @param corners The detected corners of the field, used to create a lineSegment.
     */
    private void determineWidthSlope(List<Point> corners) {
        LineSegment left = new LineSegment(corners.get(0), corners.get(3));
        LineSegment right = new LineSegment(corners.get(1), corners.get(2));

        double estimatedSlope = (left.getA() + left.getB()) / 2.0;
        double estimatedB = (right.getB() + right.getB()) / 2.0;

        this.widthSlope = new LineSegment(estimatedSlope, estimatedB);
    }

    public void alignRobotWithPoint(LineSegment robotDirection, Point ball){

    }


    public LineSegment getLengthAlign(){return lengthSlope;}
    public LineSegment getWidthAlign(){return widthSlope;}

}
