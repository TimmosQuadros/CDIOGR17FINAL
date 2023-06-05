package Vectors;

import LineCreation.LineSegment;
import org.opencv.core.Point;

public class VectorCalculations {

    private Vector vectorOne;
    private Vector vectorTwo;
    private Point center;
    private Point pivotPoint;
    private double cosAngle;

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
    }

    public double getAngle(){
        double angleInRadians = Math.acos(cosAngle);

        return Math.toDegrees(angleInRadians);
    }

}
