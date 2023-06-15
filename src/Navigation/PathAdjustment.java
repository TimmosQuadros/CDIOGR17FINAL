package Navigation;

import LineCreation.Circle;
import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class PathAdjustment {

    private RedRectangleDetection fieldDetection;

    public PathAdjustment(RedRectangleDetection fieldDetection){
        this.fieldDetection = fieldDetection;
    }

    public boolean isNearCross(Point ballCoordinate){
        return fieldDetection.getRedCross().getCrossArea().isPointInside(ballCoordinate);
    }

    /**
     * This method determines whether a ball is near a side or not.
     * If the ball is determined to be near a side, it will require a special route to pick it up.
     * @param ballCoordinate
     * @return true if the ball is outside of the area of the inner field, meaning its by the side.
     */
    public boolean isNearSide(Point ballCoordinate){
        Path2D.Double rectangle = new Path2D.Double();
        rectangle.moveTo(fieldDetection.getFloorCorners()[0].x, fieldDetection.getFloorCorners()[0].y);
        rectangle.lineTo(fieldDetection.getFloorCorners()[1].x, fieldDetection.getFloorCorners()[1].y);
        rectangle.lineTo(fieldDetection.getFloorCorners()[2].x, fieldDetection.getFloorCorners()[2].y);
        rectangle.lineTo(fieldDetection.getFloorCorners()[3].x, fieldDetection.getFloorCorners()[3].y);
        rectangle.closePath();

        Point2D.Double point = new Point2D.Double(ballCoordinate.x, ballCoordinate.y);

        return !rectangle.contains(point);
    }

    public Point[] setupNewPathAroundCross(){
        Point[] path = new Point[2];

        return path;
    }

    /**
     * This method returns true if the line that is created from the robot to the ball,
     * intersects with the equation of the circle representing the red cross obstacle.
     * @param ball target ball to pick up.
     * @param robotCenter The center of the robot.
     * @return false is the path does not intersect with the cross.
     */
    public boolean pathIntersects(Point ball, Point robotCenter) {
        // Line equation y = mx + c
        double m = (ball.y - robotCenter.y) / (ball.x - robotCenter.x);
        double c = robotCenter.y - m * robotCenter.x;

        // Substitute line equation into circle equation
        double h = fieldDetection.getRedCross().getScalefactorAdjustedCrossArea().getCenter().x;
        double k = fieldDetection.getRedCross().getScalefactorAdjustedCrossArea().getCenter().y;

        // Coefficients for the quadratic equation
        double a = (m * m + 1);
        double b = (2 * m * c - 2 * m * k - 2 * h);
        double cc = (h * h + c * c - 2 * c * k + k * k -
                fieldDetection.getRedCross().getScalefactorAdjustedCrossArea().getRadius() *
                        fieldDetection.getRedCross().getScalefactorAdjustedCrossArea().getRadius());

        // Discriminant
        double discriminant = b * b - 4 * a * cc;

        return discriminant > 0;
    }

    public void alighWithSide(){

    }

    public Point[] createFixedPathToGoal(){
        Point[] pathToGoal = new Point[4];

        return pathToGoal;
    }

    /**
     * This method is used to determined if a ball is hard to collect.
     * After it has been determined that a ball is near a side,
     * we will check if the ball is in a corner.
     * If the ball is in a corner, it is hard to collect and might be skipped.
     * Otherwise we will proceed to collect it.
     * @param ballCoordinate coordinate of the ball.
     * @return true if the ball is within the radius of a corner.
     */
    public boolean isInCorner(Point ballCoordinate){
        Point center = null;
        //compares ball coordinate to center of the cross
        if (ballCoordinate.y > fieldDetection.getRedCross().getCoordinates().get(0).y){
            if (ballCoordinate.x > fieldDetection.getRedCross().getCoordinates().get(0).x)
                //right upper corner.
                center = fieldDetection.getRawCorners()[1];
            else
                //left upper corner
                center = fieldDetection.getRawCorners()[0];
        }else{
            if(ballCoordinate.x > fieldDetection.getRedCross().getCoordinates().get(0).x)
                //right lower corner
                center = fieldDetection.getRawCorners()[2];
            else
                //left lower corner
                center = fieldDetection.getRawCorners()[3];
        }

        //if the center is still null, something have gone wrong and we will skip the ball.
        if (center == null)
            return true;

        //adjusts robo width with 2 centimeters, to take height for the side widths.
        Circle circle = new Circle(center, fieldDetection.getRoboWidth() + 2.0);

        return circle.isPointInside(ballCoordinate);
    }

    /**
     * Method to find out whether a ball is on the field or not.
     * @param ballCoordinate coordinate of the ball.
     * @return true if ball is on field.
     */
    public boolean isBallInField(Point ballCoordinate){
        Path2D.Double rectangle = new Path2D.Double();
        rectangle.moveTo(fieldDetection.getMaskCorners()[0].x, fieldDetection.getMaskCorners()[0].y);
        rectangle.lineTo(fieldDetection.getMaskCorners()[1].x, fieldDetection.getMaskCorners()[1].y);
        rectangle.lineTo(fieldDetection.getMaskCorners()[2].x, fieldDetection.getMaskCorners()[2].y);
        rectangle.lineTo(fieldDetection.getMaskCorners()[3].x, fieldDetection.getMaskCorners()[3].y);
        rectangle.closePath();

        Point2D.Double point = new Point2D.Double(ballCoordinate.x, ballCoordinate.y);

        return rectangle.contains(point);
    }

}
