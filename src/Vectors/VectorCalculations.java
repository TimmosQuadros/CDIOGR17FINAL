package Vectors;

import LineCreation.LineSegment;
import org.opencv.core.Point;

public class VectorCalculations {

    private Vector vectorOne;
    private Vector vectorTwo;
    private Point center;
    private Point pivotPoint;
    private double cosAngle;

    /**
     * This method will take a linesegment from the robot along with a ballcoordinat.
     * Based on the robots linesegment, we will calculate the center point of the robot.
     * We will then use this centerpoint to create two vectors, with their starting point in the center.
     * The first vector will be the vector representing the robots heading, hence we will use the frontpoint of the robot.
     * The next vector will be from the the center to the ball.
     * Having the same starting point of the vectors allows us to easily calculate the angle,
     * that the robot needs to turn in order to point towards the ball.
     * @param robot Linesegemnt.
     * @param ballCoordinat point.
     */
    public VectorCalculations(LineSegment robot, Point ballCoordinat){
        center = new Point ((robot.getEndPoint().x + robot.getStartPoint().x) / 2.0,
                (robot.getEndPoint().y + robot.getStartPoint().y) / 2.0);

        vectorOne = new Vector(robot.getEndPoint().x - center.x, robot.getEndPoint().y - center.y);
        vectorTwo = new Vector(ballCoordinat.x - center.x, ballCoordinat.y - center.y);

        calculateAngle();
    }

    public VectorCalculations(Point center, Point edge1, Point edge2){
        vectorOne = new Vector(edge1.x - center.x, edge1.y - center.y);
        vectorTwo = new Vector(edge2.x - center.x, edge2.y - center.y);

        Vector resultingVector = new Vector(vectorOne.getX() + vectorTwo.getX(), vectorOne.getY() + vectorTwo.getY());
        pivotPoint = new Point(center.x + resultingVector.getX(), center.y + resultingVector.getY());
    }

    private void calculateAngle(){
        cosAngle = (((vectorOne.getX() * vectorTwo.getX()) + (vectorOne.getY() * vectorTwo.getY())) /
                (((Math.sqrt(Math.pow(vectorOne.getX(), 2) + Math.pow(vectorOne.getY(), 2)))) *
                        ((Math.sqrt(Math.pow(vectorTwo.getX(), 2) + Math.pow(vectorTwo.getY(), 2))))));

        double crossProduct = crossProduct(vectorOne, vectorTwo);
        if (crossProduct < 0) {
            cosAngle = 360 - cosAngle;
        }
    }

    private double crossProduct(Vector vectorOne, Vector vectorTwo) {
        return (vectorOne.getX()) * vectorTwo.getY() - (vectorOne.getY()) * vectorOne.getX();
    }

    public double getAngle(){
        double angleInRadians = Math.acos(cosAngle);

        return Math.toDegrees(angleInRadians);
    }

}
