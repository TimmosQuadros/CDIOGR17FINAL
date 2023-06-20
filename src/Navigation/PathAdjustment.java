package Navigation;

import LineCreation.Circle;
import ObjectDetection.RedRectangleDetection;
import Vectors.Vector;
import org.opencv.core.Point;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * this class' methods will be used to determine if we should go for a ball in this order.
 * First we check whether the ball is collectable, with the go for ball method.
 * If this method return true, we move on to the next check.
 * For the next check we use the path intersects method.
 * If this method returns false, we don't need any more work from this class.
 * Otherwise we need to adjust the path, by adding a pitstop on the way.
 * Here we use the adjust path method that will return a point that the robot should go to.
 * From this point the robot can pick up the ball.
 */
public class PathAdjustment {
    private double leftSideX;
    private double rightSideX;
    private double upperSideY;
    private double lowerSideY;
    private Point leftUpperQuadrantWayPoint;
    private Point leftLowerQuadrantWayPoint;
    private Point rightUpperQuadrantWayPoint;
    private Point rightLowerQuadrantWayPoint;
    private Point goalAlignmentPoint;
    private RedRectangleDetection fieldDetection;

    public PathAdjustment(RedRectangleDetection fieldDetection){
        this.fieldDetection = fieldDetection;
        determineWaypoints();
        setGoalAlignmentPoint();
        determinePointsForSide();
    }

    /**
     * This point is calculated based on the two waypoints on the left side,
     * although it should be based on the side of the small goal, which is the only goal we will deliver in.
     * The point will be used for better alignment with the goal, helping to achieve a better delivery angle.
     */
    private void setGoalAlignmentPoint() {
        goalAlignmentPoint = new Point((leftLowerQuadrantWayPoint.x + leftUpperQuadrantWayPoint.x) / 2.0,
                fieldDetection.getGoals().get(0).y);
    }

    private void determineWaypoints() {
        leftUpperQuadrantWayPoint = getAverage(fieldDetection.getCourseCoordinates()[0]);
        leftLowerQuadrantWayPoint = getAverage(fieldDetection.getCourseCoordinates()[3]);
        rightUpperQuadrantWayPoint = getAverage(fieldDetection.getCourseCoordinates()[1]);
        rightLowerQuadrantWayPoint = getAverage(fieldDetection.getCourseCoordinates()[2]);
    }

    private Point getAverage(Point cornerPoint) {
        return new Point((fieldDetection.getRedCross().getCrossArea().getCenter().x + cornerPoint.x) / 2.0,
                (fieldDetection.getRedCross().getCrossArea().getCenter().y + cornerPoint.y) / 2.0);
    }

    /**
     * This method finds the path to the ball using the waypoints.
     * It is in use either when a ball is near the side or cross,
     * or when it is determined that the current path would intersect with the cross.
     * @param ballCoordinate coordinate of ball.
     * @param roboCenter robocenter.
     * @return An adjusted path to the ball.
     */
    public List<Point> adjustPath(Point ballCoordinate, Point roboCenter){
        List<Point> path = new ArrayList<>();
        boolean nearSide = isNearSide(ballCoordinate);
        boolean nearCross = isAroundCross(ballCoordinate);

        //if the first if statement is true, the points to the opposite quadrant will be added to the path.
        //Otherwise we will just add the point of the near quadrant to the path.
        if(!determineIfRoboAndBallAreInOppositeQuadrant(determineQuadrant(roboCenter, path), ballCoordinate, path)) {
            if (!nearCross && !nearSide) {
                if (pathIntersects(path.get(0), ballCoordinate)) {
                    determineQuadrant(ballCoordinate, path);
                    path.add(ballCoordinate);
                } else
                    path.add(ballCoordinate);

                if (path.get(0).equals(path.get(1)))// checks to see if the ball and robo were somehow in same quadrant.
                    path.remove(1);
            }
        }else{
            if (!nearCross && !nearSide) {
                if (!pathIntersects(path.get(1), ballCoordinate))
                    path.add(2, ballCoordinate);
                else
                    path.add(ballCoordinate);
            }
        }

        //at this point in the method the robot will have gotten the path to the same quadrant as the ball.
        //if the ball is defined as easy to pick up, nothing more will happen after here.
        //if the ball is neear a side or in the area of cross, we will add one more point, to the path.

        if (nearCross){
            path.add(determineCoordinateForCrossPickup(ballCoordinate));
            path.add(ballCoordinate);
        }
        else if (nearSide){
            path.add(determineCoordinateForSidePickup(ballCoordinate));
            path.add(ballCoordinate);
        }

        if(path.isEmpty())
            return null;

        return path;
    }

    /**
     * We use this method to find which x or y coordinates are outside the inner field.
     * This is used for when we know a ball is near one of the side,
     * and we then get to know which side it is by comparing the robots x or y coordiantes,
     *  with one of these to know where we need to subtract or add to the waypoint.
     */
    private void determinePointsForSide(){
        if(fieldDetection.getFloorCorners()[0].x > fieldDetection.getFloorCorners()[3].x){
            leftSideX = fieldDetection.getFloorCorners()[0].x;
            rightSideX = fieldDetection.getFloorCorners()[1].x;
        }else {
            leftSideX = fieldDetection.getFloorCorners()[3].x;
            rightSideX = fieldDetection.getFloorCorners()[2].x;
        }

        if(fieldDetection.getFloorCorners()[0].y > fieldDetection.getFloorCorners()[1].y){
            upperSideY = fieldDetection.getFloorCorners()[0].y;
            lowerSideY = fieldDetection.getFloorCorners()[3].y;
        }else {
            upperSideY = fieldDetection.getFloorCorners()[1].y;
            lowerSideY = fieldDetection.getFloorCorners()[2].y;
        }
    }

    private Point determineCoordinateForSidePickup(Point ballCoordinate) {
        Point alignmentPoint = null;

        if(ballCoordinate.x < this.leftSideX)
            alignmentPoint = new Point(ballCoordinate.x + (15 * fieldDetection.getScaleFactor()), ballCoordinate.y);
        else if(ballCoordinate.x > this.rightSideX)
            alignmentPoint = new Point(ballCoordinate.x - (15 * fieldDetection.getScaleFactor()), ballCoordinate.y);
        else if(ballCoordinate.y < this.upperSideY)
            alignmentPoint = new Point(ballCoordinate.x, ballCoordinate.y + (15 * fieldDetection.getScaleFactor()));
        else
            alignmentPoint = new Point(ballCoordinate.x, ballCoordinate.y - (15 * fieldDetection.getScaleFactor()));

        return alignmentPoint;
    }

    private Point determineCoordinateForCrossPickup(Point ballCoordinate) {
        Vector vector = new Vector(fieldDetection.getRedCross().getCrossArea().getCenter(), ballCoordinate);
        Vector normVector = new Vector(vector.getX() / vector.getLength(), vector.getY() / vector.getLength());
        double extensionLength = 15 * fieldDetection.getScaleFactor();
        Vector extendedVector = new Vector(normVector.getX() * extensionLength, normVector.getY() * extensionLength);

        return new Point(fieldDetection.getRedCross().getCrossArea().getCenter().x + extendedVector.getX(),
                fieldDetection.getRedCross().getCrossArea().getCenter().y + extendedVector.getY());
    }

    private boolean determineIfRoboAndBallAreInOppositeQuadrant(String determineQuadrant, Point ballCoordinate, List<Point> path) {
        boolean opposite = false;

        switch (determineQuadrant){
            case "leftLower" -> { opposite = ballCoordinate.x >= fieldDetection.getRedCross().getCrossArea().getCenter().x &&
            ballCoordinate.y <= fieldDetection.getRedCross().getCrossArea().getCenter().y;
                if(opposite) {
                    path.add(leftUpperQuadrantWayPoint);
                    path.add(rightUpperQuadrantWayPoint);
                }
            }
            case "leftUpper" -> {opposite = ballCoordinate.x >= fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                    ballCoordinate.y >= fieldDetection.getRedCross().getCrossArea().getCenter().y;
                if(opposite) {
                    path.add(leftLowerQuadrantWayPoint);
                    path.add(rightLowerQuadrantWayPoint);
                }
            }
            case "rightLower" -> {opposite = ballCoordinate.x <= fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                    ballCoordinate.y <= fieldDetection.getRedCross().getCrossArea().getCenter().y;
                if(opposite) {
                    path.add(rightUpperQuadrantWayPoint);
                    path.add(leftUpperQuadrantWayPoint);
                }
            }
            case "rightUpper" -> {opposite = ballCoordinate.x <= fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                    ballCoordinate.y >= fieldDetection.getRedCross().getCrossArea().getCenter().y;
                if(opposite) {
                    path.add(rightLowerQuadrantWayPoint);
                    path.add(leftLowerQuadrantWayPoint);
                }
            }
        }
        return opposite;
    }

    /**
     * This method determines the position of the robot, and dds the first point to the new path.
     * @param roboCenter
     * @param path
     */
    private String determineQuadrant(Point roboCenter, List<Point> path) {
        if (fieldDetection.getRedCross().getCrossArea().getCenter().x < roboCenter.x){
            if (fieldDetection.getRedCross().getCrossArea().getCenter().y < roboCenter.y) { //right lower quadrant, with largest x and y values
                path.add(rightLowerQuadrantWayPoint);
                return "rightLower";
            }
            else {
                path.add(rightUpperQuadrantWayPoint);
                return "rightUpper";
            }
        }else{
            if(fieldDetection.getRedCross().getCrossArea().getCenter().y < roboCenter.y){
                path.add(leftLowerQuadrantWayPoint);
                return "leftLower";
            }
            else {
                path.add(leftUpperQuadrantWayPoint);
                return "leftUpper";
            }
        }
    }

    public boolean isNearCross(Point ballCoordinate){
        return fieldDetection.getRedCross().getCrossArea().isPointInside(ballCoordinate);
    }

    public boolean isAroundCross(Point ballCoordinate){
        return fieldDetection.getRedCross().getScalefactorAdjustedCrossArea().isPointInside(ballCoordinate);
    }

    public boolean goForBall(Point ballCoordinate){
        //checks if ball is in the corner or by the cross
        return !isInCorner(ballCoordinate) && !isNearCross(ballCoordinate);
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

    public List<Point> pathToGoal(Point roboPosition){
        List<Point> path = new ArrayList<>();

        if (roboPosition.x < fieldDetection.getRedCross().getCrossArea().getCenter().x){
            path.add(goalAlignmentPoint);
            path.add(fieldDetection.getGoals().get(0));
            return path;
        }else{
            path.add(new Point(roboPosition.x, rightLowerQuadrantWayPoint.y));
            if (roboPosition.y > goalAlignmentPoint.y){
                path.add(leftLowerQuadrantWayPoint);
            }else{
                path.add(leftUpperQuadrantWayPoint);
            }
        }
        path.add(goalAlignmentPoint);
        path.add(fieldDetection.getGoals().get(0));

        return path;
    }

    public Point getGoalPoint(){
        return fieldDetection.getGoals().get(0);
    }

    /**
     * This method returns true if the line that is created from the robot to the ball,
     * intersects with the equation of the circle representing the red cross obstacle.
     * @param ball target ball to pick up.
     * @param roboCenter The center of the robot.
     * @return false is the path does not intersect with the cross.
     */
    public boolean pathIntersects(Point ball, Point roboCenter) {
        //check if ball and robo are in same quadrant
        if (isInSameQuadrant(ball, roboCenter))
            return false;

        // Line equation y = mx + c
        double m = (ball.y - roboCenter.y) / (ball.x - roboCenter.x);
        double c = roboCenter.y - m * roboCenter.x;

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

    private boolean isInSameQuadrant(Point ballCoordinate, Point roboCenter){
        if (fieldDetection.getRedCross().getCrossArea().getCenter().x < roboCenter.x){
            if (fieldDetection.getRedCross().getCrossArea().getCenter().y < roboCenter.y) { //right lower quadrant, with largest x and y values
                return ballCoordinate.x > fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                        ballCoordinate.y > fieldDetection.getRedCross().getCrossArea().getCenter().y;
            }
            else {
                return ballCoordinate.x > fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                        ballCoordinate.y < fieldDetection.getRedCross().getCrossArea().getCenter().y;
            }
        }else{
            if(fieldDetection.getRedCross().getCrossArea().getCenter().y < roboCenter.y){ //leftLower
                return ballCoordinate.x < fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                        ballCoordinate.y > fieldDetection.getRedCross().getCrossArea().getCenter().y;
            }
            else {
                return ballCoordinate.x < fieldDetection.getRedCross().getCrossArea().getCenter().x &&
                        ballCoordinate.y < fieldDetection.getRedCross().getCrossArea().getCenter().y;
            }
        }
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


    /**
     * A single method to perform multiple checks to see if a Point is in a location that is easy to pick up
     * @param ballCoordinate the point variable to be tested
     * @return true if the ball is easy, false if the ball is difficult to pick up
     */
    public boolean isEasy(Point ballCoordinate){
        return (!isInCorner(ballCoordinate) && !isNearCross(ballCoordinate) && isBallInField(ballCoordinate));
    }

}
