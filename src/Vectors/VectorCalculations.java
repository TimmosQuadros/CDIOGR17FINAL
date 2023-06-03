package Vectors;

import LineCreation.LineSegment;
import org.opencv.core.Point;

public class VectorCalculations {

    private Vector vectorRobot;
    private Vector vectorBall;
    private Point robotCenter;
    private double cosAngle;

    public VectorCalculations(LineSegment robot, Point ballCoordinat){
        robotCenter = new Point ((robot.getEndPoint().x + robot.getEndPoint().x) / 2.0,
                (robot.getEndPoint().y + robot.getEndPoint().y) / 2.0);

        vectorRobot = new Vector(robot.getEndPoint().x - robotCenter.x, robot.getEndPoint().y - robotCenter.y);
        vectorBall = new Vector(ballCoordinat.x - robotCenter.x, ballCoordinat.y - robotCenter.y);

        calculateAngle();
    }

    private void calculateAngle(){
        cosAngle = (((vectorRobot.getX() * vectorBall.getX()) + (vectorRobot.getY() * vectorBall.getY())) /
                (((Math.sqrt(Math.pow(vectorRobot.getX(), 2) + Math.pow(vectorRobot.getY(), 2)))) *
                        ((Math.sqrt(Math.pow(vectorBall.getX(), 2) + Math.pow(vectorBall.getY(), 2))))));
    }

    public double getAngle(){
        double angleInRadians = Math.acos(cosAngle);

        return Math.toDegrees(angleInRadians);
    }

}
