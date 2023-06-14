package Navigation;

import LineCreation.Circle;
import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;

public class PathAdjustment {

    private RedRectangleDetection fieldDetection;

    public PathAdjustment(RedRectangleDetection fieldDetection){
        this.fieldDetection = fieldDetection;
    }

    public boolean isNearCross(){

        return true;
    }

    public boolean isNearSide(){

        return true;
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
        double h = fieldDetection.getRedCross().scalefactorAdjustedCrossArea.getCenter().x;
        double k = fieldDetection.getRedCross().scalefactorAdjustedCrossArea.getCenter().y;

        // Coefficients for the quadratic equation
        double a = (m * m + 1);
        double b = (2 * m * c - 2 * m * k - 2 * h);
        double cc = (h * h + c * c - 2 * c * k + k * k - fieldDetection.getRedCross().scalefactorAdjustedCrossArea.getRadius() * fieldDetection.getRedCross().scalefactorAdjustedCrossArea.getRadius());

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
                center = fieldDetection.getFloorCorners().get(1);
            else
                //left upper corner
                center = fieldDetection.getFloorCorners().get(0);
        }else{
            if(ballCoordinate.x > fieldDetection.getRedCross().getCoordinates().get(0).x)
                //right lower corner
                center = fieldDetection.getFloorCorners().get(2);
            else
                //left lower corner
                center = fieldDetection.getFloorCorners().get(3);
        }

        //if the center is still null, something have gone wrong and we will skip the ball.
        if (center == null)
            return true;

        Circle circle = new Circle(center, fieldDetection.getRoboWidth());

        return circle.isPointInside(ballCoordinate);
    }


}
