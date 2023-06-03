package LineCreation;

import org.opencv.core.Point;

import java.util.List;

public class AlignRobot {

    private LineSegment lengthSlope;
    private LineSegment widthSlope;

    public AlignRobot(List<Point> corners) {
        determineLengthSlope(corners);
        determineWidthSlope(corners);
    }

    private void determineLengthSlope(List<Point> corners) {
        LineSegment upper = new LineSegment(corners.get(0), corners.get(1));
        LineSegment lower = new LineSegment(corners.get(2), corners.get(3));

        double estimatedSlope = (upper.getA() + upper.getB()) / 2.0;
        double estimatedB = (upper.getB() + lower.getB()) / 2.0;

        this.lengthSlope = new LineSegment(estimatedSlope, estimatedB);
    }

    private void determineWidthSlope(List<Point> corners) {
        LineSegment left = new LineSegment(corners.get(0), corners.get(3));
        LineSegment right = new LineSegment(corners.get(1), corners.get(2));

        double estimatedSlope = (left.getA() + left.getB()) / 2.0;
        double estimatedB = (right.getB() + right.getB()) / 2.0;

        this.widthSlope = new LineSegment(estimatedSlope, estimatedB);
    }


    public LineSegment getLengthAlign(){return lengthSlope;}
    public LineSegment getWidthAlign(){return widthSlope;}

}
